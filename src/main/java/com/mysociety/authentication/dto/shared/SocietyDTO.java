package com.mysociety.authentication.dto.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocietyDTO {
    @JsonProperty("society_name")
    private String societyName;

    @JsonProperty("society_address")
    private String societyAddress;

    @JsonProperty("society_city")
    private String societyCity;
    @JsonProperty("society_state")
    private String societyState;
}
