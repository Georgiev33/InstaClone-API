package com.example.demo.model.exception;

public class UserAlreadyBannedException extends BadRequestException{
    public UserAlreadyBannedException(String message){
        super(message);
    }
}
