package com.example.demo.model.exception;

public class PasswordMismatchException extends BadRequestException {
    public PasswordMismatchException(String message){
        super(message);
    }
}
