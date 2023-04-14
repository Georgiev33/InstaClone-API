package com.example.demo.model.exception;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException(String message){
        super(message);
    }
}
