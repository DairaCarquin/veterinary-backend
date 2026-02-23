package com.clinic.auth_service.infrastructure.adapter.in;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.clinic.auth_service.application.dto.request.RegisterRequest;
import com.clinic.auth_service.application.dto.response.ApiResponse;
import com.clinic.auth_service.application.dto.response.AuthResponse;
import com.clinic.auth_service.application.dto.response.RegisterResponse;
import com.clinic.auth_service.application.service.AuthService;

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
}