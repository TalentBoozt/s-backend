# Phase 2: Product/Application Discovery

## 2.1 Discovered Products

From controller paths, security rules, domain boundaries, and business workflows, **7 distinct products** exist in this monolith:

---

### Product 1: TalentBoozt Portal (Job Portal + Community Platform)

The **original core product** — a recruitment and community platform for job seekers and employers.

```
TalentBoozt Portal
├── Domains
│   ├── com_job_portal    (13 files) — Company job postings, company profiles
│   ├── plat_job_portal   (16 files) — Job applications, interview questions, pre-orders
│   ├── com_courses       (29 files) — Community-driven courses
│   ├── plat_courses      (55 files) — Platform courses, gamification, certificates, badges
│   ├── community         (37 files) — Posts, comments, voting, moderation
│   ├── messaging         (19 files) — WebSocket chat, direct/group messaging
│   ├── article           (17 files) — Blog articles
│   ├── announcement      (12 files) — Community announcements
│   ├── reputation        (14 files) — User reputation scores, badges
│   ├── resume            (13 files) — Resume builder, ATS scoring
│   ├── jobs              (7 files)  — Job post entities
│   ├── candidate         (1 file)   — Candidate aggregation view
│   ├── recommendations   (1 file)   — Job recommendations
│   ├── recruiter         (3 files)  — Recruiter profiles
│   └── interviews        (3 files)  — Interview management
├── APIs: /api/v2/communities, /api/v2/posts, /api/v2/messaging,
│         /api/v2/emp_courses, /api/v2/portal_*, /api/v2/resumes
├── Infrastructure: MongoDB, Redis, WebSocket, Caffeine
└── Dependencies: auth, user, payment, ai_tool, common
```

### Product 2: EduPlatform (Learning Management System)

A **full-featured LMS** — the largest product at 471 files. Has its own auth, admin, finance, marketplace, SEO engine, AI, and analytics.

```
EduPlatform LMS
├── Domains (all under domains/edu/)
│   ├── controller/       (42 files) — Admin, courses, enrollment, finance, quizzes, etc.
│   ├── service/          (68 files) — Business logic, AI, analytics, payments, moderation
│   ├── model/            (51 files) — MongoDB entities
│   ├── repository/       (48 files) — Data access
│   ├── dto/              (56 files) — Transfer objects
│   ├── enums/            (29 files) — Status codes, types, categories
│   ├── seo/              (106 files) — SEO engine (schema, sitemap, analytics, IndexNow)
│   ├── career/           (6 files)  — Career coaching, portfolio generator
│   ├── community/        (9 files)  — Edu community, leaderboards, peer learning
│   ├── marketplace/      (9 files)  — Course marketplace
│   ├── ai/               (4 files)  — AI-powered content generation
│   ├── ai_learning/      (3 files)  — Personalized learning engine
│   ├── content/          (3 files)  — AI content generation
│   ├── learning/         (5 files)  — Learning paths
│   ├── domain/           (5 files)  — DDD domain models
│   ├── config/           (1 file)   — EduMongoConfig (separate DB)
│   └── exception/        (6 files)  — Domain exceptions
├── APIs: /api/edu/*, /api/monetization/*, /api/admin/*
├── Infrastructure: Separate MongoDB database, AI/LLM clients, Stripe, SEO crawlers
├── Scheduled Jobs: AI credit cron, wallet scheduler, SEO scheduler, holding cron
└── Dependencies: auth, common, referral, subscription, user
```

### Product 3: FinancePlanr (Personal Finance Planning)

A **standalone finance planning app** with its own auth, collaboration, analytics, and scenario modeling.

```
FinancePlanr
├── Domains (all under domains/finance_planning/)
│   ├── controllers/      (16 files) — Financial data, scenarios, collaboration
│   ├── services/         (12 files) — Computation, versioning, AI training
│   ├── models/           (13 files) — Financial workspace, plans, scenarios
│   ├── repository/       (12 files) — MongoDB repositories
│   ├── dtos/             (6 files)  — Request/response DTOs
│   ├── analytics/        (10 files) — Financial analytics, event listeners
│   ├── collaboration/    (7 files)  — Real-time collaboration, conflict resolution
│   ├── security/         (8 files)  — Permission model, workspace guards
│   ├── scenario/         (10 files) — Financial scenario simulation
│   ├── events/           (1 file)   — FinancialsChangedEvent
│   └── exception/        (2 files)  — Domain exceptions
├── APIs: /api/finance/*, /api/finance/auth/*
├── Infrastructure: MongoDB (own workspace), AI training pipeline
└── Dependencies: auth, user, workspace
```

### Product 4: LifePlanr (Life Planning App)

A **personal life planning app** with journaling, mood tracking, AI coaching, credits system, and gamification.

```
LifePlanr
├── Domains (all under domains/lifeplanner/)
│   ├── planner/          (13 files) — Schedule, daily plans, habit tracking
│   ├── ai/              (13 files) — AI coaching, prompt engine
│   ├── journal/          (9 files)  — Journal entries, mood tracking
│   ├── credits/          (8 files)  — Credit system, Stripe payments
│   ├── user/             (8 files)  — User preferences, profiles
│   ├── goal/             (7 files)  — Goal tracking
│   ├── notification/     (4 files)  — Push notifications
│   ├── analytics/        (3 files)  — Usage analytics
│   ├── scheduler/        (3 files)  — Mood aggregation, plan staleness
│   ├── admin/            (2 files)  — Admin dashboard
│   ├── shared/           (4 files)  — Shared utilities
│   └── seo/              (1 file)   — SEO support
├── APIs: /api/lifeplanner/*
├── Infrastructure: MongoDB, Stripe, AI/LLM
└── Dependencies: user
```

### Product 5: LeadEngine (CRM / Lead Management)

A **B2B CRM system** with lead scoring, automation, campaigns, and intelligence.

```
LeadEngine CRM
├── Domains (all under domains/leads/)
│   ├── controller/       (8 files) — Lead management, campaigns
│   ├── service/          (7 files) — Lead processing
│   ├── model/            (7 files) — Lead, pipeline, timeline entities
│   ├── repository/       (7 files) — Data access
│   ├── automation/       (6 files) — Workflow automation
│   ├── crm/              (7 files) — CRM features, timeline events
│   ├── campaign/         (4 files) — Campaign management
│   ├── intelligence/     (3 files) — Lead scoring, intent analysis
│   ├── analytics/        (2 files) — Lead analytics
│   ├── events/           (1 file)  — LNewSignalEvent
│   └── dto/              (1 file)  — DTOs
├── APIs: /api/leads/*
├── Infrastructure: MongoDB
└── Dependencies: audit_logs
```

### Product 6: Ambassador Program

A **referral & ambassador management system** with points, rewards, commissions, and leaderboards.

```
Ambassador Program
├── Domains (all under domains/ambassador/)
│   ├── controller/       — Profile, rewards, sessions, referrals
│   ├── service/          — Points, leaderboards, rewards
│   ├── model/            — Ambassador profiles, rewards, sessions
│   └── repository/       — Data access
├── APIs: /api/v2/ambassador/*
├── Infrastructure: MongoDB
└── Dependencies: auth, common, payment, plat_courses
```

### Product 7: Software Marketplace

A **nascent app marketplace** (minimal, 4 files).

```
Software Marketplace
├── controller/SoftwareAppController
├── model/SoftwareAppModel
├── repository/SoftwareAppRepository
└── service/SoftwareAppService
├── APIs: /api/software/*
└── Dependencies: auth
```

---

# Phase 3: Domain Discovery

## 3.1 Domain Classification

### Core Domains (Primary business value)

| Domain | Type | File Count | Description |
|---|---|---|---|
| **edu** | Core | 471 | LMS platform — courses, enrollments, marketplace, AI, finance |
| **finance_planning** | Core | 98 | Financial planning, scenarios, collaboration |
| **lifeplanner** | Core | 75 | Life planning, journaling, AI coaching |
| **leads** | Core | 53 | CRM, lead management, automation |
| **com_job_portal + plat_job_portal** | Core | 29 | Job portal — postings, applications |
| **community** | Core | 37 | Social platform — posts, moderation |
| **ambassador** | Core | 29 | Ambassador/referral business program |
| **resume** | Core | 13 | Resume builder, ATS scoring |

### Supporting Domains (Assist core business)

| Domain | Type | File Count | Description |
|---|---|---|---|
| **plat_courses + com_courses** | Supporting | 84 | Course delivery infrastructure |
| **payment** | Supporting | 37 | Stripe payment orchestration |
| **subscription** | Supporting | 29 | Plan management, entitlements |
| **messaging** | Supporting | 19 | WebSocket chat system |
| **article** | Supporting | 17 | Content publishing |
| **reputation** | Supporting | 14 | Reputation scoring engine |
| **referral** | Supporting | 11 | Referral tracking |
| **announcement** | Supporting | 12 | System announcements |
| **pipeline** | Supporting | 5 | Hiring pipeline (ATS) |
| **drive** | Supporting | 8 | Google Drive file management |

### Generic Domains (Shared platform infrastructure)

| Domain | Type | File Count | Description |
|---|---|---|---|
| **auth** | Generic | 47 | Authentication, OAuth2, credentials, roles |
| **user** | Generic | 54 | User profiles, skills, education, contacts |
| **audit_logs** | Generic | 39 | Audit trail, scheduler logs |
| **sys_tracking** | Generic | 41 | System analytics, monitoring |
| **common** | Generic | 26 | Batch operations, file upload, system notifications |
| **ai_tool** | Generic | 22 | AI/OpenAI integrations |
| **notifications** | Generic | 4 | Push notifications |
| **workspace** | Generic | 4 | Multi-workspace management |
| **feature_flags** | Generic | 3 | Feature toggles |
| **support** | Generic | 4 | Support ticket system |

## 3.2 Domain Detail Cards

### Domain: Auth
```
Responsibilities:
- User registration (local + OAuth2: Google, GitHub, Facebook, LinkedIn)
- JWT token generation and validation
- Password reset flow
- Role and permission management
- SSO support
- API key authentication

Owned Entities:
- CredentialsModel, RoleModel, PermissionModel, PasswordResetToken

Services:
- CredentialsService, AuthService, CustomUserDetailsService
- RoleService, PermissionService, UserPermissionsService
- FacebookAuthService, GitHubAuthService, LinkedinAuthService
- GoogleOAuthService, SsoAuthService

Events: (none — relies on synchronous calls)

Used By: ALL products (universal dependency)

Depends On: com_job_portal, common, finance_planning, referral, user, workspace
```

### Domain: User
```
Responsibilities:
- Employee profile lifecycle (CRUD)
- Skills, education, experience, certificates, contacts, projects
- Following/followers social graph
- Trainer profiles
- Notification preferences
- Profile completion tracking

Owned Entities:
- EmployeeModel, EmpSkillsModel, EmpEducationModel, EmpExperiencesModel
- EmpCertificatesModel, EmpContactModel, EmpProjectsModel
- EmpFollowersModel, EmpFollowingModel, TrainerProfile, PlatformRole

Services:
- EmployeeService, EmpSkillsService, EmpEducationService, etc.
- ProfileUpdateService, TrainerProfileService

Events:
- UserProfileUpdatedEvent → UserProfileUpdateListener

Used By: edu, finance_planning, lifeplanner, community, messaging, reputation, common, plat_courses

Depends On: auth, common, plat_courses, plat_job_portal
```

### Domain: Edu (Full LMS)
```
Responsibilities:
- Course creation, moderation, lifecycle management
- Student enrollment and progress tracking
- Quiz/assignment engine
- Certificate generation
- AI-powered content generation and personalization
- Course marketplace with monetization
- SEO engine (sitemaps, schema, IndexNow)
- Enterprise workspace management
- Financial operations (wallets, ledger, refunds, payouts)
- Fraud detection and trust scoring
- Affiliate program
- Community features (leaderboards, peer learning)

Owned Entities:
- 51 MongoDB models (ECourse, EEnrollment, ELesson, EQuiz, EWallet, etc.)

Services:
- 68 service classes covering all features

Events:
- EAnalyticsEvents, EWebhookEvent

Used By: subscription (circular), referral (circular)

Depends On: auth, common, referral, subscription, user
```

### Domain: Finance Planning
```
Responsibilities:
- Financial workspace and plan management
- Scenario simulation and what-if analysis
- Real-time collaboration with conflict resolution
- AI-powered financial training and insights
- Permission model with workspace guards
- Incremental computation engine

Owned Entities:
- FinWorkspace, FinPlan, FinScenario, FinCollabSession, etc.

Events:
- FinancialsChangedEvent → AnalyticsEventListener, FinAiTrainingEventListener

Depends On: auth, user, workspace
```

---

# Phase 4: Shared Domain Detection

## 4.1 Cross-Product Shared Domains

### Shared Domain 1: User Identity
```
Consumers:
- TalentBoozt Portal
- EduPlatform
- FinancePlanr
- LifePlanr
- LeadEngine
- Ambassador Program

Shared Components:
- EmployeeModel, CredentialsModel
- EmployeeRepository, CredentialsRepository
- EmployeeService, CredentialsService

Refactoring Recommendation:
- Extract into shared/identity module
- Create UserIdentityPort interface for loose coupling
- Products should depend on port, not concrete service
```

### Shared Domain 2: Authentication & Authorization
```
Consumers:
- ALL products

Shared Components:
- JwtService, JwtAuthenticationFilter, ApiKeyAuthenticationFilter
- SecurityConfig, CustomUserDetailsService
- RbacInterceptor, RequireRole, RequirePlan annotations
- EntitlementPort, EntitlementPlan

Refactoring Recommendation:
- Already partially in shared/security
- Move auth domain controllers + services into shared/auth
- SecurityConfig should remain in config/
- Eliminate auth → com_job_portal dependency (CompanyModel lookup)
```

### Shared Domain 3: Notifications
```
Consumers:
- TalentBoozt Portal (community notifications)
- EduPlatform (enrollment/course notifications)
- LifePlanr (reminder notifications)
- Pipeline (candidate stage changes → realtime)

Shared Components:
- NotificationController, NotificationService
- RealtimeBroadcaster (WebSocket)
- CandidateStageChangedRealtimeListener

Refactoring Recommendation:
- Consolidate domains/notifications + shared/realtime
- Create shared/notification module with pub/sub pattern
- Products publish events, notification module consumes
```

### Shared Domain 4: Audit & Tracking
```
Consumers:
- TalentBoozt Portal (user activity)
- EduPlatform (edu audit service)
- Payment (stripe audit logs)
- Ambassador (reward audit)
- LeadEngine (via audit_logs)

Shared Components:
- audit_logs/ (39 files) + sys_tracking/ (41 files) + audit/ (3 files)
- SchedulerLoggerService, AsyncUpdateLogger
- TrackingController, MonitoringController

Refactoring Recommendation:
- Merge audit_logs, sys_tracking, and audit into shared/audit
- Define AuditPort interface for domain-specific audit events
- Products emit audit events, shared module persists them
```

### Shared Domain 5: Payment & Billing
```
Consumers:
- TalentBoozt Portal (course payments)
- EduPlatform (course purchases, refunds)
- LifePlanr (credit purchases)
- Ambassador (commission payouts)

Shared Components:
- PaymentController, StripeWebhookController
- PaymentService, StripeConfig
- billing/ domain (3 files)

Refactoring Recommendation:
- Consolidate payment/ + billing/ into shared/payment
- Define PaymentPort interface
- Remove payment → com_courses, com_job_portal direct dependencies
```

### Shared Domain 6: AI Services
```
Consumers:
- EduPlatform (content generation, personalization)
- LifePlanr (AI coaching)
- Resume (AI scoring)
- Article (AI-enhanced content)
- Announcement (AI summarization)
- Finance Planning (AI training)

Shared Components:
- ai_tool/ (22 files), shared/ai/ (3 files)
- ai_assistant/ (3 files), ai_orchestration/ (3 files)
- LLMClient, LLMRouter (in edu)

Refactoring Recommendation:
- Consolidate all AI into shared/ai
- Create AIServicePort interface
- Products call AIServicePort, shared module routes to OpenAI/other LLMs
```

### Shared Domain 7: Email & Communication
```
Consumers:
- ALL products (registration, notifications, receipts)

Shared Components:
- shared/mail/ (16 files)
- 21 HTML email templates
- EmailController, EmailQueueController, NewsLatterController
- HTMLEmailService, EmailJob

Refactoring Recommendation:
- Already correctly in shared/mail — good
- Add EmailPort interface for type-safe template rendering
```

### Shared Domain 8: Subscription & Entitlements
```
Consumers:
- EduPlatform (plan-gated features)
- ai_tool (AI credit quotas)
- TalentBoozt Portal (feature gating)

Shared Components:
- subscription/ (29 files)
- SubscriptionEntitlementAdapter, EduPlanCatalogAdapter
- UserPlanChangedEvent

Refactoring Recommendation:
- Break circular dependency: subscription ↔ edu
- Extract subscription into shared/subscription
- Define PlanCatalogPort implemented by each product
- EntitlementPort already exists — good pattern
```
