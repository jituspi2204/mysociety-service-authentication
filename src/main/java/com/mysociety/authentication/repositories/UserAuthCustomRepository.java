package com.mysociety.authentication.repositories;


import java.util.Map;

public interface UserAuthCustomRepository {
    public void updateUserTokenByPhoneNumber(String phoneNumber, String token, long validity);
    public void updateUserFieldsByPhoneNumber(String phoneNumber, Map<String , Object> fields);
    public void updateUserFieldsById(String id, Map<String , Object> fields);
    public void refreshTokenByPhoneNumber(String phoneNumber);

}
