package com.example.demo.model.exception;

public class InvalidValidationCode extends BadRequestException{
    public InvalidValidationCode(String message){
        super(message);
    }
}
