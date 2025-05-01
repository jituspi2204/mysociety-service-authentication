package com.mysociety.authentication.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {

    private String name;
    private String email;
    private String password;
    @JsonProperty("society_id")
    private String societyId;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("flat_no")
    private String flatNo;
    @JsonProperty("block_no")
    private String blockNo;

}
