package com.talentboozt.s_backend.shared.security.annotations;

import com.talentboozt.s_backend.shared.security.model.SecurityRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    SecurityRole[] value();
    boolean anyOf() default true; // true = OR (user needs any one), false = AND (user needs all)
}
