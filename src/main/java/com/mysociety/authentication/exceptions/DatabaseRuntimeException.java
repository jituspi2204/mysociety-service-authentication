package com.mysociety.authentication.exceptions;

public class DatabaseRuntimeException extends RuntimeException{
    public DatabaseRuntimeException(String message){
        super(message);
    }
}
