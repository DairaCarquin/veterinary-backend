package com.clinic.auth_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private Long id;
    private String username;
    private String role;
    private String accessToken;
    private String refreshToken;
}