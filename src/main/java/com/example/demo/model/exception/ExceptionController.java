package com.example.demo.model.exception;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;


@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {BadRequestException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    private ExceptionDTO handleBadRequest(Exception ex){
        ExceptionDTO errorDTO = new ExceptionDTO();
        errorDTO.setMessage(ex.getMessage());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setTime(LocalDateTime.now());
        return errorDTO;
    }


    @ExceptionHandler(value = {NotFoundException.class, UsernameNotFoundException.class})
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    private ExceptionDTO handleNotFound(Exception ex){
        ExceptionDTO errorDTO = new ExceptionDTO();
        errorDTO.setMessage(ex.getMessage());
        errorDTO.setStatus(HttpStatus.NOT_FOUND.value());
        errorDTO.setTime(LocalDateTime.now());
        return errorDTO;
    }

    @ExceptionHandler(value = {UnauthorizedException.class, InvalidJwtTokenException.class})
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    private ExceptionDTO handleUnauthorized(Exception ex){
        ExceptionDTO errorDTO = new ExceptionDTO();
        errorDTO.setMessage(ex.getMessage());
        errorDTO.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorDTO.setTime(LocalDateTime.now());
        return errorDTO;
    }
}
