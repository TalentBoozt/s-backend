package com.talentboozt.s_backend.shared.security.service;

import com.talentboozt.s_backend.shared.utils.EncryptionUtility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KeyService {

    private final EncryptionUtility encryptionUtility;

    public KeyService(EncryptionUtility encryptionUtility) {
        this.encryptionUtility = encryptionUtility;
    }

    public ResponseEntity<Map<String, String>> encryptData(String data) {
        try {
            String encryptedData = encryptionUtility.encrypt(data);
            Map<String, String> response = Map.of("data", encryptedData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = Map.of("Encryption failed", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, String>> decryptData(String data) {
        try {
            String decryptedData = encryptionUtility.decrypt(data);
            Map<String, String> response = Map.of("data", decryptedData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = Map.of("Decryption failed", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
