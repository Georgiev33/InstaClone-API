package com.example.demo.model.exception;

public class ReportedUserAlreadyExist extends RuntimeException{
    public ReportedUserAlreadyExist(String message){
        super(message);
    }

}
