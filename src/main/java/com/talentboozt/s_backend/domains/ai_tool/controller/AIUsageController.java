package com.talentboozt.s_backend.domains.ai_tool.controller;

import com.talentboozt.s_backend.domains.ai_tool.model.AIQuota;
import com.talentboozt.s_backend.domains.ai_tool.repository.mongodb.AIQuotaRepository;
import com.talentboozt.s_backend.domains.ai_tool.repository.mongodb.AIUsageRepository;
import com.talentboozt.s_backend.domains.ai_tool.model.AIUsage;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai/usage")
@RequiredArgsConstructor
public class AIUsageController {

    private final AIQuotaRepository quotaRepository;
    private final AIUsageRepository usageRepository;

    @GetMapping("/quota")
    public ResponseEntity<AIQuota> getMyQuota(@AuthenticatedUser String userId) {
        return quotaRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<AIUsage>> getMyUsageLogs(
            @AuthenticatedUser String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<AIUsage> logs = usageRepository.findByUserId(
                userId, 
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
        return ResponseEntity.ok(logs);
    }
}
