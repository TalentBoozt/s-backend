package com.talentboozt.s_backend.domains.payment.service;

import com.talentboozt.s_backend.domains.payment.model.RecordedCoursePayment;
import com.talentboozt.s_backend.domains.payment.repository.RecordedCoursePaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordedCoursePaymentService {

    private final RecordedCoursePaymentRepository paymentRepository;

    /**
     * Create a new payment record after successful transaction
     */
    public RecordedCoursePayment createPaymentRecord(
            String courseId,
            String courseName,
            String learnerId,
            String trainerId,
            BigDecimal grossAmount,
            BigDecimal netAmount,
            String currency,
            String splitType,
            String paymentMethod,
            String transactionId) {

        // Calculate split
        Map<String, BigDecimal> split = calculateRevenueSplit(splitType, netAmount);

        RecordedCoursePayment payment = new RecordedCoursePayment();
        payment.setCourseId(courseId);
        payment.setCourseName(courseName);
        payment.setLearnerId(learnerId);
        payment.setTrainerId(trainerId);
        payment.setGrossAmount(grossAmount);
        payment.setNetAmount(netAmount);
        payment.setCurrency(currency);
        payment.setTrainerAmount(split.get("trainer"));
        payment.setPlatformAmount(split.get("platform"));
        payment.setSplitType(splitType);
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus("success");
        payment.setTransactionId(transactionId);
        payment.setCreatedAt(Instant.now().toString());
        payment.setUpdatedAt(Instant.now().toString());

        return paymentRepository.save(payment);
    }

    /**
     * Marketplace revenue split rules (Udemy-like)
     */
    private Map<String, BigDecimal> calculateRevenueSplit(String splitType, BigDecimal netAmount) {
        BigDecimal trainerPercent;
        BigDecimal platformPercent;

        switch (splitType) {
            case "trainer-led" -> {
                trainerPercent = new BigDecimal("0.90");
                platformPercent = new BigDecimal("0.10");
            }
            case "promotion" -> {
                trainerPercent = new BigDecimal("0.30");
                platformPercent = new BigDecimal("0.70");
            }
            case "platform-led" -> {
                trainerPercent = new BigDecimal("0.40");
                platformPercent = new BigDecimal("0.60");
            }
            default -> {
                trainerPercent = new BigDecimal("0.50"); // adjustable range 0.3–0.5
                platformPercent = BigDecimal.ONE.subtract(trainerPercent);
            }
        }

        Map<String, BigDecimal> split = new HashMap<>();
        split.put("trainer", netAmount.multiply(trainerPercent));
        split.put("platform", netAmount.multiply(platformPercent));
        return split;
    }

    /**
     * Get trainer's earnings summary
     */
    public BigDecimal getTrainerTotalEarnings(String trainerId) {
        return paymentRepository.findSuccessfulPaymentsByTrainer(trainerId)
                .stream()
                .map(RecordedCoursePayment::getTrainerAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get earnings per course for a trainer
     */
    public Map<String, BigDecimal> getTrainerEarningsByCourse(String trainerId) {
        return paymentRepository.findSuccessfulPaymentsByTrainer(trainerId)
                .stream()
                .collect(Collectors.groupingBy(
                        RecordedCoursePayment::getCourseId,
                        Collectors.mapping(RecordedCoursePayment::getTrainerAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }
}

