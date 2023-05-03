package com.example.demo.model.exception;

public class UsernameAlreadyExist extends BadRequestException{

    public UsernameAlreadyExist(String message){
        super(message);
    }
}
