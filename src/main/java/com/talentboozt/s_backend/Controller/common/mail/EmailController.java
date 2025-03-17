package com.talentboozt.s_backend.Controller.common.mail;

import com.talentboozt.s_backend.DTO.common.ApiResponse;
import com.talentboozt.s_backend.DTO.common.mail.BankPaymentDTO;
import com.talentboozt.s_backend.DTO.common.mail.CVRequestDTO;
import com.talentboozt.s_backend.DTO.common.mail.ContactUsDTO;
import com.talentboozt.s_backend.DTO.common.mail.PersonalContactDTO;
import com.talentboozt.s_backend.Service.common.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse> contactUs(@RequestBody ContactUsDTO contactUsDTO) {
        emailService.contactUs(contactUsDTO);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }

    @PostMapping("/personal-contact")
    public ResponseEntity<ApiResponse> personalContact(@RequestBody PersonalContactDTO personalContactDTO) {
        emailService.personalContact(personalContactDTO);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }

    @PostMapping("/bank-payment")
    public ResponseEntity<ApiResponse> bankPayment(@RequestBody BankPaymentDTO bankPaymentDTO) {
        emailService.bankPayment(bankPaymentDTO);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }

    @PostMapping("/cv-request")
    public ResponseEntity<ApiResponse> requestResume(@RequestBody CVRequestDTO cvRequestDTO) {
        emailService.requestResume(cvRequestDTO);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully"));
    }
}
