package com.talentboozt.s_backend.domains.subscription.infrastructure.mapping;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionStatus;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LmsPlanAndStatusMappingTest {

    @Test
    void plan_roundTrip_matches() {
        for (ESubscriptionPlan edu : ESubscriptionPlan.values()) {
            SubscriptionPlanCode code = LmsPlanAndStatusMapping.toPlanCode(edu);
            assertThat(LmsPlanAndStatusMapping.toEduPlan(code)).isEqualTo(edu);
        }
    }

    @Test
    void status_roundTrip_matches() {
        for (ESubscriptionStatus edu : ESubscriptionStatus.values()) {
            SubscriptionStatus s = LmsPlanAndStatusMapping.toSubscriptionStatus(edu);
            assertThat(LmsPlanAndStatusMapping.toEduStatus(s)).isEqualTo(edu);
        }
    }
}
