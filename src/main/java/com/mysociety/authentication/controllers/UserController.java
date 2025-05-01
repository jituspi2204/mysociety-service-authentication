package com.mysociety.authentication.controllers;


import com.mysociety.authentication.dto.request.RegisterUserRequest;
import com.mysociety.authentication.dto.request.UpdateUserDetailsRequest;
import com.mysociety.authentication.dto.response.ApiResponse;
import com.mysociety.authentication.entities.CustomUserPrincipal;
import com.mysociety.authentication.enums.ResponseError;
import com.mysociety.authentication.services.UserAuthService;
import com.mysociety.authentication.services.UserService;
import com.mysociety.authentication.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<?> userDetailsController(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ApiResponse.success(
                Optional.of("user details"),
                Optional.of((CustomUserPrincipal)auth.getPrincipal()),
                Optional.empty()
        );
    }

    @PutMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest registerUserRequest){
        if(registerUserRequest.getName() == null || registerUserRequest.getName().isBlank()){
            return ApiResponse.failed(
                    Optional.of(ResponseError.VALIDATION_ERROR.toString()),
                    Optional.of("name field not provided"),
                    Optional.of(HttpStatus.BAD_REQUEST));
        }
        userService.registerUser(registerUserRequest);
        return ApiResponse.success(Optional.of("registered successfully"),
                Optional.empty(),
                Optional.empty());
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserDetailsController(@RequestBody UpdateUserDetailsRequest request){
        Map<String, Object> fieldMap = new HashMap<>();
        if(request.name() != null) fieldMap.put("name", request.name());
        if(request.email() != null) fieldMap.put("email", request.email());
        if(request.societyId() != null){
            fieldMap.put("society_id", request.societyId());
            if(request.flatNo() != null) fieldMap.put("flat_no", request.flatNo());
            if(request.blockNo() != null) fieldMap.put("block_no", request.blockNo());
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String id = ((CustomUserPrincipal)(auth.getPrincipal())).getId();
        userService.updateUserDetails(id, fieldMap);
        return ApiResponse.success(
                Optional.of("updated"),
                Optional.of(fieldMap),
                Optional.empty()
        );
    }

    @GetMapping("/update/phone-number")
    public ResponseEntity<?> generateOtpForUpdatedNumber(@RequestBody Map<String, String> request){
        String newPhoneNumber= request.get("new_phone_number");
        if(!Validator.isValidPhoneNumber(newPhoneNumber)){
            return ApiResponse.failed(
                    Optional.of(ResponseError.VALIDATION_ERROR.toString()),
                    Optional.of("invalid phone number"),
                    Optional.of(HttpStatus.BAD_REQUEST));
        }
        CustomUserPrincipal user = (CustomUserPrincipal)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if(user.getPhoneNumber().equals(newPhoneNumber)){
            return ApiResponse.failed(
                    Optional.of(ResponseError.OPERATION_NOT_ALLOWED.toString()),
                    Optional.of("new phone number and registered phone number are is same "),
                    Optional.of(HttpStatus.BAD_REQUEST));
        }
        userService.generateOtpForNewPhoneNumber(user.getId(),newPhoneNumber);
        return ApiResponse.success(
                Optional.of("otp send to new number"),
                Optional.of(request),
                Optional.empty()
        );
    }
    @PutMapping("/update/phone-number/verify")
    public ResponseEntity<?> verifyOtpForUpdatedNumber(@RequestBody Map<String, String> request){
        String newPhoneNumber= request.get("new_phone_number");
        if(!Validator.isValidPhoneNumber(newPhoneNumber)){
            return ApiResponse.failed(
                    Optional.of(ResponseError.VALIDATION_ERROR.toString()),
                    Optional.of("invalid phone number"),
                    Optional.of(HttpStatus.BAD_REQUEST));
        }
        CustomUserPrincipal user = (CustomUserPrincipal)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        userService.verifyOtpForNewPhoneNumber(user.getId(),newPhoneNumber, request.get("otp"));
        return ApiResponse.success(
                Optional.of("phone number update"),
                Optional.of(Map.of("new_phone_number", newPhoneNumber)),
                Optional.empty()
        );
    }
}
