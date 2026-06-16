# Phase 7 REVISED: Adjusted Target Structure

## Key Adjustment from Review

> [!IMPORTANT]  
> **Original plan** proposed a new `products/` top-level package. This would **break** the `PersistenceConfig` which explicitly scans `com.talentboozt.s_backend.domains` and `com.talentboozt.s_backend.shared`.
>
> **Revised plan** keeps the two existing top-level packages (`shared/` and `domains/`) but reorganizes `domains/` internally into product-grouped sub-packages. This is **zero-risk** for Spring component scanning, MongoDB repo discovery, and Redis repo scanning.

## Revised Target Structure

```
src/main/java/com/talentboozt/s_backend/
├── SBackendApplication.java
│
├── config/                              # Global configs (UNCHANGED)
│
├── shared/                              # Cross-product shared kernel
│   ├── identity/                        # NEW: Extracted from auth + user
│   │   ├── model/                       #   CredentialsModel, EmployeeModel, RoleModel
│   │   ├── repository/                  #   CredentialsRepository, EmployeeRepository
│   │   ├── service/                     #   CredentialsService, EmployeeService, RoleService
│   │   ├── port/                        #   UserLookupPort, WorkspaceSyncPort (NEW interfaces)
│   │   └── dto/
│   │
│   ├── auth/                            # NEW: Auth controllers & OAuth from domains/auth
│   │   ├── controller/                  #   AuthController, OAuth*, SSO, PasswordReset, Role, Permission
│   │   ├── service/                     #   AuthService, CustomUserDetailsService, OAuth services
│   │   ├── model/                       #   PasswordResetToken
│   │   └── repository/
│   │
│   ├── security/                        # EXISTING (refined)
│   ├── mail/                            # EXISTING (unchanged)
│   ├── audit/                           # NEW: Merged audit_logs + sys_tracking + audit
│   ├── payment/                         # NEW: Moved from domains/payment + billing
│   ├── ai/                              # NEW: Consolidated ai_tool + ai_assistant + ai_orchestration
│   ├── subscription/                    # NEW: Moved from domains/subscription
│   ├── notification/                    # NEW: Merged domains/notifications + automation + communication
│   ├── infrastructure/                  # NEW: Merged utilities
│   │   ├── tenant/                      #   Existing shared/tenant
│   │   ├── scheduler/                   #   Existing shared/scheduler
│   │   ├── monitoring/                  #   Existing shared/monitoring
│   │   ├── async/                       #   Existing shared/async
│   │   ├── events/                      #   Existing shared/events
│   │   ├── filter/                      #   Existing shared/filter
│   │   ├── storage/                     #   FileUploadController + drive
│   │   └── utils/                       #   Existing shared/utils
│   │
│   ├── realtime/                        # EXISTING (unchanged)
│   ├── dto/                             # EXISTING (unchanged) + common DTOs
│   └── common/                          # NEW: ApiResponse, ErrorResponse from domains/common/dto
│
└── domains/                             # Product-specific bounded contexts
    │
    ├── portal/                          # TalentBoozt Portal product
    │   ├── job_portal/                  #   Merged com_job_portal + plat_job_portal + jobs + recruiter + interviews
    │   ├── courses/                     #   Merged com_courses + plat_courses
    │   ├── community/                   #   Posts, comments, voting, moderation
    │   ├── messaging/                   #   WebSocket chat
    │   ├── content/                     #   Articles + announcements
    │   ├── reputation/                  #   Reputation scoring
    │   ├── resume/                      #   Resume builder + ATS
    │   ├── ambassador/                  #   Ambassador program
    │   ├── referral/                    #   Referral system
    │   ├── user_profile/                #   Emp* profile sub-entities (skills, edu, etc.)
    │   ├── pipeline/                    #   Hiring pipeline
    │   ├── recommendations/             #   Job recommendations
    │   ├── activity/                    #   Activity feed
    │   └── support/                     #   Support tickets
    │
    ├── edu/                             # EduPlatform LMS (MOSTLY UNCHANGED internally)
    │   └── (all existing edu/ files stay here)
    │
    ├── finance/                         # FinancePlanr (renamed from finance_planning)
    │   └── (all existing finance_planning/ files stay here)
    │
    ├── lifeplanner/                     # LifePlanr (UNCHANGED)
    │   └── (all existing lifeplanner/ files stay here)
    │
    ├── leads/                           # LeadEngine CRM (UNCHANGED)
    │   └── (all existing leads/ files stay here)
    │
    ├── marketplace/                     # Software Marketplace (renamed from software_marketplace)
    │   └── (all existing software_marketplace/ files stay here)
    │
    ├── admin/                           # Platform Admin (from _private + common admin parts)
    │   ├── controller/                  #   UserManagement, Metrics, Whitelist, Captcha, Redirect
    │   ├── service/                     #   Admin services
    │   └── model/                       #   Feature, Issue models
    │
    └── _public/                         # Public APIs (UNCHANGED)
        └── (all existing _public/ files stay here)
```

## Execution Order (17 Phases)

| # | Phase | Files Moved | Risk |
|---|---|---|---|
| 1 | Create port interfaces in shared/ | ~5 new files | None |
| 2 | Move shared/infrastructure (utils→infra, async, events, filter, monitoring→infra) | ~15 files | Low |
| 3 | Extract shared/identity (CredentialsModel, EmployeeModel, RoleModel + repos + services) | ~20 files | **HIGH** |
| 4 | Extract shared/auth (auth controllers + OAuth services) | ~25 files | **HIGH** |
| 5 | Merge shared/audit (audit_logs + sys_tracking + audit) | ~83 files | Medium |
| 6 | Move shared/payment (payment + billing) | ~40 files | Medium |
| 7 | Consolidate shared/ai (ai_tool + ai_assistant + ai_orchestration) | ~28 files | Low |
| 8 | Move shared/subscription | ~29 files | Medium |
| 9 | Move shared/notification (notifications + automation + communication) | ~10 files | Low |
| 10 | Restructure portal/ (community, messaging, content) | ~85 files | Medium |
| 11 | Merge portal/job_portal (com_job_portal + plat_job_portal + jobs etc.) | ~45 files | Medium |
| 12 | Merge portal/courses (com_courses + plat_courses) | ~84 files | Medium |
| 13 | Move portal/ remaining (ambassador, referral, reputation, resume, etc.) | ~60 files | Low |
| 14 | Rename finance_planning → domains/finance | ~98 files | Low |
| 15 | Move domains/admin (from _private + common admin) | ~30 files | Low |
| 16 | Distribute remaining common/ files | ~10 files | Low |
| 17 | Final compilation + test validation | 0 files | — |

**Total files affected: ~1,475** across all phases.
