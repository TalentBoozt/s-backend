package com.talentboozt.s_backend.domains.leads.controller;

import com.talentboozt.s_backend.domains.leads.service.LLeadEngagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/leads")
public class LReplyController {

    private final LLeadEngagementService engagementService;

    public LReplyController(LLeadEngagementService engagementService) {
        this.engagementService = engagementService;
    }

    @PostMapping("/draft")
    public ResponseEntity<Map<String, String>> generateDraft(@RequestBody Map<String, String> request) {
        String signalId = request.get("signalId");
        String tone = request.get("tone");
        String draft = engagementService.generateDraft(signalId, tone);
        return ResponseEntity.ok(Map.of("draft", draft));
    }

    @PostMapping("/send-reply")
    public ResponseEntity<Map<String, String>> sendReply(@RequestBody Map<String, String> request) {
        String signalId = request.get("signalId");
        String replyText = request.get("replyText");
        engagementService.executeReply(signalId, replyText);
        return ResponseEntity.ok(Map.of("message", "Reply sent and logged successfully"));
    }
}
