// src/main/java/com/mentorlink/security/jwt/JwtTokenProvider.java
package com.mentorlink.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final long validityInMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms:86400000}") long validityInMs
    ) {
        // HS256 requires a 256-bit (32+ chars) secret
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityInMs = validityInMs;
    }

    // ✅ Generate JWT with roles (with ROLE_ prefix intact)
    public String generate(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles) // e.g. ["ROLE_ADMIN"]
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityInMs))
                .signWith(key)
                .compact();
    }

    // ✅ Extract username (email)
    public String getUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    // ✅ Extract roles
    public List<String> getRoles(String token) {
        return parseClaims(token).getBody().get("roles", List.class);
    }

    // ✅ Validate token
    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
}
