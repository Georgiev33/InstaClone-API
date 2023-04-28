package com.example.demo.model.exception;

public class EmailAlreadyExist extends RuntimeException{

    public EmailAlreadyExist(String message){
        super(message);
    }
}
