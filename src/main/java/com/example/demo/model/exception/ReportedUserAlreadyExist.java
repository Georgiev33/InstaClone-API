package com.example.demo.model.exception;

public class ReportedUserAlreadyExist extends BadRequestException{
    public ReportedUserAlreadyExist(String message){
        super(message);
    }

}
