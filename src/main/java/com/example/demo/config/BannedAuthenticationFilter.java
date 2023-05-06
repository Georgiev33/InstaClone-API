package com.example.demo.config;

import com.example.demo.model.exception.ExceptionController;
import com.example.demo.service.contracts.JwtService;
import com.example.demo.service.contracts.UserValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final String authHeader = request.getHeader(AUTHORIZATION);

        if (authentication != null && authentication.isAuthenticated()) {
            if(validationService.isUserBanned(jwtService.extractUserId(authHeader))){
                SecurityContextHolder.clearContext();
                ExceptionController.response(response, HttpStatus.FORBIDDEN, "You are banned from accessing this resource");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
