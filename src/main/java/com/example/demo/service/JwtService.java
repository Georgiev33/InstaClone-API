package com.example.demo.service;

import com.example.demo.model.entity.BlackListToken;
import com.example.demo.model.exception.BadRequestException;
import com.example.demo.repository.BlackListedTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.example.demo.util.Constants.TOKEN_MISSING_DATA;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final BlackListedTokenRepository blackListedTokenRepository;
    private static final String SECRET_KEY = "25432A462D4A614E645267556B58703273357638782F413F4428472B4B625065";

    public void invalidateToken(String token) {
        blackListedTokenRepository.save(new BlackListToken(token.substring(7)));
    }

    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return blackListedTokenRepository.getBlackListTokensByToken(token).isEmpty()
                && userDetails.getUsername().equals(username)
                && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public long extractUserId(String authToken) {
        String jwtToken = authToken.substring(7);
        Claims claims = extractAllClaims(jwtToken);
        if (claims.get("USER_ID", Long.class) == null) {
            throw new BadRequestException(TOKEN_MISSING_DATA);
        }
        return claims.get("USER_ID", Long.class);
    }
}