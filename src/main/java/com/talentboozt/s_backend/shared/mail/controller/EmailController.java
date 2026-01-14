package com.talentboozt.s_backend.shared.mail.controller;

import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import com.talentboozt.s_backend.shared.mail.dto.*;
import com.talentboozt.s_backend.shared.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v2/email")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/contact/{email}")
    public ResponseEntity<ApiResponse> contact(@PathVariable String email) {
        emailService.contactMe(email);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }

    @PostMapping("/contact-us")
    public ResponseEntity<ApiResponse> contactUs(@RequestBody ContactUsDTO contactUsDTO) throws IOException {
        emailService.contactUs(contactUsDTO);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }

    @PostMapping("/personal-contact")
    public ResponseEntity<ApiResponse> personalContact(@RequestBody PersonalContactDTO personalContactDTO) throws IOException {
        emailService.personalContact(personalContactDTO);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }

    @PostMapping("/bank-payment")
    public ResponseEntity<ApiResponse> bankPayment(@RequestBody BankPaymentDTO bankPaymentDTO) throws IOException {
        emailService.bankPayment(bankPaymentDTO);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }

    @PostMapping("/cv-request")
    public ResponseEntity<ApiResponse> requestResume(@RequestBody CVRequestDTO cvRequestDTO) throws IOException {
        emailService.requestResume(cvRequestDTO);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }

    @PostMapping("/leads")
    public ResponseEntity<ApiResponse> leads(@RequestBody LeadsDTO leadsDTO) throws IOException {
        emailService.leads(leadsDTO);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }
}
