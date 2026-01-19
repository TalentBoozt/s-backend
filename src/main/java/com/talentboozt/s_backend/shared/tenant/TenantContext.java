package com.talentboozt.s_backend.shared.tenant;

import lombok.Getter;
import lombok.Setter;

/**
 * Thread-local tenant context for multi-tenant isolation
 * Ensures tenant data isolation across request lifecycle
 */
public class TenantContext {
    
    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();
    
    @Getter
    @Setter
    private String tenantId;
    
    @Getter
    @Setter
    private String organizationId;
    
    @Getter
    @Setter
    private String userId;
    
    @Getter
    @Setter
    private String databaseName;
    
    @Getter
    @Setter
    private boolean resolved = false;
    
    public static TenantContext getCurrent() {
        TenantContext context = CONTEXT.get();
        if (context == null) {
            context = new TenantContext();
            CONTEXT.set(context);
        }
        return context;
    }
    
    public static void setCurrent(TenantContext context) {
        CONTEXT.set(context);
    }
    
    public static void clear() {
        CONTEXT.remove();
    }
    
    public static boolean isSet() {
        return CONTEXT.get() != null;
    }
    
    public void reset() {
        this.tenantId = null;
        this.organizationId = null;
        this.userId = null;
        this.databaseName = null;
        this.resolved = false;
    }
}
