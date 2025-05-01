package com.mysociety.authentication.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

public class TokenGenerator {

    public static String generateUUIDToken(){
        return UUID.randomUUID().toString();
    }

}
