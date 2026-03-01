package com.talentboozt.s_backend.domains.auth.constant;

public class PermissionConstants {
    // System Level
    public static final String SYSTEM_MANAGE = "system:manage";
    public static final String SYSTEM_VIEW_AUDIT = "system:view_audit";
    public static final String SYSTEM_BYPASS_BILLING = "system:bypass_billing";

    // Company Level (Tenant)
    public static final String COMPANY_MANAGE = "company:manage";
    public static final String COMPANY_BILLING = "company:billing";
    public static final String COMPANY_USERS = "company:users";

    // Ambassador Level
    public static final String AMBASSADOR_DASHBOARD = "ambassador:dashboard";
    public static final String AMBASSADOR_TASKS = "ambassador:tasks";
    public static final String AMBASSADOR_REWARDS = "ambassador:rewards";
    public static final String AMBASSADOR_MANAGE = "ambassador:manage"; // For system admins

    // Content Level
    public static final String CONTENT_CREATE = "content:create";
    public static final String CONTENT_MODERATE = "content:moderate";
    public static final String CONTENT_VIEW_PRIVATE = "content:view_private";

    // Training Level
    public static final String TRAINING_MANAGE = "training:manage";
    public static final String TRAINING_ATTEND = "training:attend";

    // Member Level
    public static final String MEMBER_READ = "member:read";
    public static final String MEMBER_POST = "member:post";
    public static final String MEMBER_COMMENT = "member:comment";

    // Job Level
    public static final String JOB_POST = "job:post";
    public static final String JOB_MANAGE = "job:manage";
    public static final String RESUME_VIEW = "resume:view";
    public static final String APPLICANT_MANAGE = "applicant:manage";

    // Role Names
    public static final String ROLE_MEMBER = "MEMBER";
    public static final String ROLE_AMBASSADOR = "AMBASSADOR";
    public static final String ROLE_RECRUITER = "RECRUITER";
    public static final String ROLE_OWNER = "OWNER";
    public static final String ROLE_SYSTEM_ADMIN = "SYSTEM_ADMIN";
}
