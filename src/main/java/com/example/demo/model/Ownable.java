package com.example.demo.model;

import com.example.demo.model.exception.InvalidOwnerException;

public interface Ownable {
    void verifyOwnerIdOrThrow(long userId) throws InvalidOwnerException;
}
