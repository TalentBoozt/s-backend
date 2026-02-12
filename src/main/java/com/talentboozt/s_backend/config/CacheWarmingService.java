package com.talentboozt.s_backend.config;

import com.talentboozt.s_backend.domains.auth.repository.mongodb.CredentialsRepository;
import com.talentboozt.s_backend.shared.tenant.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheWarmingService {

    private static final Logger logger = LoggerFactory.getLogger(CacheWarmingService.class);

    @Autowired
    private TenantService tenantService;

    @Autowired
    private CredentialsRepository credentialsRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void warmUpCaches() {
        logger.info("Starting cache warming...");

        // Warm up userCredentials cache
        warmUpUserCredentialsCache();

        // Warm up organizations cache (if a service method exists to get all organizations)
        // warmUpOrganizationsCache(); 

        logger.info("Cache warming completed.");
    }

    private void warmUpUserCredentialsCache() {
        // Example: Warm up a few critical user credentials (e.g., admin users)
        // In a real application, this might involve querying for frequently accessed users
        List<String> userIdsToWarm = List.of("admin1", "support_user"); // Replace with actual user IDs

        for (String userId : userIdsToWarm) {
            try {
                tenantService.getUserCredentials(userId);
                logger.debug("Warmed up userCredentials for userId: {}", userId);
            } catch (Exception e) {
                logger.warn("Failed to warm up userCredentials for userId: {}. Error: {}", userId, e.getMessage());
            }
        }
    }

    // private void warmUpOrganizationsCache() {
    //     // Example: Warm up all organizations
    //     // This assumes there's a service method like organizationService.getAllOrganizations()
    //     // organizationService.getAllOrganizations().forEach(org -> {
    //     //     logger.debug("Warmed up organization: {}", org.getId());
    //     // });
    // }
}
