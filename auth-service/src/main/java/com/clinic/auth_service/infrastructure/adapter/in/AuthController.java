package com.clinic.auth_service.infrastructure.adapter.in;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.auth_service.application.dto.request.RegisterRequest;
import com.clinic.auth_service.application.dto.response.ApiResponse;
import com.clinic.auth_service.application.dto.response.AuthResponse;
import com.clinic.auth_service.application.dto.response.RegisterResponse;
import com.clinic.auth_service.application.service.AuthService;
import com.clinic.auth_service.domain.model.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @PostMapping("/register")
        public Mono<ApiResponse<RegisterResponse>> register(
                        @RequestBody RegisterRequest request) {

                return authService.register(request)
                                .map(response -> ApiResponse.<RegisterResponse>builder()
                                                .status(201)
                                                .description("Usuario registrado correctamente")
                                                .data(response)
                                                .build());
        }

        @PostMapping("/login")
        public Mono<ApiResponse<AuthResponse>> login(@RequestBody Map<String, String> body) {

                return authService.login(body.get("username"), body.get("password"))
                                .map(response -> ApiResponse.<AuthResponse>builder()
                                                .status(200)
                                                .description("Login exitoso")
                                                .data(response)
                                                .build());
        }

        @GetMapping("/users")
        public Mono<Map<String, Object>> listUsers(
                        @RequestParam(required = false) String username,
                        @RequestParam(required = false) Long roleId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                return authService.findAll(username, roleId, page, size)
                                .collectList()
                                .zipWith(authService.countUsers())
                                .map(tuple -> {
                                        Map<String, Object> response = new HashMap<>();
                                        response.put("data", tuple.getT1());
                                        response.put("total", tuple.getT2());
                                        response.put("page", page);
                                        response.put("size", size);
                                        return response;
                                });
        }

        @PutMapping("/users/{id}")
        public Mono<User> update(@PathVariable Long id,
                        @RequestBody RegisterRequest request) {
                return authService.update(id, request);
        }

        @PatchMapping("/users/{id}/status")
        public Mono<Void> toggle(@PathVariable Long id,
                        @RequestParam boolean enabled) {
                return authService.toggleEnabled(id, enabled);
        }
}