package com.talentboozt.s_backend.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

@Component
public class EncryptionUtility {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static byte[] KEY;
    private static byte[] IV;

    @Autowired
    public EncryptionUtility(ConfigUtility configUtil) throws NoSuchAlgorithmException {
        String secretKey = configUtil.getProperty("ENCRYPT_PASSWORD");

        // CRUCIAL: MATCH FRONTEND KEY GENERATION
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] hashedKey = sha.digest(secretKey.getBytes());
        KEY = Arrays.copyOfRange(hashedKey, 0, 16); // First 16 bytes

        // MATCH MD5 IV GENERATION
        MessageDigest md = MessageDigest.getInstance("MD5");
        IV = md.digest(secretKey.getBytes());
    }

    public static String encrypt(String data) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(original);
    }
}
