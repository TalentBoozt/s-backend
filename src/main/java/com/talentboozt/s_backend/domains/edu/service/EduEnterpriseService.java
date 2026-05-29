package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.model.EnterpriseInquiry;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EnterpriseInquiryRepository;
import com.talentboozt.s_backend.shared.mail.service.HTMLEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduEnterpriseService {
    private final EnterpriseInquiryRepository inquiryRepository;
    private final EduSubscriptionService subscriptionService;
    private final HTMLEmailService emailService;

    public EnterpriseInquiry submitInquiry(EnterpriseInquiry inquiry) {
        EnterpriseInquiry saved = inquiryRepository.save(inquiry);

        // Send internal notification (Admin)
        try {
            emailService.sendHtmlEmail(
                    "info@talnova.io",
                    "New Enterprise Inquiry: " + inquiry.getCompanyName(),
                    "<h1>New Enterprise Lead</h1>" +
                            "<p><strong>Company:</strong> " + inquiry.getCompanyName() + "</p>" +
                            "<p><strong>Contact:</strong> " + inquiry.getContactPerson() + " ("
                            + inquiry.getContactEmail() + ")</p>" +
                            "<p><strong>Scale:</strong> " + inquiry.getExpectedMembers() + " members, "
                            + inquiry.getExpectedCourses() + " courses</p>" +
                            "<p><strong>Requirements:</strong> " + inquiry.getRequirements() + "</p>");

            // Send acknowledgement to user
            emailService.sendHtmlEmail(
                    inquiry.getContactEmail(),
                    "We received your Enterprise Inquiry",
                    "<h1>Hello " + inquiry.getContactPerson() + "</h1>" +
                            "<p>Thank you for reaching out. Our enterprise strategy team has received your request for <strong>"
                            +
                            inquiry.getCompanyName() + "</strong>.</p>" +
                            "<p>We will review your requirements and get back to you within 24-48 business hours.</p>");
        } catch (Exception e) {
            log.error("Failed to send enterprise inquiry emails", e);
        }

        return saved;
    }

    public List<EnterpriseInquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    private Double getAsDouble(Object val, Double defaultVal) {
        if (val == null) return defaultVal;
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        try {
            return Double.parseDouble(val.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    private Integer getAsInteger(Object val, Integer defaultVal) {
        if (val == null) return defaultVal;
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        try {
            return Integer.parseInt(val.toString());
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public EnterpriseInquiry approveInquiry(String inquiryId, String adminId, Map<String, Object> config) {
        EnterpriseInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        // Provision the subscription
        subscriptionService.provisionManualEnterprise(
                inquiry.getUserId(),
                getAsDouble(config.get("commissionRate"), 0.03),
                getAsInteger(config.get("maxCourses"), 1000),
                getAsInteger(config.get("maxMembers"), inquiry.getExpectedMembers()),
                (String) config.getOrDefault("billingNotes", "Inquiry Approved"));

        inquiry.setStatus("APPROVED");
        inquiry.setProcessedBy(adminId);
        inquiry.setProcessedAt(Instant.now());
        inquiry.setAdminNotes((String) config.get("adminNotes"));

        return inquiryRepository.save(inquiry);
    }

    public EnterpriseInquiry rejectInquiry(String inquiryId, String adminId, String reason) {
        EnterpriseInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        inquiry.setStatus("REJECTED");
        inquiry.setProcessedBy(adminId);
        inquiry.setProcessedAt(Instant.now());
        inquiry.setAdminNotes(reason);

        return inquiryRepository.save(inquiry);
    }
}
