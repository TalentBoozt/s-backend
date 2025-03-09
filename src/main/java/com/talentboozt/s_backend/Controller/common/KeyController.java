package com.talentboozt.s_backend.Controller.common;

import com.talentboozt.s_backend.Utils.EncryptionUtility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/encryption")
public class KeyController {

    private final EncryptionUtility encryptionUtility;

    public KeyController(EncryptionUtility encryptionUtility) {
        this.encryptionUtility = encryptionUtility;
    }

    @PostMapping("/encrypt")
    public ResponseEntity<Map<String, String>> encrypt(@RequestBody Map<String, String> request) {
        String data = request.get("data");
        try {
            String encryptedData = encryptionUtility.encrypt(data);
            Map<String, String> response = Map.of("data", encryptedData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = Map.of("Encryption failed", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/decrypt")
    public ResponseEntity<Map<String, String>> decrypt(@RequestBody Map<String, String> request) {
        String data = request.get("data");
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
