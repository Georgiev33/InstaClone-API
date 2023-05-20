package com.example.demo.model.exception;


public class StoryNotFoundException extends NotFoundException {
    public StoryNotFoundException(String message) {
        super("Story not found.");
    }
}
