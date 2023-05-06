package com.example.demo.model.exception;

public class InvalidFileTypeException extends BadRequestException{
    public InvalidFileTypeException(String message) {
        super(message);
    }
}
