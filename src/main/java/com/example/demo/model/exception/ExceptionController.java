package com.example.demo.model.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
    //TODO set type

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            org.springframework.web.bind.MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String validationErrorMessage = extractValidationErrorMessage(ex);
        return new ResponseEntity<>(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, validationErrorMessage),
                HttpStatus.BAD_REQUEST);
    }
    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String validationErrorMessage = extractValidationErrorMessage(ex);
        return new ResponseEntity<>(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, validationErrorMessage),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BadRequestException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBadRequest(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(value = {NotFoundException.class, UsernameNotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ProblemDetail handleNotFound(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(value = {AccessDeniedException.class, AuthenticationException.class})
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    private ProblemDetail handleAccessDenied(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(value = {BannedUserException.class, UserNotVerifiedException.class, ExpiredJwtException.class})
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    private ProblemDetail handlePermissionDenied(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    public static void response(HttpServletResponse response, HttpStatus statusCode, String errorMessage) throws IOException {
        response.setContentType("application/json");
        response.setStatus(statusCode.value());
        String json =
                "    \"type\": \"about:blank\",\n" +
                        "    \"title:\": \"" + statusCode.name() + "\",\n" +
                        "    \"status\": \"" + statusCode.value() + "\",\n" +
                        "    \"detail\": \"" + errorMessage + "\",\n";
        response.getWriter().write(json);
    }
    private String extractValidationErrorMessage(BindException ex) {
        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorMessages.add(fieldError.getDefaultMessage());
        }
        for (ObjectError objectError : ex.getBindingResult().getGlobalErrors()) {
            errorMessages.add(objectError.getDefaultMessage());
        }
        return String.join("; ", errorMessages);
    }
}
