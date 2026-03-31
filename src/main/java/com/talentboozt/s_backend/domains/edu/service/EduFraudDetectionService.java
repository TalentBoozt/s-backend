package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EduFraudDetectionService {

    private final ETransactionsRepository transactionsRepository;

    public EduFraudDetectionService(ETransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    public void validateBulkPurchases(String buyerId, Map<String, Long> currentCartSellerCounts) {
        Instant last24h = Instant.now().minus(24, ChronoUnit.HOURS);
        
        // Ensure the repository exists method for this query or process differently
        // Wait, ETransactionsRepository might not have findByBuyerIdAndCreatedAtAfter.
        // Let's use findByBuyerId and filter.
        List<ETransactions> allTxs = transactionsRepository.findByBuyerId(buyerId);
        List<ETransactions> recentTxs = allTxs.stream()
                .filter(tx -> tx.getCreatedAt() != null && tx.getCreatedAt().isAfter(last24h))
                .collect(Collectors.toList());

        Map<String, Long> past24hCounts = recentTxs.stream()
                .filter(tx -> tx.getSellerId() != null && tx.getPaymentStatus() != null && !tx.getPaymentStatus().name().equals("FAILED"))
                .collect(Collectors.groupingBy(ETransactions::getSellerId, Collectors.counting()));

        for (Map.Entry<String, Long> current : currentCartSellerCounts.entrySet()) {
            String sellerId = current.getKey();
            long total = current.getValue() + past24hCounts.getOrDefault(sellerId, 0L);
            if (total > 5) {
                log.warn("FRAUD ALERT: User {} attempting to purchase {} total courses from Seller {} in 24 hours.",
                        buyerId, total, sellerId);
                throw new RuntimeException("Purchase anomaly detected: Purchasing >5 courses from the same creator in 24 hours is flagged as suspicious activity.");
            }
        }
    }
}
