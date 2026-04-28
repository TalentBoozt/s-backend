package com.talentboozt.s_backend.domains.subscription.service;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import com.talentboozt.s_backend.domains.subscription.repository.mongodb.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionMigrationService implements CommandLineRunner {

    private final EUserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void run(String... args) {
        // Run migration only if no subscriptions exist or specifically requested
        if (subscriptionRepository.count() == 0) {
            log.info("Starting subscription migration for existing users...");
            migrateUsers();
        }
    }

    private void migrateUsers() {
        userRepository.findAll().forEach(user -> {
            if (subscriptionRepository.findByUserId(user.getId()).isEmpty()) {
                Subscription sub = Subscription.builder()
                        .userId(user.getId())
                        .plan(user.getPlan() != null ? user.getPlan() : ESubscriptionPlan.FREE)
                        .status(user.getSubscriptionStatus() != null ? user.getSubscriptionStatus() : ESubscriptionStatus.ACTIVE)
                        .startDate(user.getCreatedAt())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                subscriptionRepository.save(sub);
                log.info("Migrated subscription for user: {}", user.getEmail());
            }
        });
        log.info("Subscription migration completed.");
    }
}
