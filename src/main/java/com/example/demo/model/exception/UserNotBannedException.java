package com.example.demo.model.exception;

public class UserNotBannedException extends BadRequestException{
    public UserNotBannedException(String message){
        super(message);
    }
}
