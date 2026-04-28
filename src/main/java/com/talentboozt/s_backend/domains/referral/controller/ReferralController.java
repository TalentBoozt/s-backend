package com.talentboozt.s_backend.domains.referral.controller;

import com.talentboozt.s_backend.domains.referral.enums.ReferralType;
import com.talentboozt.s_backend.domains.referral.model.Referral;
import com.talentboozt.s_backend.domains.referral.model.ReferralCode;
import com.talentboozt.s_backend.domains.referral.service.ReferralService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/referrals")
@RequiredArgsConstructor
public class ReferralController {

    private final ReferralService referralService;

    /**
     * Generates a unique referral code for the authenticated user.
     */
    @PostMapping("/code/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReferralCode> generateMyCode(@AuthenticatedUser String userId) {
        return ResponseEntity.ok(referralService.generateReferralCode(userId));
    }

    /**
     * Validates a referral code and returns the associated ReferralCode entity.
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<ReferralCode> validateCode(@PathVariable String code) {
        return referralService.validateReferralCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Registers a new referral for the current user using a referral code.
     */
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Referral> register(@AuthenticatedUser String userId, 
                                           @RequestParam String code, 
                                           @RequestParam ReferralType type) {
        ReferralCode refCode = referralService.validateReferralCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Invalid referral code"));
        
        return ResponseEntity.ok(referralService.registerReferral(refCode.getUserId(), userId, type));
    }

    /**
     * Retrieves all referrals made by the authenticated user.
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Referral>> getMyReferrals(@AuthenticatedUser String userId) {
        return ResponseEntity.ok(referralService.getMyReferrals(userId));
    }
}
