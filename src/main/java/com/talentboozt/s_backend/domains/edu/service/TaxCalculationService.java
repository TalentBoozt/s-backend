package com.talentboozt.s_backend.domains.edu.service;

import org.springframework.stereotype.Service;

@Service
public class TaxCalculationService {

    /**
     * Calculates tax stub. 
     * In a production environment this should use Stripe Tax, TaxJar, or an internal engine
     * based on user location (IP/Address).
     */
    public TaxResult calculateTax(String userId, double amount) {
        // Stub implementation: 
        // We'll mock a default 15% rate for demonstration purposes.
        double rate = 0.15;
        double taxAmount = Math.round((amount * rate) * 100.0) / 100.0;
        
        TaxResult result = new TaxResult();
        result.rate = rate;
        result.taxAmount = taxAmount;
        result.totalAmount = amount + taxAmount;
        
        return result;
    }

    public static class TaxResult {
        public double rate;
        public double taxAmount;
        public double totalAmount;
    }
}
