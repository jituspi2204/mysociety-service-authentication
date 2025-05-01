package com.mysociety.authentication.controllers;

import com.mysociety.authentication.dto.request.SignUpRequest;
import com.mysociety.authentication.dto.request.RegisterUserRequest;
import com.mysociety.authentication.dto.response.ApiResponse;
import com.mysociety.authentication.entities.CustomUserPrincipal;
import com.mysociety.authentication.enums.ResponseError;
import com.mysociety.authentication.token.TokenService;
import com.mysociety.authentication.services.UserAuthService;
import com.mysociety.authentication.utils.Validator;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/api/v1/")
public class UserAuthController {

    private static final Logger logger = LoggerFactory.getLogger(UserAuthController.class);


    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private TokenService tokenService;

    @GetMapping("/authentication/health")
    public ResponseEntity<String> getHealthStatus(){
        return ResponseEntity.ok().body("Working fine!!");
    }

    @GetMapping("/authentication/generate-otp")
    public ResponseEntity<?> generateOtpForAuth(@RequestBody SignUpRequest signUpRequest){
        //validate whether phone number is correct or not
        String phoneNumber = signUpRequest.getPhoneNumber();
        if(!Validator.isValidPhoneNumber(phoneNumber)){
            logger.debug("phone number not valid");
            return ApiResponse.failed(
                    Optional.of(ResponseError.VALIDATION_ERROR.toString()),
                    Optional.of("invalid phone number"),
                    Optional.of(HttpStatus.BAD_REQUEST)
                    );
        }
        userAuthService.generateOtpForUser(signUpRequest.getPhoneNumber());
        logger.debug("otp generated and sent to user phone");
        return ApiResponse.success(
                Optional.of("otp send to registered phone number"),
                Optional.of(Map.of("phone_number", signUpRequest.getPhoneNumber())),
                Optional.of(HttpStatus.CREATED)
        );
    }

    @PostMapping("/authentication/verify-otp")
    public ResponseEntity<?> verifyOtpForUser(HttpServletRequest request, @RequestBody SignUpRequest signUpRequest){
        if(!Validator.isValidPhoneNumber(signUpRequest.getPhoneNumber())){
            logger.debug("not valid phone number, inside verify otp");
            return ApiResponse.failed(
                    Optional.of(ResponseError.VALIDATION_ERROR.toString()),
                    Optional.of("invalid phone number"),
                    Optional.of(HttpStatus.BAD_REQUEST)
            );
        }
        String token = userAuthService.verifyUserOtpThenLogin(
                                    signUpRequest.getPhoneNumber(),
                                    signUpRequest.getOtp(),
                                    signUpRequest.getCountryCode(),
                                    request
        );
        logger.debug("user phone number verified");
        return ApiResponse.success(
                Optional.of("phone number verfied"),
                Optional.of(Map.of("token", token)),
                Optional.of(HttpStatus.CREATED)
        );
    }


    @DeleteMapping("/authentication/logout")
    public ResponseEntity<?> logoutUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserPrincipal principal = (CustomUserPrincipal) auth.getPrincipal();
        tokenService.deleteToken(principal.getToken());
        logger.debug("user logout successfully");
        return new ResponseEntity<>("logout successfully",
                HttpStatus.OK);
    }



}
