package com.vet.security.application.service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.vet.security.application.dto.request.LoginRequest;
import com.vet.security.application.dto.request.RefreshRequest;
import com.vet.security.application.dto.request.RegisterRequest;
import com.vet.security.application.dto.response.LoginResponse;
import com.vet.security.application.dto.response.RefreshResponse;
import com.vet.security.application.dto.response.TokenValidationResponse;
import com.vet.security.domain.model.RefreshToken;
import com.vet.security.domain.model.Role;
import com.vet.security.domain.model.User;
import com.vet.security.domain.port.in.AuthUseCase;
import com.vet.security.domain.port.out.RefreshTokenRepositoryPort;
import com.vet.security.domain.port.out.RoleRepositoryPort;
import com.vet.security.domain.port.out.TokenProviderPort;
import com.vet.security.domain.port.out.UserRepositoryPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final TokenProviderPort tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.username());

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String accessToken = tokenProvider.generateAccessToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserId(user.getId());
        refreshToken.setExpiresAt(Instant.now().plusSeconds(86400));
        refreshToken.setRevoked(false);

        refreshTokenRepository.save(refreshToken);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                900,
                user.getRoles().stream().map(Role::getName).toList());
    }

    @Override
    public void register(RegisterRequest request) {

        Set<Role> roles = request.roles().stream()
                .map(roleRepository::findByName)
                .collect(java.util.stream.Collectors.toSet());

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEnabled(true);
        user.setRoles(roles);
        user.setCreatedAt(Instant.now());

        userRepository.save(user);
    }

    @Override
    public RefreshResponse refreshToken(RefreshRequest request) {

        RefreshToken token = refreshTokenRepository.findByToken(request.refreshToken());

        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token inválido");
        }

        User user = userRepository.findById(token.getUserId());

        return new RefreshResponse(
                tokenProvider.generateAccessToken(user),
                900);
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
