package com.vet.security.application.service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vet.security.application.dto.request.LoginRequest;
import com.vet.security.application.dto.request.RefreshRequest;
import com.vet.security.application.dto.request.RegisterRequest;
import com.vet.security.application.dto.response.LoginResponse;
import com.vet.security.application.dto.response.RefreshResponse;
import com.vet.security.application.dto.response.TokenValidationResponse;
import com.vet.security.domain.exception.BadRequestException;
import com.vet.security.domain.exception.ErrorCode;
import com.vet.security.domain.exception.model.RefreshToken;
import com.vet.security.domain.exception.model.Role;
import com.vet.security.domain.exception.model.User;
import com.vet.security.domain.port.in.AuthUseCase;
import com.vet.security.domain.port.out.RefreshTokenRepositoryPort;
import com.vet.security.domain.port.out.RoleRepositoryPort;
import com.vet.security.domain.port.out.TokenProviderPort;
import com.vet.security.domain.port.out.UserEventPublisherPort;
import com.vet.security.domain.port.out.UserRepositoryPort;
import com.vet.security.infrastructure.kafka.AuthEventProducer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AuthUseCaseImpl.class);

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final TokenProviderPort tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthEventProducer authEventProducer;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for user: {}", request.username());

        // Buscar usuario
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadRequestException(ErrorCode.AUTH_003));

        // Validar status = 1 (activo)
        if (user.getStatus() != 1) {
            logger.warn("Login failed for user {} - status != 1", request.username());
            throw new BadRequestException(
                ErrorCode.AUTH_003,
                "Usuario inactivo (status != 1)"
            );
        }

        // Validar enabled = true
        if (!user.isEnabled()) {
            logger.warn("Login failed for user {} - not enabled", request.username());
            throw new BadRequestException(ErrorCode.AUTH_004);
        }

        // Validar password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            logger.warn("Login failed for user {} - invalid password", request.username());
            throw new BadRequestException(ErrorCode.AUTH_003);
        }

        // Generar tokens
        String accessToken = tokenProvider.generateAccessToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserId(user.getId());
        refreshToken.setExpiresAt(Instant.now().plusSeconds(86400)); // 24 horas
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        logger.info("Login successful for user: {}", request.username());

        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                900, // 15 minutos
                user.getRoles().stream().map(Role::getName).toList());
    }

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        logger.info("Registering new user: {}", request.username());

        // Validar username único
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException(ErrorCode.USER_001);
        }

        // Validar password mínimo 8 caracteres
        if (request.password() == null || request.password().length() < 8) {
            throw new BadRequestException(ErrorCode.USER_005);
        }

        // Obtener roles
        Set<Role> roles = request.roles().stream()
                .map(roleRepository::findByName)
                .collect(java.util.stream.Collectors.toSet());

        if (roles.isEmpty()) {
            throw new BadRequestException(ErrorCode.ROLE_001);
        }

        // Crear usuario con status = 1 (activo) y enabled = true
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .status(1) // 1 = activo
                .roles(roles)
                .createdAt(Instant.now())
                .build();

        User savedUser = userRepository.save(user);

        // Emitir evento USER_CREATED
        authEventProducer.publishUserCreated(savedUser);

        logger.info("User registered successfully: {}", savedUser.getId());
    }

    @Override
    @Transactional
    public RefreshResponse refreshToken(RefreshRequest request) {
        logger.info("Refresh token request");

        RefreshToken token = refreshTokenRepository.findByToken(request.refreshToken());

        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            logger.warn("Refresh token invalid or expired");
            throw new BadRequestException(ErrorCode.AUTH_002);
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new BadRequestException(ErrorCode.USER_002));

        // Validar que el usuario esté activo
        if (user.getStatus() != 1 || !user.isEnabled()) {
            logger.warn("Refresh token denied for inactive user: {}", user.getId());
            throw new BadRequestException(ErrorCode.AUTH_004);
        }

        String newAccessToken = tokenProvider.generateAccessToken(user);

        logger.info("Refresh token successful for user: {}", user.getId());

        return new RefreshResponse(newAccessToken, 900);
    }

    @Override
    public TokenValidationResponse validateToken(String token) {
        boolean valid = tokenProvider.validateToken(token);

        if (!valid) {
            return new TokenValidationResponse(false, null, null);
        }

        return tokenProvider.parseToken(token);
    }
}
