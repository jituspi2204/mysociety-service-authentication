package com.mysociety.authentication.jwtauth;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class KeyLoader {
    private static final String PRIVATE_KEY_PATH = "keys/private_key.pem";
    private static final String PUBLIC_KEY_PATH = "keys/public_key.pem";

    public static PublicKey getPublicKey() throws Exception {
        String key = readKeyFromFile(PUBLIC_KEY_PATH);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PrivateKey getPrivateKey() throws Exception {
        String key = readKeyFromFile(PRIVATE_KEY_PATH);
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private static String readKeyFromFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        String key = new String(Files.readAllBytes(resource.getFile().toPath()));
        return key.replaceAll("-----BEGIN (.*) KEY-----", "")
                .replaceAll("-----END (.*) KEY-----", "")
                .replaceAll("\n", "").trim();
    }
}
