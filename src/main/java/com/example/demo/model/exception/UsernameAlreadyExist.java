package com.example.demo.model.exception;

public class UsernameAlreadyExist extends RuntimeException{

    public UsernameAlreadyExist(String message){
        super(message);
    }
}
