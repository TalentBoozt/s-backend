package com.talentboozt.s_backend.domains.subscription.controller;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.subscription.model.FeatureFlag;
import com.talentboozt.s_backend.domains.subscription.repository.mongodb.FeatureFlagRepository;
import com.talentboozt.s_backend.domains.subscription.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/subscription/features")
@RequiredArgsConstructor
public class FeatureFlagController {

    private final FeatureFlagService featureFlagService;
    private final FeatureFlagRepository featureFlagRepository;

    @GetMapping("/plan/{plan}")
    public ResponseEntity<List<String>> getFeaturesForPlan(@PathVariable ESubscriptionPlan plan) {
        return ResponseEntity.ok(featureFlagService.getFeaturesForPlan(plan));
    }

    @PostMapping("/flag")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<FeatureFlag> setFeatureFlag(@RequestBody FeatureFlag flag) {
        FeatureFlag existing = featureFlagRepository.findByPlanAndFeatureKey(flag.getPlan(), flag.getFeatureKey())
                .orElse(flag);
        existing.setEnabled(flag.isEnabled());
        return ResponseEntity.ok(featureFlagRepository.save(existing));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<FeatureFlag>> getAllFlags() {
        return ResponseEntity.ok(featureFlagRepository.findAll());
    }
}
