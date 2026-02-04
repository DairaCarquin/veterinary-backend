package com.vet.security.application.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vet.security.application.dto.request.LoginRequest;
import com.vet.security.application.dto.request.RefreshRequest;
import com.vet.security.application.dto.request.RegisterRequest;
import com.vet.security.application.dto.request.TokenValidationRequest;
import com.vet.security.application.dto.response.LoginResponse;
import com.vet.security.application.dto.response.RefreshResponse;
import com.vet.security.application.dto.response.TokenValidationResponse;
import com.vet.security.domain.port.in.AuthUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        return authUseCase.login(request);
    }

    @PostMapping("/register")
    public Long register(@RequestBody @Valid RegisterRequest request) {
        return authUseCase.register(request);
    }

    @PostMapping("/refresh")
    public RefreshResponse refresh(@RequestBody RefreshRequest request) {
        return authUseCase.refreshToken(request);
    }

    @PostMapping("/validate")
    public TokenValidationResponse validate(@RequestBody TokenValidationRequest request) {
        return authUseCase.validateToken(request.token());
    }
}
