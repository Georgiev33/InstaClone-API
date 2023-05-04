//package com.example.demo.config;
//
//import com.example.demo.model.exception.ExceptionController;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.http.ProblemDetail;
//import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//
//@Component
//@RequiredArgsConstructor
//public class ExceptionHandlingFilter extends OncePerRequestFilter {
//    private final ExceptionController exceptionController;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
//        try {
//            filterChain.doFilter(request, response);
//        } catch (MethodArgumentNotValidException | ServletException ex) {
//            ProblemDetail problemDetail = exceptionController.handleBadRequest(ex);
//            response.setStatus(problemDetail.getStatus());
//            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
//            System.out.println("test");
//            ObjectMapper objectMapper = new ObjectMapper();
//            objectMapper.writeValue(response.getOutputStream(), problemDetail);
//        }
//    }
//}
