package com.example.demo.model.exception;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
    //TODO set type

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            org.springframework.web.bind.MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorMessages.add(fieldError.getDefaultMessage());
        }
        for (ObjectError objectError : ex.getBindingResult().getGlobalErrors()) {
            errorMessages.add(objectError.getDefaultMessage());
        }
        String combinedErrorMessage = String.join("; ", errorMessages);
        return new ResponseEntity<>(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, combinedErrorMessage),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {BadRequestException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ProblemDetail handleBadRequest(Exception ex) {
        System.out.println(ex.getMessage());
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

    @ExceptionHandler(value = {BannedUserException.class, UserNotVerifiedException.class})
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    private ProblemDetail handlePermissionDenied(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    }
}
