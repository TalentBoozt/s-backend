package com.talentboozt.s_backend.domains.subscription.infrastructure.external;

import com.talentboozt.s_backend.domains.edu.dto.plan.LimitConfig;
import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.service.PlanConfigService;
import com.talentboozt.s_backend.domains.subscription.application.dto.PlanLimitsSnapshot;
import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EduPlanCatalogAdapterTest {

    @Mock
    private PlanConfigService planConfigService;

    @InjectMocks
    private EduPlanCatalogAdapter adapter;

    @Test
    void delegatesToPlanConfigService_andMapsFields() {
        LimitConfig legacy = LimitConfig.builder()
                .maxCourses(3)
                .aiCreditsPerMonth(0)
                .maxAiGenerationsPerMonth(0)
                .validationCreditsPerMonth(0)
                .hourlyAiLimit(0)
                .dailyAiLimit(0)
                .commissionRate(0.07)
                .features(List.of("X"))
                .build();
        when(planConfigService.getPlanLimits(eq(ESubscriptionPlan.PRO))).thenReturn(legacy);

        PlanLimitsSnapshot out = adapter.getPlanLimits(SubscriptionPlanCode.PRO);

        verify(planConfigService).getPlanLimits(ESubscriptionPlan.PRO);
        assertThat(out.maxCourses()).isEqualTo(3);
        assertThat(out.aiCreditsPerMonth()).isZero();
        assertThat(out.commissionRate()).isEqualTo(0.07);
        assertThat(out.features()).containsExactly("X");
    }

    @Test
    void nullPlanCode_treatedAsFree() {
        LimitConfig free = LimitConfig.builder()
                .maxCourses(3)
                .aiCreditsPerMonth(0)
                .maxAiGenerationsPerMonth(0)
                .validationCreditsPerMonth(0)
                .hourlyAiLimit(0)
                .dailyAiLimit(0)
                .commissionRate(0.07)
                .features(List.of())
                .build();
        when(planConfigService.getPlanLimits(ESubscriptionPlan.FREE)).thenReturn(free);

        PlanLimitsSnapshot out = adapter.getPlanLimits(null);

        verify(planConfigService).getPlanLimits(ESubscriptionPlan.FREE);
        assertThat(out.maxCourses()).isEqualTo(3);
    }
}
