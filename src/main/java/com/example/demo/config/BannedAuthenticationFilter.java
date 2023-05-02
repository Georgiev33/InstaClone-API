package com.example.demo.config;

import com.example.demo.service.JwtService;
import com.example.demo.service.contracts.UserValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class BannedAuthenticationFilter extends OncePerRequestFilter {
    private final UserValidationService validationService;
    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null) {
            filterChain.doFilter(request, response);
        }
        else {
            validationService.isUserBanned(jwtService.extractUserId(authHeader));
        }
    }
}
