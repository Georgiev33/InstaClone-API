package com.example.demo.model.exception;

public class InvalidMultipartFileException extends RuntimeException{
    public InvalidMultipartFileException(String message){
        super(message);
    }
}
