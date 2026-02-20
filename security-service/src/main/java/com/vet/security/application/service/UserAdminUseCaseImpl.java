package com.vet.security.application.service;

import com.vet.security.application.dto.request.CreateUserRequest;
import com.vet.security.application.dto.request.UpdatePasswordRequest;
import com.vet.security.application.dto.request.UpdateUserRequest;
import com.vet.security.domain.exception.BadRequestException;
import com.vet.security.domain.exception.ConflictException;
import com.vet.security.domain.exception.ErrorCode;
import com.vet.security.domain.exception.ForbiddenException;
import com.vet.security.domain.exception.NotFoundException;
import com.vet.security.domain.exception.model.Role;
import com.vet.security.domain.exception.model.User;
import com.vet.security.domain.port.in.UserAdminUseCase;
import com.vet.security.domain.port.out.RefreshTokenRepositoryPort;
import com.vet.security.domain.port.out.RoleRepositoryPort;
import com.vet.security.domain.port.out.UserRepositoryPort;
import com.vet.security.infrastructure.client.ClientServiceClient;
import com.vet.security.infrastructure.kafka.AuthEventProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserAdminUseCaseImpl implements UserAdminUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(UserAdminUseCaseImpl.class);
    
    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final AuthEventProducer authEventProducer;
    private final ClientServiceClient clientServiceClient;

    @Override
    @Transactional
    public User createUser(CreateUserRequest request) {
        logger.info("Creating user: {}", request.username());
        
        // Validar datos obligatorios
        if (request.username() == null || request.username().isBlank()) {
            throw new BadRequestException(ErrorCode.USER_003);
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new BadRequestException(ErrorCode.USER_004);
        }
        
        // Validar username único
        if (userRepositoryPort.existsByUsername(request.username())) {
            throw new ConflictException(ErrorCode.USER_001);
        }
        
        // Validar longitud de password
        if (request.password().length() < 8) {
            throw new BadRequestException(ErrorCode.USER_005);
        }
        
        // Validar que los roles existan
        Set<Role> roles = roleRepositoryPort.findByNames(request.roles());
        if (roles.size() != request.roles().size()) {
            throw new BadRequestException(ErrorCode.ROLE_001);
        }

        // Crear usuario con status = 1 (activo)
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .roles(roles)
                .enabled(request.enabled() != null ? request.enabled() : true)
                .status(1) // 1 = activo
                .createdAt(Instant.now())
                .updatedAt(null)
                .build();
        
        User savedUser = userRepositoryPort.save(user);
        
        // Publicar evento Kafka USER_CREATED
        authEventProducer.publishUserCreated(savedUser);
        
        logger.info("User created successfully: {}", savedUser.getId());
        return savedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> listUsers(String username, Boolean enabled, String role, Pageable pageable) {
        logger.info("Listing users with filters - username: {}, enabled: {}, role: {}", username, enabled, role);
        return userRepositoryPort.findAllFiltered(username, enabled, role, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id, Optional<Long> requesterId, boolean isAdmin) {
        logger.info("Getting user by id: {}", id);
        
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_002));
        
        // Si no es admin, solo puede ver su propio perfil
        if (!isAdmin && requesterId.isPresent() && !requesterId.get().equals(id)) {
            throw new ForbiddenException(ErrorCode.AUTH_001);
        }
        
        return user;
    }

    @Override
    @Transactional
    public User updateUser(Long id, UpdateUserRequest request) {
        logger.info("Updating user: {}", id);
        
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_002));
        
        boolean rolesChanged = false;
        boolean wasEnabled = user.isEnabled();
        
        // Actualizar roles si se proporcionan
        if (request.roles() != null && !request.roles().isEmpty()) {
            Set<Role> newRoles = roleRepositoryPort.findByNames(request.roles());
            if (newRoles.size() != request.roles().size()) {
                throw new BadRequestException(ErrorCode.ROLE_001);
            }
            
            if (!user.getRoles().equals(newRoles)) {
                rolesChanged = true;
                user.setRoles(newRoles);
            }
        }
        
        // Actualizar enabled si se proporciona
        if (request.enabled() != null) {
            user.setEnabled(request.enabled());
        }
        
        // Actualizar timestamp
        user.setUpdatedAt(Instant.now());
        
        User updatedUser = userRepositoryPort.save(user);
        
        // Publicar eventos según los cambios
        authEventProducer.publishUserUpdated(updatedUser);
        
        if (rolesChanged) {
            authEventProducer.publishRoleAssigned(updatedUser);
        }
        
        if (wasEnabled && !updatedUser.isEnabled()) {
            authEventProducer.publishUserDisabled(updatedUser);
            // Invalidar refresh tokens activos
            refreshTokenRepositoryPort.deleteByUserId(id);
            logger.info("Refresh tokens invalidated for user: {}", id);
        }
        
        logger.info("User updated successfully: {}", id);
        return updatedUser;
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        logger.info("Disabling user (logical delete): {}", id);
        
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_002));
        
        // Borrado lógico completo
        user.setEnabled(false);
        user.setStatus(0); // 0 = inactivo
        user.setUpdatedAt(Instant.now());
        userRepositoryPort.save(user);
        
        // Invalidar refresh tokens activos
        refreshTokenRepositoryPort.deleteByUserId(id);
        
        // Publicar evento USER_DISABLED
        authEventProducer.publishUserDisabled(user);
        
        logger.info("User disabled successfully (status=0, enabled=false): {}", id);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        logger.info("Updating password for user: {}", userId);
        
        // 1. Validar existencia del usuario
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_002));
        
        // Validar status = 1 (activo)
        if (user.getStatus() != 1) {
            throw new BadRequestException(
                ErrorCode.GEN_002,
                "Usuario inactivo (status != 1)"
            );
        }
        
        // Validar enabled = true
        if (!user.isEnabled()) {
            throw new BadRequestException(
                ErrorCode.AUTH_004,
                "Usuario deshabilitado"
            );
        }
        
        // 2. Validar contraseña actual
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadRequestException(
                ErrorCode.GEN_002,
                "Contraseña actual incorrecta"
            );
        }
        
        // 3. Validar nueva contraseña
        if (request.newPassword().length() < 8) {
            throw new BadRequestException(
                ErrorCode.USER_005,
                "Nueva contraseña debe tener mínimo 8 caracteres"
            );
        }
        
        // Validar que no sea igual a la anterior
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BadRequestException(
                ErrorCode.GEN_002,
                "Nueva contraseña no puede ser igual a la actual"
            );
        }
        
        // 4. Identificar rol del usuario
        boolean isCliente = user.getRoles().stream()
                .anyMatch(role -> "ROLE_CLIENTE".equals(role.getName()));
        
        // 5. Si es CLIENTE y tiene newDni, actualizar en client-service
        if (isCliente && request.newDni() != null && !request.newDni().isBlank()) {
            logger.info("User has CLIENTE role, updating DNI in client-service");
            
            // Validar que newDni no esté vacío
            if (request.newDni().isBlank()) {
                throw new BadRequestException(
                    ErrorCode.GEN_002,
                    "DNI no puede estar vacío para usuarios CLIENTE"
                );
            }
            
            // Comunicar con client-service
            clientServiceClient.updateClientDni(userId, request.newDni());
        }
        
        // 6. Actualizar contraseña
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setUpdatedAt(Instant.now());
        userRepositoryPort.save(user);
        
        logger.info("Password updated successfully for user: {}", userId);
    }
}
