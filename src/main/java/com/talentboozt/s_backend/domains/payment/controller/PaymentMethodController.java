package com.talentboozt.s_backend.domains.payment.controller;

import com.talentboozt.s_backend.domains.payment.model.PaymentMethodsModel;
import com.talentboozt.s_backend.domains.payment.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/payment-methods")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @GetMapping("/get/{companyId}")
    public ResponseEntity<List<PaymentMethodsModel>> getPaymentMethods(@PathVariable String companyId) {
        List<PaymentMethodsModel> paymentMethods = paymentMethodService.getPaymentMethods(companyId);
        return ResponseEntity.ok(paymentMethods);
    }

    @PostMapping("/update/{companyId}")
    public ResponseEntity<PaymentMethodsModel> addPaymentMethod(
            @PathVariable String companyId,
            @RequestBody PaymentMethodsModel paymentMethod) {
        PaymentMethodsModel addedPaymentMethod = paymentMethodService.addPaymentMethod(companyId, paymentMethod);
        return ResponseEntity.ok(addedPaymentMethod);
    }
}

