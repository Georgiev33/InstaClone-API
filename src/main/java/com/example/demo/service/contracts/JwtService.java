package com.example.demo.service.contracts;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    void invalidateToken(String token);

    String extractUsername(String jwtToken) throws ExpiredJwtException;

    String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    );

    boolean isTokenValid(String token, UserDetails userDetails);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    long extractUserId(String authToken);
}
