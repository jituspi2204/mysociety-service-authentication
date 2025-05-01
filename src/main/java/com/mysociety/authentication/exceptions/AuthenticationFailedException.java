package com.mysociety.authentication.exceptions;

public class AuthenticationFailedException extends RuntimeException {
    public AuthenticationFailedException(String message){
        super(message);
    }

}
