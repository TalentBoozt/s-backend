package com.talentboozt.s_backend.domains.support.controller;

import com.talentboozt.s_backend.domains.support.model.SupportRequestModel;
import com.talentboozt.s_backend.domains.support.service.SupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v2/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;

    @PostMapping("/submit")
    public ResponseEntity<SupportRequestModel> submitSupportRequest(@RequestBody SupportRequestModel request) {
        log.info("Received support request from: {}", request.getEmail());
        SupportRequestModel saved = supportService.submitRequest(request);
        return ResponseEntity.ok(saved);
    }
}
