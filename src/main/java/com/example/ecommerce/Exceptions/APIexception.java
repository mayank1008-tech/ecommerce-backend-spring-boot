package com.example.ecommerce.Exceptions;

public class APIexception extends RuntimeException{

    public APIexception() {};
    public APIexception(String message) {
        super(message);
    }
}
