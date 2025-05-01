package com.mysociety.authentication.utils;

public class Validator {

    public static boolean isValidPhoneNumber(String phoneNumber){
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return false;
        }
        return phoneNumber.matches("\\d{10}");
    }
}
