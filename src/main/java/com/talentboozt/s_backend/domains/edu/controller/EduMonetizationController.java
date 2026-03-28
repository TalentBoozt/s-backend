package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.monetization.CheckoutRequest;
import com.talentboozt.s_backend.domains.edu.dto.monetization.PortalRequest;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.service.EduMonetizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/monetization")
public class EduMonetizationController {

    private final EduMonetizationService monetizationService;

    public EduMonetizationController(EduMonetizationService monetizationService) {
        this.monetizationService = monetizationService;
    }

    @PostMapping("/stripe/checkout")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody CheckoutRequest request) throws Exception {
        return ResponseEntity.ok(monetizationService.createCheckoutSession(request));
    }

    @PostMapping("/stripe/portal")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> createPortalSession(@RequestBody PortalRequest request) throws Exception {
        return ResponseEntity.ok(monetizationService.createPortalSession(request.getUserId()));
    }

    @GetMapping("/subscriptions/user/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ESubscriptions> getSubscriptionStatus(@PathVariable String userId) {
        return ResponseEntity.ok(monetizationService.getSubscriptionStatus(userId));
    }
}
