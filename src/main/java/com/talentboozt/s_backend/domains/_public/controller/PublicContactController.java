package com.talentboozt.s_backend.domains._public.controller;

import com.talentboozt.s_backend.domains._public.dto.ContactFormRequest;
import com.talentboozt.s_backend.domains._public.dto.CtaLeadRequest;
import com.talentboozt.s_backend.domains._public.service.PublicContactService;
import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/contact")
public class PublicContactController {

    private final PublicContactService publicContactService;

    public PublicContactController(PublicContactService publicContactService) {
        this.publicContactService = publicContactService;
    }

    @PostMapping("/leads")
    public ResponseEntity<ApiResponse> submitLead(@RequestBody CtaLeadRequest request) {
        return ResponseEntity.ok(publicContactService.handleCtaLead(request));
    }

    @PostMapping("/forms")
    public ResponseEntity<ApiResponse> submitContactForm(@RequestBody ContactFormRequest request) {
        return ResponseEntity.ok(publicContactService.handleContactForm(request));
    }
}
