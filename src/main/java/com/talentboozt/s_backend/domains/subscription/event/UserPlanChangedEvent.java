package com.talentboozt.s_backend.domains.subscription.event;

import com.talentboozt.s_backend.domains.subscription.domain.model.SubscriptionPlanCode;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserPlanChangedEvent extends ApplicationEvent {
    private final String userId;
    private final SubscriptionPlanCode newPlan;

    public UserPlanChangedEvent(Object source, String userId, SubscriptionPlanCode newPlan) {
        super(source);
        this.userId = userId;
        this.newPlan = newPlan;
    }
}
