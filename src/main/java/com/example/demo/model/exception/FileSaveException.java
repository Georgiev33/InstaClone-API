package com.example.demo.model.exception;

public class FileSaveException extends BadRequestException{
    public FileSaveException(String message) {
        super(message);
    }
}
