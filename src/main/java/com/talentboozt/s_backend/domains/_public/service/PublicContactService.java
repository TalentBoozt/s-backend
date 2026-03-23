package com.talentboozt.s_backend.domains._public.service;

import com.talentboozt.s_backend.domains._public.dto.ContactFormRequest;
import com.talentboozt.s_backend.domains._public.dto.CtaLeadRequest;
import com.talentboozt.s_backend.domains._public.model.ContactSubmission;
import com.talentboozt.s_backend.domains._public.model.CtaLeadSubmission;
import com.talentboozt.s_backend.domains._public.repository.ContactSubmissionRepository;
import com.talentboozt.s_backend.domains._public.repository.CtaLeadRepository;
import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import com.talentboozt.s_backend.shared.mail.dto.ContactUsDTO;
import com.talentboozt.s_backend.shared.mail.dto.LeadsDTO;
import com.talentboozt.s_backend.shared.mail.service.EmailService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PublicContactService {

    private final CtaLeadRepository ctaLeadRepository;
    private final ContactSubmissionRepository contactSubmissionRepository;
    private final EmailService emailService;

    public PublicContactService(CtaLeadRepository ctaLeadRepository, ContactSubmissionRepository contactSubmissionRepository, EmailService emailService) {
        this.ctaLeadRepository = ctaLeadRepository;
        this.contactSubmissionRepository = contactSubmissionRepository;
        this.emailService = emailService;
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

        // Send Email Notification
        try {
            LeadsDTO leadsDTO = new LeadsDTO();
            leadsDTO.setName(request.getName());
            leadsDTO.setEmail(request.getEmail());
            leadsDTO.setServiceType(request.getServiceType());
            leadsDTO.setCtaType(request.getCtaType());
            leadsDTO.setFocusArea(request.getFocusArea());
            leadsDTO.setMessage(request.getMessage());
            emailService.leads(leadsDTO);
        } catch (IOException e) {
            // Log error but don't fail the request
            e.printStackTrace();
        }
        
        return new ApiResponse("Lead submitted successfully.");
    }

    public ApiResponse handleContactForm(ContactFormRequest request) {
        ContactSubmission submission = new ContactSubmission();
        submission.setName(request.getName());
        submission.setEmail(request.getEmail());
        submission.setSubject(request.getSubject());
        submission.setMessage(request.getMessage());
        
        contactSubmissionRepository.save(submission);

        // Send Email Notification
        try {
            ContactUsDTO contactUsDTO = new ContactUsDTO();
            contactUsDTO.setName(request.getName());
            contactUsDTO.setEmail(request.getEmail());
            contactUsDTO.setSubject(request.getSubject());
            contactUsDTO.setMessage(request.getMessage());
            emailService.contactUs(contactUsDTO);
        } catch (IOException e) {
            // Log error but don't fail the request
            e.printStackTrace();
        }
        
        return new ApiResponse("Contact form submitted successfully.");
    }
}
