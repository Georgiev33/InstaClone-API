package com.example.demo.model.exception;

public class PasswordMismatchException extends RuntimeException {
    public PasswordMismatchException(String message){
        super(message);
    }
}
