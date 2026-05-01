package com.talentboozt.s_backend.domains.finance_planning.security.annotations;

import com.talentboozt.s_backend.domains.finance_planning.security.rbac.FinPermission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresFinPermission {
    FinPermission value();
    
    /**
     * Source of Organization ID: "header", "path", "param", or "request"
     */
    String orgIdSource() default "header";
    
    /**
     * Key name for Organization ID (e.g., "X-Organization-Id" for header, "organizationId" for path)
     */
    String orgIdKey() default "X-Organization-Id";
    
    /**
     * Source of Project ID: "none", "path", "param", or "body"
     */
    String projectIdSource() default "none";
    
    /**
     * Key name for Project ID
     */
    String projectIdKey() default "projectId";
}
