package com.vet.pet.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private static final String SECRET = "super-secret-key-very-long-super-secret-key";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }

    public Long getUserId(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                return Long.valueOf(claims.getSubject());
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                return (List<String>) claims.get("roles", List.class);
            }
        } catch (Exception e) {
            return List.of();
        }
        return List.of();
    }

}
