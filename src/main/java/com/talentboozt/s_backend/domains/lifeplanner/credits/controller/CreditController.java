package com.talentboozt.s_backend.domains.lifeplanner.credits.controller;

import com.talentboozt.s_backend.domains.lifeplanner.credits.model.UserCredits;
import com.talentboozt.s_backend.domains.lifeplanner.credits.service.LifePlannerCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lifeplanner/credits")
@RequiredArgsConstructor
public class CreditController {

    private final LifePlannerCreditService lifePlannerCreditService;

    @GetMapping
    public ResponseEntity<UserCredits> getUserCredits(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(lifePlannerCreditService.getUserCreditsInfo(userId));
    }
}
