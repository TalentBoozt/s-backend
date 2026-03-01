package com.talentboozt.s_backend.domains.payment.controller;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.domains.payment.model.*;
import com.talentboozt.s_backend.domains.payment.service.*;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/subscriptions")
public class BillingPortalController {

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private BillingHistoryService billingHistoryService;
    @Autowired
    private InvoicesService invoicesService;
    @Autowired
    private PaymentMethodService paymentMethodService;
    @Autowired
    private UsageDataService usageDataService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private CredentialsService credentialsService;

    private String getCompanyId(HttpServletRequest request) {
        String token = jwtService.extractTokenFromHeaderOrCookie(request);
        if (token == null || !jwtService.validateToken(token))
            return null;
        CredentialsModel userTokenInfo = jwtService.getUserFromToken(token);
        Optional<CredentialsModel> fullUser = credentialsService
                .getCredentialsByEmployeeId(userTokenInfo.getEmployeeId());
        return fullUser.map(CredentialsModel::getCompanyId).orElse(null);
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentSubscription(HttpServletRequest request) {
        String companyId = getCompanyId(request);
        if (companyId == null)
            return ResponseEntity.status(401).build();

        SubscriptionsModel sub = subscriptionService.getSubscription(companyId);
        return sub != null ? ResponseEntity.ok(sub) : ResponseEntity.notFound().build();
    }

    @GetMapping("/history")
    public ResponseEntity<?> getBillingHistory(HttpServletRequest request) {
        String companyId = getCompanyId(request);
        if (companyId == null)
            return ResponseEntity.status(401).build();

        List<BillingHistoryModel> history = billingHistoryService.getBillingHistory(companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("content", history);
        response.put("total", history.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoices")
    public ResponseEntity<?> getInvoices(HttpServletRequest request) {
        String companyId = getCompanyId(request);
        if (companyId == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(invoicesService.getInvoicesByCompanyId(companyId));
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<?> getPaymentMethods(HttpServletRequest request) {
        String companyId = getCompanyId(request);
        if (companyId == null)
            return ResponseEntity.status(410).build();

        return ResponseEntity.ok(paymentMethodService.getPaymentMethods(companyId));
    }

    @GetMapping("/usage")
    public ResponseEntity<?> getUsage(HttpServletRequest request) {
        String companyId = getCompanyId(request);
        if (companyId == null)
            return ResponseEntity.status(401).build();

        UsageDataModel usage = usageDataService.getUsageData(companyId);
        if (usage == null) {
            usage = new UsageDataModel();
            usage.setCompanyId(companyId);
            usage.setUsers(0);
            usage.setStorage(0);
            usage.setBandwidth(0);
        }
        return ResponseEntity.ok(usage);
    }
}
