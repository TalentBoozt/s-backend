package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.EBundles;
import com.talentboozt.s_backend.domains.edu.service.EduBundleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/bundles")
@RequiredArgsConstructor
public class EduBundleController {

    private final EduBundleService bundleService;

    @PostMapping
    @PreAuthorize("hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<EBundles> createBundle(
            @RequestParam String creatorId,
            @RequestBody EBundles request) {
        return ResponseEntity.ok(bundleService.createBundle(creatorId, request));
    }

    @GetMapping("/creator/{creatorId}")
    @PreAuthorize("hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<List<EBundles>> getBundlesByCreator(@PathVariable String creatorId) {
        return ResponseEntity.ok(bundleService.getBundlesByCreator(creatorId));
    }

    @PutMapping("/{bundleId}")
    @PreAuthorize("hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<EBundles> updateBundle(
            @PathVariable String bundleId,
            @RequestParam String creatorId,
            @RequestBody EBundles request) {
        return ResponseEntity.ok(bundleService.updateBundle(bundleId, creatorId, request));
    }

    @DeleteMapping("/{bundleId}")
    @PreAuthorize("hasAuthority('SELLER_FREE') or hasAuthority('ENTERPRISE_INSTRUCTOR')")
    public ResponseEntity<Void> deleteBundle(
            @PathVariable String bundleId,
            @RequestParam String creatorId) {
        bundleService.deleteBundle(bundleId, creatorId);
        return ResponseEntity.noContent().build();
    }
}
