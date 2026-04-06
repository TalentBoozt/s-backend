package com.talentboozt.s_backend.domains.edu.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Normalizes currency to USD or the platform's base currency.
 * In a real application, this would integrate with an exchange rate provider (e.g., OpenExchangeRates)
 * to perform real-time conversion.
 */
@Service
public class CurrencyService {

    // Simple mock exchange rate table (1 unit of currency to USD)
    private static final Map<String, Double> EXCHANGE_RATES;
    
    static {
        EXCHANGE_RATES = new HashMap<>();
        EXCHANGE_RATES.put("usd", 1.0);
        EXCHANGE_RATES.put("eur", 1.08); // 1 EUR = 1.08 USD
        EXCHANGE_RATES.put("gbp", 1.25);
        EXCHANGE_RATES.put("inr", 0.012);
        EXCHANGE_RATES.put("aud", 0.65);
    }

    /**
     * Converts a given amount from its source currency to USD base currency.
     * @param amount The original amount
     * @param currencyCode The source currency code (e.g. "eur", "gbp")
     * @return The normalized amount in USD
     */
    public double normalizeToUSD(double amount, String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            return amount;
        }

        String code = currencyCode.toLowerCase();
        if ("usd".equals(code)) {
            return amount;
        }

        Double rate = EXCHANGE_RATES.get(code);
        if (rate == null) {
            // Unrecognized currency fallback
            return amount;
        }

        return amount * rate;
    }

    /**
     * Calculate amount in target currency from USD
     */
    public double convertFromUSD(double usdAmount, String targetCurrency) {
        if (targetCurrency == null || targetCurrency.isBlank() || "usd".equalsIgnoreCase(targetCurrency)) {
            return usdAmount;
        }

        Double rate = EXCHANGE_RATES.get(targetCurrency.toLowerCase());
        if (rate == null || rate == 0) {
            return usdAmount;
        }

        return usdAmount / rate;
    }
}
