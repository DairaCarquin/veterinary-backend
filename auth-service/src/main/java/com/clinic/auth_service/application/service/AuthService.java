package com.clinic.auth_service.application.service;

import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.clinic.auth_service.application.dto.request.RegisterRequest;
import com.clinic.auth_service.application.dto.response.AuthResponse;
import com.clinic.auth_service.application.dto.response.RegisterResponse;
import com.clinic.auth_service.domain.event.UserCreatedEvent;
import com.clinic.auth_service.domain.model.User;
import com.clinic.auth_service.domain.port.UserRepositoryPort;
import com.clinic.auth_service.infrastructure.adapter.out.R2dbcRoleRepositoryAdapter;
import com.clinic.auth_service.infrastructure.config.JwtUtil;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepositoryPort userRepository;
        private final R2dbcRoleRepositoryAdapter roleRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;
        private final KafkaTemplate<String, Object> kafkaTemplate;

        public Mono<RegisterResponse> register(RegisterRequest request) {

                return roleRepository.findByName(request.getRole())
                .switchIfEmpty(
                        Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol inválido"))
                )
                .flatMap(role -> {

                        User user = User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .roleId(role.getId())
                                .build();

                        return userRepository.save(user)
                                .doOnSuccess(saved -> kafkaTemplate.send(
                                                "user-created-topic",
                                                new UserCreatedEvent(
                                                                saved.getId(),
                                                                saved.getUsername(),
                                                                role.getName())))
                                .map(saved -> RegisterResponse.builder()
                                                .id(saved.getId())
                                                .username(saved.getUsername())
                                                .role(role.getName())
                                                .build());
                });
        }

        public Mono<AuthResponse> login(String username, String password) {

                return userRepository.findByUsername(username)
                        .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                        .flatMap(user -> roleRepository.findById(user.getRoleId())
                                .map(role -> AuthResponse.builder()
                                        .id(user.getId())
                                        .username(user.getUsername())
                                        .role(role.getName())
                                        .accessToken(jwtUtil.generateAccessToken(user,
                                                        role.getName()))
                                        .refreshToken(jwtUtil.generateRefreshToken(user))
                                        .build()));
        }
}