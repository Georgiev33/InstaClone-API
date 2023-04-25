package com.example.demo.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice

public class ExceptionController extends ResponseEntityExceptionHandler {
    //TODO set type
    @ExceptionHandler(value = {BadRequestException.class, InvalidMultipartFileException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ProblemDetail handleBadRequest(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler(value = {NotFoundException.class, UsernameNotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ProblemDetail handleNotFound(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    private ProblemDetail handleAccessDenied(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }
}
