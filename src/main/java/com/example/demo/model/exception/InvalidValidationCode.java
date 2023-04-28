package com.example.demo.model.exception;

public class InvalidValidationCode extends RuntimeException{
    public InvalidValidationCode(String message){
        super(message);
    }
}
