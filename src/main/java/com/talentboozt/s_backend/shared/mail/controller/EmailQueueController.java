package com.talentboozt.s_backend.shared.mail.controller;

import com.talentboozt.s_backend.shared.mail.cfg.EmailQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring/email-queue")
public class EmailQueueController {

    @Autowired
    private EmailQueueService emailQueueService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getQueueStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("queueSize", emailQueueService.getQueueSize());
        response.put("maxRetryLimit", 3);
        response.put("status", "OK");

        return ResponseEntity.ok(response);
    }
}
