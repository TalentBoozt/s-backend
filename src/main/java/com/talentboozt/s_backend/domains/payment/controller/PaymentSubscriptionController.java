package com.talentboozt.s_backend.domains.payment.controller;

import com.talentboozt.s_backend.domains.payment.model.PaymentSubscriptionsModel;
import com.talentboozt.s_backend.domains.payment.service.PaymentSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/subscriptions")
public class PaymentSubscriptionController {

    @Autowired
    private PaymentSubscriptionService subscriptionService;

    @GetMapping("/get/{companyId}")
    public ResponseEntity<PaymentSubscriptionsModel> getSubscription(@PathVariable String companyId) {
        PaymentSubscriptionsModel subscription = subscriptionService.getSubscription(companyId);
        if (subscription != null) {
            return ResponseEntity.ok(subscription);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/update/{companyId}")
    public ResponseEntity<PaymentSubscriptionsModel> updateSubscription(
            @PathVariable String companyId,
            @RequestBody PaymentSubscriptionsModel subscription) {
        PaymentSubscriptionsModel updatedSubscription = subscriptionService.updateSubscription(companyId, subscription);
        if (updatedSubscription != null) {
            return ResponseEntity.ok(updatedSubscription);
        }
        return ResponseEntity.notFound().build();
    }
}
