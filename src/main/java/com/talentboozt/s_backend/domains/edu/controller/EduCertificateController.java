package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.model.ECertificates;
import com.talentboozt.s_backend.domains.edu.service.EduCertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/edu/certificates")
public class EduCertificateController {

    private final EduCertificateService certificateService;

    public EduCertificateController(EduCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/generate/{enrollmentId}")
    @PreAuthorize("hasAuthority('LEARNER')")
    public ResponseEntity<ECertificates> generateCertificate(@PathVariable String enrollmentId) {
        return ResponseEntity.ok(certificateService.generateCertificate(enrollmentId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('LEARNER')")
    public ResponseEntity<List<ECertificates>> getUserCertificates(@PathVariable String userId) {
        return ResponseEntity.ok(certificateService.getUserCertificates(userId));
    }

    // Public verification endpoint
    @GetMapping("/verify/{certificateId}")
    public ResponseEntity<ECertificates> verifyCertificate(@PathVariable String certificateId) {
        return ResponseEntity.ok(certificateService.verifyCertificate(certificateId));
    }
}
