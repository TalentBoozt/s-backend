package com.talentboozt.s_backend.shared.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Secure key manager that generates keys on-demand instead of storing them statically.
 * This reduces the risk of key extraction from heap dumps.
 * 
 * Note: For production, consider using a Hardware Security Module (HSM) or
 * a key management service like AWS KMS, Azure Key Vault, or HashiCorp Vault.
 */
@Component
public class SecureKeyManager {

    private final ConfigUtility configUtility;
    private volatile byte[] keyCache;
    private volatile byte[] ivCache;

    @Autowired
    public SecureKeyManager(ConfigUtility configUtility) {
        this.configUtility = configUtility;
    }

    /**
     * Generate encryption key on-demand (not stored statically)
     */
    public SecretKeySpec getEncryptionKey() throws NoSuchAlgorithmException {
        if (keyCache == null) {
            synchronized (this) {
                if (keyCache == null) {
                    String secretKey = configUtility.getProperty("ENCRYPT_PASSWORD");
                    MessageDigest sha = MessageDigest.getInstance("SHA-256");
                    byte[] hashedKey = sha.digest(secretKey.getBytes());
                    keyCache = Arrays.copyOfRange(hashedKey, 0, 16);
                }
            }
        }
        return new SecretKeySpec(keyCache, "AES");
    }

    /**
     * Generate IV on-demand (not stored statically)
     */
    public IvParameterSpec getIv() throws NoSuchAlgorithmException {
        if (ivCache == null) {
            synchronized (this) {
                if (ivCache == null) {
                    String secretKey = configUtility.getProperty("ENCRYPT_PASSWORD");
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    ivCache = md.digest(secretKey.getBytes());
                }
            }
        }
        return new IvParameterSpec(ivCache);
    }

    /**
     * Clear cached keys (useful for security rotations)
     */
    public void clearCache() {
        synchronized (this) {
            if (keyCache != null) {
                Arrays.fill(keyCache, (byte) 0);
                keyCache = null;
            }
            if (ivCache != null) {
                Arrays.fill(ivCache, (byte) 0);
                ivCache = null;
            }
        }
    }
}
