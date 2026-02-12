package com.talentboozt.s_backend.shared.tenant;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.repository.mongodb.CredentialsRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for tenant management operations
 * Provides caching and validation for tenant-related operations
 */
@Service
public class TenantService {

    private final CredentialsRepository credentialsRepository;

    public TenantService(CredentialsRepository credentialsRepository) {
        this.credentialsRepository = credentialsRepository;
    }

    /**
     * Get tenant context for current user
     */
    public TenantContext getCurrentTenantContext() {
        return TenantContext.getCurrent();
    }

    /**
     * Validate if user has access to the specified tenant/organization
     */
    public boolean validateTenantAccess(String userId, String organizationId) {
        TenantContext context = TenantContext.getCurrent();
        if (context == null || !context.isResolved()) {
            return false;
        }

        CredentialsModel user = getUserCredentials(userId);
        if (user == null) {
            return false;
        }

        List<Map<String, String>> organizations = user.getOrganizations();
        if (organizations == null || organizations.isEmpty()) {
            return false;
        }

        return organizations.stream()
                .anyMatch(org -> organizationId.equals(org.get("companyId")) ||
                                organizationId.equals(org.get("tenantId")));
    }

    /**
     * Get user credentials with caching
     */
    @Cacheable(value = "userCredentials", key = "#userId")
    public CredentialsModel getUserCredentials(String userId) {
        return credentialsRepository.findByEmployeeId(userId).orElse(null);
    }

    /**
     * Get primary organization for user
     */
    public Optional<String> getPrimaryOrganizationId(String userId) {
        CredentialsModel user = getUserCredentials(userId);
        if (user == null || user.getOrganizations() == null || user.getOrganizations().isEmpty()) {
            return Optional.empty();
        }

        Map<String, String> primaryOrg = user.getOrganizations().get(0);
        String orgId = primaryOrg.get("companyId");
        if (orgId == null) {
            orgId = primaryOrg.get("tenantId");
        }
        return Optional.ofNullable(orgId);
    }

    /**
     * Check if tenant context is properly set
     */
    public boolean isTenantContextSet() {
        TenantContext context = TenantContext.getCurrent();
        return context != null && context.isResolved();
    }
}
