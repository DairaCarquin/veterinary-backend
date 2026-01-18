package com.vet.security.domain.port.in;

import com.vet.security.application.dto.request.LoginRequest;
import com.vet.security.application.dto.request.RefreshRequest;
import com.vet.security.application.dto.request.RegisterRequest;
import com.vet.security.application.dto.response.LoginResponse;
import com.vet.security.application.dto.response.RefreshResponse;
import com.vet.security.application.dto.response.TokenValidationResponse;

public interface AuthUseCase {

    LoginResponse login(LoginRequest request);

    void register(RegisterRequest request);

    RefreshResponse refreshToken(RefreshRequest request);

    TokenValidationResponse validateToken(String token);
}