package com.talentboozt.s_backend.domains.payment.controller;

import com.talentboozt.s_backend.domains.payment.dto.PaymentRequestDto;
import com.talentboozt.s_backend.domains.payment.model.RecordedCoursePayment;
import com.talentboozt.s_backend.domains.payment.service.RecordedCoursePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/payments/recorded")
@RequiredArgsConstructor
public class RecordedCoursePaymentController {

    private final RecordedCoursePaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<RecordedCoursePayment> createPayment(
            @RequestBody PaymentRequestDto dto) {
        RecordedCoursePayment payment = paymentService.createPaymentRecord(
                dto.getCourseId(),
                dto.getCourseName(),
                dto.getLearnerId(),
                dto.getTrainerId(),
                dto.getGrossAmount(),
                dto.getNetAmount(),
                dto.getCurrency(),
                dto.getSplitType(),
                dto.getPaymentMethod(),
                dto.getTransactionId()
        );
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/trainer/{trainerId}/earnings")
    public ResponseEntity<BigDecimal> getTrainerTotalEarnings(@PathVariable String trainerId) {
        return ResponseEntity.ok(paymentService.getTrainerTotalEarnings(trainerId));
    }

    @GetMapping("/trainer/{trainerId}/earnings-by-course")
    public ResponseEntity<Map<String, BigDecimal>> getTrainerEarningsByCourse(
            @PathVariable String trainerId) {
        return ResponseEntity.ok(paymentService.getTrainerEarningsByCourse(trainerId));
    }
}

