package com.mysociety.authentication.utils;

import java.util.Date;

public class Constants {

    public static long getOtpExpirationTime(){
        //15 minutes
        return (60 * 15 * 1000);
    }
    public static int MAX_OTP_ATTEMPT = 3;
}
