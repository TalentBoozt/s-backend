package com.talentboozt.s_backend.domains.support.service;

import com.talentboozt.s_backend.domains.support.model.SupportRequestModel;
import com.talentboozt.s_backend.domains.support.repository.SupportRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportRequestRepository supportRequestRepository;
    private final com.talentboozt.s_backend.shared.mail.service.EmailService emailService;

    public SupportRequestModel submitRequest(SupportRequestModel request) {
        request.setStatus("PENDING");
        request.setCreatedAt(Instant.now());
        SupportRequestModel saved = supportRequestRepository.save(request);

        try {
            emailService.sendSupportRequestNotification(saved);
        } catch (Exception e) {
            // Log but don't fail the request
            e.printStackTrace();
        }

        return saved;
    }
}
