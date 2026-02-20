package com.vet.security.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.vet.security.application.dto.response.TokenValidationResponse;
import com.vet.security.domain.exception.model.Role;
import com.vet.security.domain.exception.model.User;
import com.vet.security.domain.port.out.TokenProviderPort;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider implements TokenProviderPort {

    private static final String SECRET = "super-secret-key-very-long-super-secret-key";
    private static final long EXPIRATION = 900_000;

    @Override
    public String generateAccessToken(User user) {

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("roles",
                        user.getRoles().stream()
                                .map(Role::getName)
                                .toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(
                        Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public TokenValidationResponse parseToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = Long.valueOf(claims.getSubject());
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles", List.class);

            return new TokenValidationResponse(true, userId, roles);

        } catch (Exception e) {
            return new TokenValidationResponse(false, null, List.of());
        }
    }
}
