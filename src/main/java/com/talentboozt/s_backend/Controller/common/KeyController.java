package com.talentboozt.s_backend.Controller.common;

import com.talentboozt.s_backend.Service.common.KeyService;
import com.talentboozt.s_backend.Utils.EncryptionUtility;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private KeyService keyService;

    @PostMapping("/encrypt")
    public ResponseEntity<Map<String, String>> encrypt(@RequestBody Map<String, String> request) {
        String data = request.get("data");
        return keyService.encryptData(data);
    }

    @PostMapping("/decrypt")
    public ResponseEntity<Map<String, String>> decrypt(@RequestBody Map<String, String> request) {
        String data = request.get("data");
        return keyService.decryptData(data);
    }
}
