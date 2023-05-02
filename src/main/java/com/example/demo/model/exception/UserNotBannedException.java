package com.example.demo.model.exception;

public class UserNotBannedException extends RuntimeException{
    public UserNotBannedException(String message){
        super(message);
    }
}
