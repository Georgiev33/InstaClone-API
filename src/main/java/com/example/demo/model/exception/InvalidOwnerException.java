package com.example.demo.model.exception;

public class InvalidOwnerException extends BadRequestException{
    public InvalidOwnerException(String message) {
        super(message);
    }
}
