package com.talentboozt.s_backend.domains.feature_flags.service;

import com.talentboozt.s_backend.domains.feature_flags.model.FeatureFlagModel;
import com.talentboozt.s_backend.domains.feature_flags.repository.mongodb.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeatureFlagService {
    private final FeatureFlagRepository repository;

    public boolean isEnabled(String key, String userId, String orgId, String role, String tier) {
        Optional<FeatureFlagModel> flagOpt = repository.findByKey(key);
        if (flagOpt.isEmpty()) return false;
        
        FeatureFlagModel flag = flagOpt.get();
        if (flag.isGlobalEnabled()) return true;
        
        if (flag.getWhitelistedUserIds() != null && flag.getWhitelistedUserIds().contains(userId)) return true;
        if (flag.getWhitelistedOrgIds() != null && flag.getWhitelistedOrgIds().contains(orgId)) return true;
        
        if (flag.getEnabledForRoles() != null && flag.getEnabledForRoles().contains(role)) return true;
        if (flag.getEnabledForTiers() != null && flag.getEnabledForTiers().contains(tier)) return true;
        
        return false;
    }
}
