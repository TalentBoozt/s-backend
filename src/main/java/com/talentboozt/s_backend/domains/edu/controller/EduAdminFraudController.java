package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.EFraudFlag;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EFraudFlagRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/edu/admin/fraud")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
public class EduAdminFraudController {

    private final EFraudFlagRepository fraudFlagRepository;
    private final EUserRepository userRepository;

    @GetMapping("/flags")
    public ResponseEntity<List<EFraudFlag>> getAllFlags() {
        return ResponseEntity.ok(fraudFlagRepository.findAll());
    }

    @GetMapping("/flags/pending")
    public ResponseEntity<List<EFraudFlag>> getPendingFlags() {
        return ResponseEntity.ok(fraudFlagRepository.findByStatus("PENDING_REVIEW"));
    }

    @PostMapping("/flags/{flagId}/resolve")
    public ResponseEntity<EFraudFlag> resolveFlag(
            @PathVariable String flagId,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        
        EFraudFlag flag = fraudFlagRepository.findById(flagId)
                .orElseThrow(() -> new EduResourceNotFoundException("Flag not found"));
        
        flag.setStatus(status);
        flag.setResolutionNotes(notes);
        flag.setResolvedAt(Instant.now());
        
        return ResponseEntity.ok(fraudFlagRepository.save(flag));
    }

    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<Void> banUser(@PathVariable String userId, @RequestParam String reason) {
        EUser user = userRepository.findById(userId)
                .orElseThrow(() -> new EduResourceNotFoundException("User not found"));
        
        user.setIsBanned(true);
        user.setBanReason(reason);
        user.setIsActive(false);
        userRepository.save(user);
        
        return ResponseEntity.noContent().build();
    }
}
