package com.example.bookportal.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String DEFAULT_SECRET = "9f3c7d4a1b8e6c2f5a9d7e3b4c1f8a6d2e7c9b5a3f1d6e8c4b7a2f9d1c6e3b8";
    private static final long DEFAULT_EXPIRATION_MS = 3600000L;

    private final Key key;
    private final long expiration;

    /**
     * Constructs a JwtUtil with the provided secret and expiration.
     * 
     * @param secret     the JWT secret
     * @param expiration the expiration time in milliseconds
     */
    public JwtUtil(@Value("${security.jwt.secret:${JWT_SECRET:" + DEFAULT_SECRET + "}}") String secret,
            @Value("${security.jwt.expiration-ms:${JWT_EXPIRATION_MS:" + DEFAULT_EXPIRATION_MS
                    + "}}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * Generates a JWT token for the given user ID.
     * 
     * @param userId the user ID
     * @return the generated JWT token
     */
    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts claims from the given JWT token.
     * 
     * @param token the JWT token
     * @return the extracted Claims
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the user ID from the given JWT token.
     * 
     * @param token the JWT token
     * @return the extracted user ID
     */
    public Long extractUserId(String token) {
        return Long.parseLong(extractClaims(token).getSubject());
    }

    /**
     * Validates the given JWT token.
     * 
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            extractUserId(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}
