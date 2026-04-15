package com.clinic.auth_service.application.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.auth_service.application.dto.request.RegisterRequest;
import com.clinic.auth_service.application.dto.response.AuthResponse;
import com.clinic.auth_service.application.dto.response.RegisterResponse;
import com.clinic.auth_service.application.dto.response.UserResponse;
import com.clinic.auth_service.domain.event.UserCreatedEvent;
import com.clinic.auth_service.domain.model.User;
import com.clinic.auth_service.domain.port.UserRepositoryPort;
import com.clinic.auth_service.infrastructure.adapter.out.R2dbcRoleRepository;
import com.clinic.auth_service.infrastructure.config.DuplicateFieldException;
import com.clinic.auth_service.infrastructure.config.JwtUtil;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepositoryPort userRepository;
    private final R2dbcRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public Mono<RegisterResponse> register(RegisterRequest request) {
        String username = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String normalizedRole = request.getRole().trim().toUpperCase(Locale.ROOT);
        String dni = request.getDni().trim();
        String phone = request.getPhone().trim();

        return validateUniqueFields(null, username, dni, phone)
                .then(Mono.defer(() -> roleRepository.findByName(normalizedRole)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Rol invalido")))
                        .flatMap(role -> {
                            User user = User.builder()
                                    .username(username)
                                    .password(passwordEncoder.encode(request.getPassword()))
                                    .roleId(role.getId())
                                    .dni(dni)
                                    .phone(phone)
                                    .enabled(true)
                                    .createdAt(LocalDateTime.now())
                                    .build();

                            return userRepository.save(user)
                                    .doOnSuccess(saved -> kafkaTemplate.send(
                                            "user-created-topic",
                                            new UserCreatedEvent(
                                                    saved.getId(),
                                                    saved.getUsername(),
                                                    role.getName(),
                                                    request.getFirstName().trim(),
                                                    request.getLastName().trim(),
                                                    dni,
                                                    username,
                                                    phone)))
                                    .map(saved -> RegisterResponse.builder()
                                            .id(saved.getId())
                                            .username(saved.getUsername())
                                            .role(role.getName())
                                            .build());
                        })));
    }

    public Mono<AuthResponse> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> Boolean.TRUE.equals(user.getEnabled()))
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .flatMap(user -> roleRepository.findById(user.getRoleId())
                        .map(role -> AuthResponse.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .role(role.getName())
                                .accessToken(jwtUtil.generateAccessToken(user, role.getName()))
                                .refreshToken(jwtUtil.generateRefreshToken(user))
                                .build()))
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales invalidas")));
    }

    public Flux<UserResponse> findAll(String username, Long roleId, int page, int size) {
        int offset = page * size;

        return userRepository.search(username, roleId, size, offset)
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .roleId(user.getRoleId())
                        .enabled(user.getEnabled())
                        .build());
    }

    public Mono<UserResponse> findById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado")))
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .roleId(user.getRoleId())
                        .enabled(user.getEnabled())
                        .build());
    }

    public Mono<User> update(Long id, RegisterRequest request) {
        String normalizedRole = request.getRole().trim().toUpperCase(Locale.ROOT);

        return roleRepository.findByName(normalizedRole)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Rol invalido")))
                .flatMap(role -> userRepository.findById(id)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Usuario no encontrado")))
                        .flatMap(user -> validateUniqueFields(user.getId(),
                                        request.getEmail().trim().toLowerCase(Locale.ROOT),
                                        request.getDni().trim(),
                                        request.getPhone().trim())
                                .then(Mono.defer(() -> {
                            user.setUsername(request.getEmail().trim().toLowerCase(Locale.ROOT));
                            user.setPassword(passwordEncoder.encode(request.getPassword()));
                            user.setRoleId(role.getId());
                            user.setDni(request.getDni().trim());
                            user.setPhone(request.getPhone().trim());
                            user.setUpdatedAt(LocalDateTime.now());
                            return userRepository.save(user);
                                }))));
    }

    public Mono<Void> toggleEnabled(Long id, boolean enabled) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado")))
                .flatMap(user -> {
                    user.setEnabled(enabled);
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .then();
    }

    public Mono<Long> countUsers() {
        return userRepository.countAll();
    }

    public Mono<Long> countUsers(String username, Long roleId) {
        return userRepository.countFiltered(username, roleId);
    }

    private Mono<Void> validateUniqueFields(Long currentUserId, String username, String dni, String phone) {
        return Mono.zip(
                        userRepository.findByUsername(username).defaultIfEmpty(User.builder().build()),
                        userRepository.findByDni(dni).defaultIfEmpty(User.builder().build()),
                        userRepository.findByPhone(phone).defaultIfEmpty(User.builder().build()))
                .flatMap(tuple -> {
                    Map<String, String> fieldErrors = new LinkedHashMap<>();

                    if (tuple.getT1().getId() != null && !tuple.getT1().getId().equals(currentUserId)) {
                        fieldErrors.put("email", "El email ya se encuentra registrado.");
                    }

                    if (tuple.getT2().getId() != null && !tuple.getT2().getId().equals(currentUserId)) {
                        fieldErrors.put("dni", "El DNI ya se encuentra registrado.");
                    }

                    if (tuple.getT3().getId() != null && !tuple.getT3().getId().equals(currentUserId)) {
                        fieldErrors.put("phone", "El telefono ya se encuentra registrado.");
                    }

                    if (!fieldErrors.isEmpty()) {
                        return Mono.error(new DuplicateFieldException("Ya existe un usuario con esos datos.", fieldErrors));
                    }

                    return Mono.empty();
                });
    }
}
