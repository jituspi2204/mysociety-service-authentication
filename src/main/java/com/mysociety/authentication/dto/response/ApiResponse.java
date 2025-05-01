package com.mysociety.authentication.dto.response;


import com.mysociety.authentication.enums.ResponseError;
import lombok.Data;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Lazy
@Data
public class ApiResponse<T> {

    private String message;
    private T data;

    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public static ResponseEntity<?> success(Optional<String> message, Optional data, Optional<HttpStatus> status){
        Map<String ,Object> resData = new HashMap<>();
        resData.put("message" , message.orElse("ok"));
        resData.put("timestamp" , new Date(System.currentTimeMillis()).toString());
        if(data.isPresent()){
            resData.put("data", data.get());
        }
        return new ResponseEntity<>(resData, status.orElse(HttpStatus.OK) );
    }
    public static ResponseEntity<?> failed(Optional<String> error,  Optional<String> message,Optional<HttpStatus> status){
        Map<String ,Object> resData = new HashMap<>();
        resData.put("error" , error.orElse("ERROR"));
        resData.put("message", message.orElse(ResponseError.INTERNAL_SERVER_ERROR.toString()));
        resData.put("timestamp" , new Date(System.currentTimeMillis()).toString());
        return new ResponseEntity<>(resData, status.orElse(HttpStatus.INTERNAL_SERVER_ERROR));
    }



}
