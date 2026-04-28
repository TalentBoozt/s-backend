package com.talentboozt.s_backend.domains.subscription.event;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserPlanChangedEvent extends ApplicationEvent {
    private final String userId;
    private final ESubscriptionPlan newPlan;

    public UserPlanChangedEvent(Object source, String userId, ESubscriptionPlan newPlan) {
        super(source);
        this.userId = userId;
        this.newPlan = newPlan;
    }
}
