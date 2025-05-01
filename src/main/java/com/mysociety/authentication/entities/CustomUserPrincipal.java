package com.mysociety.authentication.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mysociety.authentication.enums.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomUserPrincipal implements UserDetails {
    private String id;
    private String phoneNumber;
    private String societyId;
    private String societyName;
    private String name;
    private String email;
    private String flatNo;
    private String blockNo;
    private List<Role> roles;
    @JsonIgnore
    private String token;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return this.id;
    }
}
