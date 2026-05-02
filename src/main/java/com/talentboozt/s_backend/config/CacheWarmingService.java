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
        // Real application: Warm up platform admins and frequently active users
        try {
            credentialsRepository.findAll().stream()
                .filter(c -> "PLATFORM_ADMIN".equals(c.getPlatformRole()))
                .limit(100) // Don't overwhelm on startup
                .forEach(c -> {
                    tenantService.getUserCredentials(c.getEmployeeId());
                    logger.debug("Warmed up userCredentials for admin: {}", c.getEmployeeId());
                });
        } catch (Exception e) {
            logger.warn("Failed to warm up admin credentials. Error: {}", e.getMessage());
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
