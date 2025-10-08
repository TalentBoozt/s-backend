package com.talentboozt.s_backend.domains.payment.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.talentboozt.s_backend.domains.com_courses.dto.InstallmentDTO;
import com.talentboozt.s_backend.domains.payment.dto.PaymentRequestDto;
import com.talentboozt.s_backend.domains.payment.model.RecordedCoursePayment;
import com.talentboozt.s_backend.domains.payment.service.RecordedCoursePaymentService;
import com.talentboozt.s_backend.domains.payment.service.StripeService;
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
    private final StripeService stripeService;

    @PostMapping("/create")
    public ResponseEntity<RecordedCoursePayment> createPayment(
            @RequestBody PaymentRequestDto dto) {
        RecordedCoursePayment payment = paymentService.createPaymentRecord(
                dto.getCourseId(),
                dto.getCourseName(),
                dto.getLearnerId(),
                dto.getTrainerId(),
                dto.getCompanyId(),
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

    @PostMapping("/create/stripe/product/{courseName}")
    public InstallmentDTO createProduct(@RequestBody InstallmentDTO installment,
                                        @PathVariable String courseName) throws StripeException {
        Product product = stripeService.createProduct(
                courseName,
                installment.getName() + " for " + courseName + " course."
        );

        String currency = switch (installment.getCurrency()) {
            case "€" -> "eur";
            case "£" -> "gbp";
            case "Rs" -> "lkr";
            case "¥" -> "jpy";
            case "₹" -> "inr";
            default -> "usd";
        };

        long defaultAmount = toStripeAmount(installment.getPrice(), currency);
        Price defaultPrice = stripeService.createPriceForCourse(product.getId(), defaultAmount, currency);

        installment.setProductId(product.getId());
        installment.setPriceId(defaultPrice.getId());
        installment.setPriceType("default");

        if (installment.getDiscountedPrice() != null && !installment.getDiscountedPrice().isBlank()) {
            long discountedAmount = toStripeAmount(installment.getDiscountedPrice(), currency);
            Price discountedPrice = stripeService.createPriceForCourse(product.getId(), discountedAmount, currency);

            installment.setDiscountedPriceId(discountedPrice.getId());
            installment.setPriceType("discounted");
        }

        return installment;
    }

    private long toStripeAmount(String price, String currency) {
        if ("jpy".equals(currency)) {
            return Long.parseLong(price);
        } else {
            double amount = Double.parseDouble(price);
            return Math.round(amount * 100);
        }
    }
}

