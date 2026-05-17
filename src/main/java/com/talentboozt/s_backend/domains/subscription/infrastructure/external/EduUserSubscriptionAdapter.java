package com.talentboozt.s_backend.domains.subscription.infrastructure.external;

import com.talentboozt.s_backend.domains.edu.enums.ERoles;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import com.talentboozt.s_backend.domains.edu.service.EduSubscriptionService;
import com.talentboozt.s_backend.domains.subscription.application.dto.PortalSubscriptionMigrationRow;
import com.talentboozt.s_backend.domains.subscription.application.port.UserSubscriptionPort;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionStatus;
import com.talentboozt.s_backend.domains.subscription.domain.model.UserSubscription;
import com.talentboozt.s_backend.domains.subscription.infrastructure.mapping.LmsPlanAndStatusMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class EduUserSubscriptionAdapter implements UserSubscriptionPort {

    private final EduSubscriptionService eduSubscriptionService;
    private final EUserRepository userRepository;

    @Override
    public UserSubscription getLmsSubscriptionForUser(String userId) {
        ESubscriptions raw = eduSubscriptionService.getUserSubscription(userId);
        return toUserSubscription(raw);
    }

    @Override
    public SubscriptionPlanCode resolvePlanCodeFromUserProfile(String userId) {
        if (userId == null) {
            return SubscriptionPlanCode.FREE;
        }
        return userRepository.findById(userId)
                .map(EUser::getPlan)
                .map(LmsPlanAndStatusMapping::toPlanCode)
                .orElse(SubscriptionPlanCode.FREE);
    }

    @Override
    public boolean userExists(String userId) {
        return userId != null && userRepository.existsById(userId);
    }

    @Override
    public boolean applyUserPlanAndSellerRoles(String userId, SubscriptionPlanCode newPlan) {
        return userRepository.findById(userId).map(user -> {
            Set<ERoles> roles = new HashSet<>(Arrays.asList(user.getRoles() != null ? user.getRoles() : new ERoles[0]));

            roles.remove(ERoles.SELLER_PRO);
            roles.remove(ERoles.SELLER_PREMIUM);
            roles.remove(ERoles.ENTERPRISE_INSTRUCTOR);
            roles.remove(ERoles.SELLER_FREE);

            ESubscriptionPlan eduPlan = LmsPlanAndStatusMapping.toEduPlan(newPlan);
            switch (eduPlan) {
                case PRO:
                    roles.add(ERoles.SELLER_PRO);
                    break;
                case PREMIUM:
                    roles.add(ERoles.SELLER_PREMIUM);
                    break;
                case ENTERPRISE:
                    roles.add(ERoles.ENTERPRISE_INSTRUCTOR);
                    break;
                case FREE:
                default:
                    roles.add(ERoles.SELLER_FREE);
                    break;
            }

            user.setRoles(roles.toArray(new ERoles[0]));
            user.setPlan(eduPlan);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    @Override
    public void forEachUserForPortalSubscriptionMigration(Consumer<PortalSubscriptionMigrationRow> consumer) {
        userRepository.findAll().forEach(user -> consumer.accept(new PortalSubscriptionMigrationRow(
                user.getId(),
                LmsPlanAndStatusMapping.toPlanCode(user.getPlan()),
                LmsPlanAndStatusMapping.toSubscriptionStatus(user.getSubscriptionStatus()),
                user.getCreatedAt() != null ? user.getCreatedAt() : java.time.Instant.now())));
    }

    private static UserSubscription toUserSubscription(ESubscriptions e) {
        if (e == null) {
            return new UserSubscription(
                    "default-free",
                    null,
                    SubscriptionPlanCode.FREE,
                    SubscriptionStatus.ACTIVE,
                    null);
        }
        SubscriptionPlanCode plan = LmsPlanAndStatusMapping.toPlanCode(e.getPlan());
        SubscriptionStatus status = LmsPlanAndStatusMapping.toSubscriptionStatus(e.getStatus());
        return new UserSubscription(e.getId(), e.getUserId(), plan, status, e.getEndDate());
    }
}
