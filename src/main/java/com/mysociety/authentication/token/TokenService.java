package com.mysociety.authentication.token;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TokenService {

    @Autowired
    private TokenRepository tokenRepository;

    public OpaqueToken getTokenDetails(String tokenAlias){
        Optional<OpaqueToken> opaqueToken = tokenRepository.findByTokenAlias(tokenAlias);
        return opaqueToken.orElse(null);
    }
    public void deleteToken(String tokenAlias){
        Optional<OpaqueToken> opaqueToken = tokenRepository.findByTokenAlias(tokenAlias);
        if(opaqueToken.isEmpty()){
            throw new RuntimeException("user not logged in");
        }
        tokenRepository.deleteByTokenAlias(tokenAlias);
    }
}
