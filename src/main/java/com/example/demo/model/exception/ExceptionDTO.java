package com.example.demo.model.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Setter
@Getter
public class ExceptionDTO {
    private String message;
    private int status;
    private LocalDateTime time;
}

