package com.mysociety.authentication.jwtauth;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.Base64;

public class RsaKeyGenerator {

    private static final String privateKeyPath = "private_key.pem";
    private static final String publicKeyPath = "public_key.pem";

    public static void main(String[] args)  throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        saveKeyToFile(privateKeyPath, privateKey.getEncoded(), "PRIVATE");
        saveKeyToFile(publicKeyPath, publicKey.getEncoded(), "PUBLIC");
    }

    private static void saveKeyToFile(String filename, byte[] key, String type){

        String base64 = Base64.getEncoder().encodeToString(key);
        String keyString = "-----BEGIN " + type + " KEY-----\n" +
                base64 + "\n-----END " + type + " KEY-----";
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(keyString.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
