package com.mysociety.authentication.entities;

import com.mysociety.authentication.enums.Role;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

@Document(collection = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    @Field("phone_number")
    private String phoneNumber;
    private String name;
    @Field("country_code")
    private String countryCode;
    private List<Role> roles;
    @Field("society_id")
    private String societyId;
    @Field("society_name")
    private String societyName;
    @Field("society_address")
    private String societyAddress;
    @Field("block_no")
    private String blockNo;
    @Field("flat_no")
    private String flatNo;
    private String token;
    @Field("token_expired_at")
    private Date tokenExpiredAt;
    @Field("created_at")
    private Date createdAt;
    @Field("updated_at")
    private Date updatedAt;
}
