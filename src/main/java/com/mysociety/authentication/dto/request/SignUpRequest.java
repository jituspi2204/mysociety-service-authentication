package com.mysociety.authentication.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequest {
    private String email;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String password;
    private String otp;
    @JsonProperty("country_code")
    private String countryCode;
}
