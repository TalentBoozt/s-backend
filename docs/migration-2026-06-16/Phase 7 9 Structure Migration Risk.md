# Phase 7: Proposed Target Structure

## 7.1 Design Principles

1. **DDD-first organization** — Bounded contexts are the primary organizing unit
2. **Shared kernel isolation** — Cross-product domains extracted to `shared/`
3. **Product autonomy** — Each product owns its full vertical slice
4. **Port/Adapter pattern** — Shared modules expose ports; products implement adapters
5. **Acyclic dependency rule** — No circular imports between modules
6. **Direction of dependency** — Products depend on shared; shared never depends on products

## 7.2 Target Folder Structure

```
src/main/java/com/talentboozt/s_backend/
├── SBackendApplication.java
│
├── config/                                    # Global Spring configs (unchanged)
│   ├── AppConfig.java
│   ├── AsyncConfig.java
│   ├── CacheConfig.java
│   ├── CorsConfig.java
│   ├── MongoConfig.java
│   ├── RedisConfig.java
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   └── ... (other configs)
│
├── shared/                                    # Cross-product shared kernel
│   ├── identity/                              # User Identity (from: auth + user)
│   │   ├── model/
│   │   │   ├── CredentialsModel.java
│   │   │   ├── EmployeeModel.java
│   │   │   ├── RoleModel.java
│   │   │   ├── PermissionModel.java
│   │   │   └── PlatformRole.java
│   │   ├── repository/
│   │   │   ├── CredentialsRepository.java
│   │   │   └── EmployeeRepository.java
│   │   ├── service/
│   │   │   ├── CredentialsService.java
│   │   │   ├── EmployeeService.java
│   │   │   ├── RoleService.java
│   │   │   └── UserPermissionsService.java
│   │   ├── port/
│   │   │   ├── UserLookupPort.java            # NEW: interface for user lookups
│   │   │   └── WorkspaceSyncPort.java         # NEW: interface for workspace sync
│   │   └── dto/
│   │       ├── EmployeeDTO.java
│   │       └── CredentialsDTO.java
│   │
│   ├── auth/                                  # Authentication & Authorization
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── OAuthController.java
│   │   │   ├── PasswordResetController.java
│   │   │   ├── RoleController.java
│   │   │   ├── PermissionController.java
│   │   │   ├── SsoAuthController.java
│   │   │   └── ... (OAuth controllers)
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── PasswordResetService.java
│   │   │   └── ... (OAuth services)
│   │   ├── model/
│   │   │   └── PasswordResetToken.java
│   │   └── repository/
│   │       └── PasswordResetTokenRepository.java
│   │
│   ├── security/                              # Security infrastructure (existing, refined)
│   │   ├── cfg/                               # Filters, interceptors
│   │   ├── controller/                        # Token, Proxy, Key, Sitemap
│   │   ├── service/                           # JWT, GeoLocation, Rate limiting
│   │   ├── annotations/                       # @RequireRole, @RequirePlan, @AuthenticatedUser
│   │   ├── interceptor/                       # RbacInterceptor
│   │   ├── port/                              # EntitlementPort, ApiKeyAuthenticationPort
│   │   ├── model/                             # CustomUserDetails, TokenModel, SecurityRole
│   │   └── dto/                               # SessionContext, IpGeoData
│   │
│   ├── notification/                          # Consolidated notifications
│   │   ├── service/
│   │   │   ├── NotificationService.java
│   │   │   └── RealtimeBroadcaster.java
│   │   ├── model/
│   │   │   └── NotificationModel.java
│   │   ├── controller/
│   │   │   └── NotificationController.java
│   │   └── port/
│   │       └── NotificationPort.java          # NEW: products use this to send notifications
│   │
│   ├── audit/                                 # Consolidated audit & tracking
│   │   ├── controller/
│   │   │   ├── AuditLogController.java
│   │   │   ├── TrackingController.java
│   │   │   └── MonitoringController.java
│   │   ├── service/
│   │   │   ├── AuditLogService.java
│   │   │   ├── TrackingService.java
│   │   │   ├── SchedulerLoggerService.java
│   │   │   └── AsyncUpdateLogger.java
│   │   ├── model/
│   │   │   ├── AuditLogModel.java
│   │   │   ├── TrackingEvent.java
│   │   │   └── SchedulerLogModel.java
│   │   ├── repository/
│   │   └── port/
│   │       └── AuditPort.java                 # NEW: domain-agnostic audit interface
│   │
│   ├── payment/                               # Consolidated payment + billing
│   │   ├── controller/
│   │   │   ├── PaymentController.java
│   │   │   ├── StripeWebhookController.java
│   │   │   └── BillingPortalController.java
│   │   ├── service/
│   │   │   ├── PaymentService.java
│   │   │   └── StripeAuditLogService.java
│   │   ├── model/
│   │   ├── port/
│   │   │   └── PaymentPort.java               # NEW: product-agnostic payment interface
│   │   └── repository/
│   │
│   ├── ai/                                    # Consolidated AI services
│   │   ├── service/
│   │   │   ├── AIToolService.java
│   │   │   ├── LLMClient.java
│   │   │   ├── LLMRouter.java
│   │   │   └── AIOrchestrationService.java
│   │   ├── port/
│   │   │   └── AIServicePort.java             # NEW: unified AI interface
│   │   └── model/
│   │
│   ├── mail/                                  # Email (already correctly placed)
│   │   ├── controller/
│   │   ├── service/
│   │   └── model/
│   │
│   ├── subscription/                          # Plan management & entitlements
│   │   ├── application/
│   │   │   ├── port/
│   │   │   │   ├── PlanCatalogPort.java
│   │   │   │   └── UserSubscriptionPort.java
│   │   │   └── entitlement/
│   │   ├── domain/
│   │   │   └── model/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── infrastructure/
│   │   └── repository/
│   │
│   ├── infrastructure/                        # Cross-cutting infrastructure
│   │   ├── tenant/                            # Multi-tenancy
│   │   ├── scheduler/                         # Shared cron jobs
│   │   ├── monitoring/                        # Metrics, interceptors
│   │   ├── async/                             # Async processing
│   │   ├── events/                            # EventPublisher
│   │   ├── filter/                            # Request filters
│   │   └── utils/                             # ConfigUtility, EncryptionUtility, etc.
│   │
│   └── common/                                # Truly shared DTOs & utilities
│       ├── dto/
│       │   ├── ApiResponse.java
│       │   ├── ApiErrorResponse.java
│       │   ├── ErrorResponse.java
│       │   └── SocialLinksDTO.java
│       └── model/
│           └── SystemNotificationsModel.java
│
└── products/                                  # Product-specific bounded contexts
    │
    ├── portal/                                # TalentBoozt Portal
    │   ├── job_portal/
    │   │   ├── application/                   # Use cases, orchestration
    │   │   │   └── service/
    │   │   ├── domain/                        # Entities, value objects, events
    │   │   │   ├── model/
    │   │   │   ├── dto/
    │   │   │   └── event/
    │   │   ├── infrastructure/                # Repositories, adapters
    │   │   │   ├── repository/
    │   │   │   └── adapter/
    │   │   └── interfaces/                    # Controllers, WebSocket handlers
    │   │       └── controller/
    │   ├── courses/                            # Community + Platform courses merged
    │   │   ├── application/
    │   │   ├── domain/
    │   │   ├── infrastructure/
    │   │   └── interfaces/
    │   ├── community/                         # Posts, comments, moderation
    │   │   ├── application/
    │   │   ├── domain/
    │   │   ├── infrastructure/
    │   │   └── interfaces/
    │   ├── messaging/                         # WebSocket chat
    │   │   ├── application/
    │   │   ├── domain/
    │   │   ├── infrastructure/
    │   │   └── interfaces/
    │   ├── content/                           # Articles + Announcements
    │   │   ├── application/
    │   │   ├── domain/
    │   │   └── interfaces/
    │   ├── reputation/                        # Reputation scoring
    │   │   ├── application/
    │   │   ├── domain/
    │   │   └── infrastructure/
    │   ├── resume/                            # Resume builder
    │   │   ├── application/
    │   │   ├── domain/
    │   │   └── interfaces/
    │   ├── ambassador/                        # Ambassador program
    │   │   ├── application/
    │   │   ├── domain/
    │   │   └── interfaces/
    │   ├── referral/                          # Referral system
    │   │   ├── application/
    │   │   ├── domain/
    │   │   └── interfaces/
    │   └── user_profile/                      # Profile sub-domains (skills, edu, etc.)
    │       ├── application/
    │       ├── domain/
    │       ├── infrastructure/
    │       └── interfaces/
    │
    ├── edu/                                   # EduPlatform LMS (self-contained)
    │   ├── application/
    │   ├── domain/
    │   ├── infrastructure/
    │   │   ├── repository/
    │   │   ├── adapter/
    │   │   │   ├── EduPlanCatalogAdapter.java  # Implements PlanCatalogPort
    │   │   │   └── EduUserSubscriptionAdapter.java
    │   │   └── config/
    │   │       └── EduMongoConfig.java
    │   ├── interfaces/
    │   │   └── controller/                    # All 42 Edu controllers
    │   ├── seo/                               # 106 SEO files (large, self-contained)
    │   ├── ai/                                # Edu-specific AI (learning engine)
    │   ├── career/                            # Career coaching
    │   ├── community/                         # Edu community
    │   └── marketplace/                       # Course marketplace
    │
    ├── finance/                               # FinancePlanr
    │   ├── application/
    │   ├── domain/
    │   ├── infrastructure/
    │   │   ├── repository/
    │   │   └── adapter/
    │   │       └── FinWorkspaceSyncAdapter.java  # Implements WorkspaceSyncPort
    │   ├── interfaces/
    │   ├── analytics/
    │   ├── collaboration/
    │   ├── scenario/
    │   └── security/
    │
    ├── lifeplanner/                           # LifePlanr (already well-structured)
    │   ├── application/
    │   ├── domain/
    │   ├── infrastructure/
    │   ├── interfaces/
    │   ├── ai/
    │   ├── credits/
    │   ├── journal/
    │   ├── planner/
    │   └── goal/
    │
    ├── leads/                                 # LeadEngine CRM (already well-structured)
    │   ├── application/
    │   ├── domain/
    │   ├── infrastructure/
    │   ├── interfaces/
    │   ├── automation/
    │   ├── campaign/
    │   ├── crm/
    │   └── intelligence/
    │
    ├── marketplace/                           # Software Marketplace
    │   ├── domain/
    │   ├── infrastructure/
    │   └── interfaces/
    │
    └── admin/                                 # Platform Administration
        ├── interfaces/
        │   └── controller/
        │       ├── UserManagementController.java
        │       ├── MetricsController.java
        │       └── WhitelistDomainsController.java
        └── service/
```

---

# Phase 8: Migration Plan Summary

> [!IMPORTANT]
> The full migration involves ~1,475 files. Below are the **key migration rules by domain**, not individual file mappings which would be too large to list here.

## 8.1 Migration Rules by Source Domain

### `domains/auth/` → Split into `shared/identity/` + `shared/auth/`
| Current | Target | Reason |
|---|---|---|
| auth/model/CredentialsModel | shared/identity/model/ | Core identity entity shared by all products |
| auth/model/RoleModel | shared/identity/model/ | RBAC model shared by all products |
| auth/service/CredentialsService | shared/identity/service/ | Used by all products for user lookup |
| auth/service/AuthService | shared/auth/service/ | Authentication logic |
| auth/controller/AuthController | shared/auth/controller/ | Auth endpoints |
| auth/controller/*OAuthController | shared/auth/controller/ | OAuth2 flows |
| auth/service/CustomUserDetailsService | shared/auth/service/ | Spring Security integration |

### `domains/user/` → Split into `shared/identity/` + `products/portal/user_profile/`
| Current | Target | Reason |
|---|---|---|
| user/model/EmployeeModel | shared/identity/model/ | Core identity entity |
| user/repository/EmployeeRepository | shared/identity/repository/ | Core identity data access |
| user/service/EmployeeService | shared/identity/service/ | Core identity operations |
| user/model/Emp*Model | products/portal/user_profile/domain/ | Portal-specific profile data |
| user/controller/Emp*Controller | products/portal/user_profile/interfaces/ | Portal-specific API |
| user/service/Emp*Service | products/portal/user_profile/application/ | Portal-specific logic |

### `domains/edu/` → `products/edu/` (keep mostly intact)
| Current | Target | Reason |
|---|---|---|
| edu/* | products/edu/* | Already self-contained; largest product |
| edu/service/EduJwtService | REMOVE — use shared/security/JwtService | Duplicate |
| edu/service/LLMClient, LLMRouter | shared/ai/service/ | Shared AI infrastructure |

### `domains/finance_planning/` → `products/finance/`
| Current | Target | Reason |
|---|---|---|
| finance_planning/* | products/finance/* | Rename to cleaner path |

### `domains/lifeplanner/` → `products/lifeplanner/`
| Current | Target | Reason |
|---|---|---|
| lifeplanner/* | products/lifeplanner/* | Already well-structured |

### `domains/leads/` → `products/leads/`
| Current | Target | Reason |
|---|---|---|
| leads/* | products/leads/* | Already well-structured |

### `domains/community/` → `products/portal/community/`
| Current | Target | Reason |
|---|---|---|
| community/* | products/portal/community/ | Portal product domain |

### `domains/messaging/` → `products/portal/messaging/`
| Current | Target | Reason |
|---|---|---|
| messaging/* | products/portal/messaging/ | Portal product domain |

### `domains/payment/` + `domains/billing/` → `shared/payment/`
| Current | Target | Reason |
|---|---|---|
| payment/* | shared/payment/ | Used by multiple products |
| billing/* | shared/payment/ | Merge with payment |

### `domains/audit_logs/` + `domains/sys_tracking/` + `domains/audit/` → `shared/audit/`
| Current | Target | Reason |
|---|---|---|
| audit_logs/* | shared/audit/ | Cross-product audit |
| sys_tracking/* | shared/audit/ | Cross-product tracking |
| audit/* | shared/audit/ | Merge all audit |

### `domains/common/` → Split into multiple targets
| Current | Target | Reason |
|---|---|---|
| common/dto/Api*Response | shared/common/dto/ | Universal response DTOs |
| common/controller/BatchController | products/portal/interfaces/ | Portal-specific batch |
| common/controller/FileUploadController | shared/infrastructure/storage/ | Shared file upload |
| common/controller/LoginController | shared/identity/controller/ | User login tracking |
| common/controller/SystemNotificationsController | shared/notification/controller/ | System-wide notifications |
| common/model/FeatureModel, IssueModel | products/admin/domain/ | Admin features |

### `domains/subscription/` → `shared/subscription/`
| Current | Target | Reason |
|---|---|---|
| subscription/* | shared/subscription/* | Cross-product entitlements |
| subscription/infrastructure/external/Edu* | products/edu/infrastructure/adapter/ | Edu-specific adapter |

### `domains/_private/` → `products/admin/`
| Current | Target | Reason |
|---|---|---|
| _private/* | products/admin/ | Admin console |

### `domains/_public/` → `products/portal/interfaces/public/`
| Current | Target | Reason |
|---|---|---|
| _public/* | products/portal/interfaces/public/ | Public portal endpoints |

### Small domains → product-appropriate locations
| Domain | Target | Reason |
|---|---|---|
| ai_tool, ai_assistant, ai_orchestration | shared/ai/ | Shared AI services |
| notifications, automation, communication | shared/notification/ | Notification infrastructure |
| drive | shared/infrastructure/storage/ | File storage |
| workspace, organization | shared/identity/ | Identity/workspace management |
| feature_flags | shared/subscription/ | Feature gating |
| support | products/portal/support/ | Portal support tickets |
| software_marketplace | products/marketplace/ | Standalone product |
| pipeline, activity, candidate, applications | products/portal/job_portal/ | Recruitment features |
| recommendations | products/portal/job_portal/ | Job matching |
| recruiter, interviews | products/portal/job_portal/ | Recruitment features |
| jobs | products/portal/job_portal/ | Job entities |
| insights, analytics | shared/audit/ or product-specific | Analytics |

---

# Phase 9: Risk Analysis

## 9.1 High-Risk Areas

### Risk 1: Circular Dependency Breaking — HIGH RISK
```
Impact: Compilation failure
Affected: auth ↔ user, edu ↔ subscription, edu ↔ referral, com_courses ↔ plat_courses
Mitigation:
- Introduce port interfaces (UserLookupPort, WorkspaceSyncPort, PlanCatalogPort)
- Products implement adapters; shared modules depend only on ports
- Move shared entities (CredentialsModel, EmployeeModel) to shared/identity
- Break edu ↔ subscription by moving subscription to shared/ and using PlanCatalogPort
- Break edu ↔ referral by having referral depend on a ReferralPort in shared
```

### Risk 2: Spring Component Scanning — HIGH RISK
```
Impact: Beans not found at startup
Affected: All moved classes
Mitigation:
- @SpringBootApplication scans com.talentboozt.s_backend.* — this covers both shared/ and products/
- Verify @ComponentScan base packages include all new paths
- Verify @Configuration classes are discovered
- Run `mvn compile` after each major batch of moves
```

### Risk 3: MongoDB Collection Mapping — MEDIUM RISK
```
Impact: Data access failure, wrong collections
Affected: All @Document annotated entities
Mitigation:
- Ensure @Document(collection = "...") annotations are preserved
- Package moves don't affect MongoDB collection names (they're string-based)
- Verify MongoConfig base packages for repository scanning
- EduMongoConfig uses separate database — verify its basePackages
```

### Risk 4: Test Breakage — MEDIUM RISK
```
Impact: Test compilation failures
Affected: All 48 test files
Mitigation:
- Update test imports after each domain move
- Test packages should mirror source packages
- Run tests after each phase of migration
- Only 48 tests — manageable to fix manually
```

### Risk 5: Security Filter Chain — MEDIUM RISK
```
Impact: Auth failures, broken endpoints
Affected: SecurityConfig, JwtAuthenticationFilter, ApiKeyAuthenticationFilter
Mitigation:
- SecurityConfig imports from shared/security (already mostly there)
- Verify filter bean discovery after package moves
- Test all security paths after migration
```

### Risk 6: Scheduled Job Wiring — LOW RISK
```
Impact: Cron jobs not executing
Affected: 16 scheduled jobs across domains
Mitigation:
- @EnableScheduling on main class scans entire classpath
- Verify @Scheduled annotated methods are still in Spring-managed beans
- Test scheduler execution after migration
```

## 9.2 Migration Execution Order (Recommended)

To minimize risk, execute migrations in this order:

| Phase | Scope | Risk | Validation |
|---|---|---|---|
| 1 | Create port interfaces in shared/ | None | Compile |
| 2 | Move shared/infrastructure (utils, async, events, filter, monitoring) | Low | Compile |
| 3 | Move shared/identity (CredentialsModel, EmployeeModel + repos) | High | Compile + Auth test |
| 4 | Move shared/auth (controllers, services) | High | Compile + Login test |
| 5 | Move shared/audit (audit_logs + sys_tracking merge) | Medium | Compile |
| 6 | Move shared/payment (payment + billing merge) | Medium | Compile + Stripe test |
| 7 | Move shared/ai (consolidate all AI) | Low | Compile |
| 8 | Move shared/subscription | Medium | Compile + circular break |
| 9 | Move shared/notification | Low | Compile |
| 10 | Move products/edu | Medium | Compile + Edu tests |
| 11 | Move products/finance | Low | Compile + Finance tests |
| 12 | Move products/lifeplanner | Low | Compile |
| 13 | Move products/leads | Low | Compile |
| 14 | Move products/portal (largest, last) | Medium | Full regression |
| 15 | Move products/admin + marketplace | Low | Compile |
| 16 | Clean up common/ (distribute remaining) | Low | Compile |
| 17 | Final validation | — | Full compile + all tests |

## 9.3 Pre-Migration Checklist

- [ ] Create a git branch for the migration
- [ ] Verify current state compiles: `mvn compile`
- [ ] Verify current tests pass: `mvn test`
- [ ] Back up the repository
- [ ] Create port interfaces before any file moves
- [ ] Update `MongoConfig` base package arrays
- [ ] Update `EduMongoConfig` base package arrays
- [ ] Update `SecurityConfig` imports
- [ ] Run incremental compilation after each phase
