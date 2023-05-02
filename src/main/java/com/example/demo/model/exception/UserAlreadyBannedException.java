package com.example.demo.model.exception;

public class UserAlreadyBannedException extends RuntimeException{
    public UserAlreadyBannedException(String message){
        super(message);
    }
}
