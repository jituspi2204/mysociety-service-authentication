package com.mysociety.authentication.exceptions;

import com.mysociety.authentication.dto.response.ApiResponse;
import com.mysociety.authentication.enums.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<?> authenticationFailedExceptionHandler(Exception e){
        return ApiResponse.failed(
                Optional.of(ResponseError.AUTHENTICATION_FAILED.toString()),
                Optional.of(e.getMessage()),
                Optional.of(HttpStatus.FORBIDDEN)
        );
    }
    @ExceptionHandler(DatabaseRuntimeException.class)
    public ResponseEntity<?> databaseRuntimeExceptionHandler(Exception e){
        return ApiResponse.failed(
                Optional.empty(),
                Optional.of(e.getMessage()),
                Optional.empty()
        );
    }
    @ExceptionHandler(InvalidRequestFieldException.class)
    public ResponseEntity<?> invalidRequestFieldExceptionHandler(Exception e){
        return ApiResponse.failed(
                Optional.of(ResponseError.VALIDATION_ERROR.toString()),
                Optional.of(e.getMessage()),
                Optional.of(HttpStatus.BAD_REQUEST)
        );
    }
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<?> userAlreadyExistExceptionHandler(Exception e){
        return ApiResponse.failed(
                Optional.of(ResponseError.USER_EXIST.toString()),
                Optional.of(e.getMessage()),
                Optional.of(HttpStatus.UNAUTHORIZED)
        );
    }

    @ExceptionHandler(OtpValidationException.class)
    public ResponseEntity<?> otpValidationExceptionHandler(Exception e){
        return ApiResponse.failed(
                Optional.of(ResponseError.VALIDATION_ERROR.toString()),
                Optional.of(e.getMessage()),
                Optional.of(HttpStatus.FORBIDDEN)
        );
    }

}
