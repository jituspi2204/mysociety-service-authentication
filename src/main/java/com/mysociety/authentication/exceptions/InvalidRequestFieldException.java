package com.mysociety.authentication.exceptions;

public class InvalidRequestFieldException extends RuntimeException{

    public InvalidRequestFieldException(String message){
        super(message);
    }
}
