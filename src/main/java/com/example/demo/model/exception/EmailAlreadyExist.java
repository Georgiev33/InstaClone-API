package com.example.demo.model.exception;

public class EmailAlreadyExist extends BadRequestException{

    public EmailAlreadyExist(String message){
        super(message);
    }
}
