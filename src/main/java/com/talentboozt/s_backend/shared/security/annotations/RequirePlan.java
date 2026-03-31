package com.talentboozt.s_backend.shared.security.annotations;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePlan {
    ESubscriptionPlan[] value();
}
