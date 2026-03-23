package com.talentboozt.s_backend.domains._public.service;

import com.talentboozt.s_backend.domains._public.dto.ContactFormRequest;
import com.talentboozt.s_backend.domains._public.dto.CtaLeadRequest;
import com.talentboozt.s_backend.domains._public.model.ContactSubmission;
import com.talentboozt.s_backend.domains._public.model.CtaLeadSubmission;
import com.talentboozt.s_backend.domains._public.repository.ContactSubmissionRepository;
import com.talentboozt.s_backend.domains._public.repository.CtaLeadRepository;
import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public class PublicContactService {

    private final CtaLeadRepository ctaLeadRepository;
    private final ContactSubmissionRepository contactSubmissionRepository;

    public PublicContactService(CtaLeadRepository ctaLeadRepository, ContactSubmissionRepository contactSubmissionRepository) {
        this.ctaLeadRepository = ctaLeadRepository;
        this.contactSubmissionRepository = contactSubmissionRepository;
    }

    public ApiResponse handleCtaLead(CtaLeadRequest request) {
        CtaLeadSubmission submission = new CtaLeadSubmission();
        submission.setName(request.getName());
        submission.setEmail(request.getEmail());
        submission.setServiceType(request.getServiceType());
        submission.setCtaType(request.getCtaType());
        submission.setFocusArea(request.getFocusArea());
        submission.setMessage(request.getMessage());
        
        ctaLeadRepository.save(submission);
        
        return new ApiResponse("Lead submitted successfully.");
    }

    public ApiResponse handleContactForm(ContactFormRequest request) {
        ContactSubmission submission = new ContactSubmission();
        submission.setName(request.getName());
        submission.setEmail(request.getEmail());
        submission.setSubject(request.getSubject());
        submission.setMessage(request.getMessage());
        
        contactSubmissionRepository.save(submission);
        
        return new ApiResponse("Contact form submitted successfully.");
    }
}
