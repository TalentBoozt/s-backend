package com.talentboozt.s_backend.domains.payment.controller;

import com.talentboozt.s_backend.domains.payment.model.SubscriptionsModel;
import com.talentboozt.s_backend.domains.payment.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping("/get/{companyId}")
    public ResponseEntity<SubscriptionsModel> getSubscription(@PathVariable String companyId) {
        SubscriptionsModel subscription = subscriptionService.getSubscription(companyId);
        if (subscription != null) {
            return ResponseEntity.ok(subscription);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/update/{companyId}")
    public ResponseEntity<SubscriptionsModel> updateSubscription(
            @PathVariable String companyId,
            @RequestBody SubscriptionsModel subscription) {
        SubscriptionsModel updatedSubscription = subscriptionService.updateSubscription(companyId, subscription);
        if (updatedSubscription != null) {
            return ResponseEntity.ok(updatedSubscription);
        }
        return ResponseEntity.notFound().build();
    }
}
