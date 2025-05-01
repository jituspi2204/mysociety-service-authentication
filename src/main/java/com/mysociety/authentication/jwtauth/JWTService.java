package com.mysociety.authentication.jwtauth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JWTService {


    private final PrivateKey privateKey;

    private final PublicKey publicKey;

//    @Value("${jwt_expiration-time}")
    private final long JWT_EXPIRATION_TIME = 3600 * 12 * 1000;

    public JWTService(){
        try {
            privateKey = KeyLoader.getPrivateKey();
            publicKey = KeyLoader.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //generate secret-key

//    public static void main(String... args){
//        SecretKey key = Jwts.SIG.HS256.key().build();
//        String secretString = Encoders.BASE64.encode(key.getEncoded());
//        System.out.println(secretString);
//    }


    public Claims extractAllClaims(String token){
        return Jwts.parser().verifyWith(publicKey).build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T>claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private String buildToken(Map<String, Object> claims, String username, long expiration){
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();

    }

    public String generateToken(Map<String, Object> claims, String username){
        return buildToken(claims, username, JWT_EXPIRATION_TIME);
    }

    public long getExpirationTime(){
        return JWT_EXPIRATION_TIME;
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) &&
                extractExpiration(token).after(new Date(System.currentTimeMillis()));

    }

}
