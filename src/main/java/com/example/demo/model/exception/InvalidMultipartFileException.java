package com.example.demo.model.exception;

public class InvalidMultipartFileException extends BadRequestException{
    public InvalidMultipartFileException(String message){
        super(message);
    }
}
