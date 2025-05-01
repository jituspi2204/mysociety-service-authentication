package com.mysociety.authentication.dto.request;

public record UpdateUserDetailsRequest(
        String name,
        String societyId,
        String flatNo,
        String blockNo,
        String email
) {
}
