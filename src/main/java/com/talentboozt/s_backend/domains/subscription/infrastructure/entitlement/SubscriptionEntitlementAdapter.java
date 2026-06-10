package com.talentboozt.s_backend.domains.subscription.infrastructure.entitlement;

import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import com.talentboozt.s_backend.domains.subscription.application.entitlement.EntitlementResolutionResult;
import com.talentboozt.s_backend.domains.subscription.application.entitlement.SubscriptionEntitlementSnapshot;
import com.talentboozt.s_backend.domains.subscription.application.entitlement.UserEntitlement;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.model.Subscription;
import com.talentboozt.s_backend.domains.subscription.service.SubscriptionService;
import com.talentboozt.s_backend.shared.security.port.EntitlementPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class SubscriptionEntitlementAdapter implements EntitlementPort {

    private final EUserRepository userRepository;
    private final SubscriptionService subscriptionService;

    @Override
    public Optional<EntitlementResolutionResult> resolveByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toResult);
    }

    private EntitlementResolutionResult toResult(EUser user) {
        Set<String> roleNames = new java.util.HashSet<>();
        boolean isEnterprise = false;
        ERoles[] roles = user.getRoles();
        if (roles != null) {
            for (ERoles r : roles) {
                roleNames.add(r.name());
                if (r == ERoles.ENTERPRISE_ADMIN || r == ERoles.ENTERPRISE_MANAGER || r == ERoles.ENTERPRISE_INSTRUCTOR) {
                    isEnterprise = true;
                }
            }
        }

        SubscriptionPlanCode plan;
        if (isEnterprise) {
            plan = SubscriptionPlanCode.ENTERPRISE;
        } else {
            Subscription sub = subscriptionService.getActiveSubscription(user.getId());
            plan = sub != null && sub.getPlan() != null ? sub.getPlan() : SubscriptionPlanCode.FREE;
        }
        int tierOrdinal = plan.ordinal();

        var snapshot = new SubscriptionEntitlementSnapshot(tierOrdinal);
        var entitlements = new UserEntitlement(roleNames, snapshot);
        return new EntitlementResolutionResult(user.getId(), entitlements);
    }
}
