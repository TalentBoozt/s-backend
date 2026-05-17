package com.talentboozt.s_backend.domains.subscription.service;

import com.talentboozt.s_backend.domains.subscription.application.port.UserSubscriptionPort;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionStatus;
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

    private final UserSubscriptionPort userSubscriptionPort;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public void run(String... args) {
        if (subscriptionRepository.count() == 0) {
            log.info("Starting subscription migration for existing users...");
            migrateUsers();
        }
    }

    private void migrateUsers() {
        userSubscriptionPort.forEachUserForPortalSubscriptionMigration(row -> {
            if (subscriptionRepository.findByUserId(row.userId()).isEmpty()) {
                SubscriptionPlanCode plan = row.plan() != null ? row.plan() : SubscriptionPlanCode.FREE;
                SubscriptionStatus status = row.status() != null ? row.status() : SubscriptionStatus.ACTIVE;
                Subscription sub = Subscription.builder()
                        .userId(row.userId())
                        .plan(plan)
                        .status(status)
                        .startDate(row.accountCreatedAt())
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                subscriptionRepository.save(sub);
                log.info("Migrated subscription for user: {}", row.userId());
            }
        });
        log.info("Subscription migration completed.");
    }
}
