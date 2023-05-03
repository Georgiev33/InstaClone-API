package com.example.demo.model.exception;

public class UserNotFoundException extends NotFoundException{
    public UserNotFoundException(String message){
        super(message);
    }
}
