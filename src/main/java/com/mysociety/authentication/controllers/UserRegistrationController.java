package com.mysociety.authentication.controllers;


import com.mysociety.authentication.dto.request.RegisterUserRequest;
import com.mysociety.authentication.dto.response.ApiResponse;
import com.mysociety.authentication.enums.ResponseError;
import com.mysociety.authentication.services.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/register")
public class UserRegistrationController {
    @Autowired
    private UserAuthService userAuthService;


}
