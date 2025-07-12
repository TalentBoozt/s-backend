package com.talentboozt.s_backend.domains.payment.controller;

import com.talentboozt.s_backend.domains.payment.model.BillingHistoryModel;
import com.talentboozt.s_backend.domains.payment.service.BillingHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/billing/history")
public class BillingHistoryController {

    @Autowired
    private BillingHistoryService billingHistoryService;

    @GetMapping("/{companyId}")
    public ResponseEntity<List<BillingHistoryModel>> getBillingHistory(@PathVariable String companyId) {
        List<BillingHistoryModel> billingHistory = billingHistoryService.getBillingHistory(companyId);
        return ResponseEntity.ok(billingHistory);
    }
}

