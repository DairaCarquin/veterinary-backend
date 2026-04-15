package com.clinic.auth_service.infrastructure.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.security.Key;

import org.springframework.stereotype.Component;

import com.clinic.auth_service.domain.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final String SECRET = "super-secret-key-clinica-super-secret-key-clinica";
    private static final long ACCESS_TOKEN_EXPIRATION_MS = 28800000;
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 604800000;

    public String generateToken(User user, String roleName) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("scope", "ROLE_" + roleName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user, String roleName) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("username", user.getUsername())
                .claim("scope", "ROLE_" + roleName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
