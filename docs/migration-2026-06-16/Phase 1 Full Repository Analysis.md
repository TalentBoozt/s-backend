# Phase 1: Full Repository Analysis

## 1.1 Repository Overview

| Metric | Value |
|---|---|
| **Framework** | Spring Boot 3.4.3 |
| **Java Version** | 17 |
| **Build Tool** | Maven |
| **Total Java Files** | 1,475 |
| **Domain Directories** | 49 |
| **Shared Modules** | 12 |
| **Config Classes** | 24 |
| **Controllers** | ~160+ |
| **Services** | ~320 |
| **Repositories** | ~208 |
| **Test Files** | ~48 |
| **Database** | MongoDB (primary), Redis (cache) |

## 1.2 Technology Stack

### Core Dependencies
- `spring-boot-starter-web` — REST API
- `spring-boot-starter-data-mongodb` — MongoDB persistence
- `spring-boot-starter-data-redis` — Redis caching
- `spring-boot-starter-security` — Security framework
- `spring-boot-starter-oauth2-client` — OAuth2 (Google, GitHub, Facebook, LinkedIn)
- `spring-boot-starter-websocket` — WebSocket (real-time messaging)
- `spring-boot-starter-webflux` — Reactive support
- `spring-boot-starter-mail` — Email
- `spring-boot-starter-aop` — Aspect-Oriented Programming
- `spring-boot-starter-validation` — Bean validation
- `spring-boot-starter-actuator` — Health/metrics

### External Integrations
- **Stripe** (`stripe-java:28.2.0`) — Payment processing
- **OpenAI** (via `okhttp` + `gson`) — AI tools
- **Google Drive API** — File storage
- **AWS S3** (`software.amazon.awssdk:s3:2.25.15`) — Object storage
- **MaxMind GeoIP2** — Geolocation
- **Jsoup** — HTML parsing
- **Apache PDFBox** — PDF generation
- **Apache POI** — Excel processing

### Resilience & Observability
- **Resilience4j** — Circuit breakers, retries, rate limiting
- **Micrometer** — Metrics
- **Caffeine** — JVM-level caching
- **Bucket4j** — Rate limiting
- **Jasypt** — Encryption
- **ArchUnit** — Architecture testing

### Auth Stack
- **JJWT** (`0.11.5`) — JWT token generation/validation
- **Spring Security OAuth2** — Social logins
- **Custom API Key filter** — Service-to-service auth

## 1.3 Package Structure (Current)

```
src/main/java/com/talentboozt/s_backend/
├── SBackendApplication.java          # @SpringBootApplication @EnableScheduling @EnableAsync
├── config/                           # 24 config classes
│   ├── SecurityConfig.java           # 22KB — Main security filter chains
│   ├── CacheConfig.java              # Caffeine + Redis caching
│   ├── MongoConfig.java              # MongoDB configuration
│   ├── RedisConfig.java              # Redis configuration
│   ├── WebSocketConfig.java          # WebSocket STOMP config
│   ├── CorsConfig.java               # CORS policies
│   ├── AsyncConfig.java              # Async thread pools
│   ├── RabbitMQConfig.java           # RabbitMQ (commented out)
│   ├── CircuitBreakerConfiguration   # Resilience4j
│   └── ... (15 more)
│
├── domains/                          # 49 domain packages
│   ├── edu/                          # 471 files — LARGEST domain (LMS Platform)
│   ├── finance_planning/             # 98 files — Financial Planning App
│   ├── lifeplanner/                  # 75 files — Life Planner App
│   ├── plat_courses/                 # 55 files — Platform Courses
│   ├── user/                         # 54 files — User Profiles
│   ├── leads/                        # 53 files — CRM/Lead Management
│   ├── auth/                         # 47 files — Authentication
│   ├── sys_tracking/                 # 41 files — System Analytics
│   ├── audit_logs/                   # 39 files — Audit Logging
│   ├── community/                    # 37 files — Social Community
│   ├── payment/                      # 37 files — Stripe Payment
│   ├── ambassador/                   # 29 files — Ambassador Program
│   ├── com_courses/                  # 29 files — Community Courses
│   ├── subscription/                 # 29 files — Plan Management
│   ├── common/                       # 26 files — Shared catchall
│   ├── ai_tool/                      # 22 files — AI Integrations
│   ├── _private/                     # 22 files — Admin APIs
│   ├── messaging/                    # 19 files — Chat/Messaging
│   ├── article/                      # 17 files — Blog Articles
│   ├── plat_job_portal/              # 16 files — Platform Job Portal
│   ├── reputation/                   # 14 files — User Reputation
│   ├── com_job_portal/               # 13 files — Company Job Portal
│   ├── resume/                       # 13 files — Resume Builder
│   ├── announcement/                 # 12 files — Announcements
│   ├── referral/                     # 11 files — Referral System
│   ├── _public/                      # 11 files — Public APIs
│   ├── drive/                        # 8 files  — Google Drive
│   ├── jobs/                         # 7 files  — Job Posts
│   ├── pipeline/                     # 5 files  — Hiring Pipeline
│   ├── workspace/                    # 4 files  — Workspace Mgmt
│   ├── notifications/                # 4 files  — Push Notifications
│   ├── support/                      # 4 files  — Support Tickets
│   ├── software_marketplace/         # 4 files  — App Marketplace
│   ├── organization/                 # 4 files  — Organization
│   ├── activity/                     # 4 files  — Activity Feed
│   ├── feature_flags/                # 3 files  — Feature Toggles
│   ├── automation/                   # 3 files  — Workflow Automation
│   ├── communication/                # 3 files  — Communication
│   ├── billing/                      # 3 files  — Billing
│   ├── ai_assistant/                 # 3 files  — AI Assistant
│   ├── ai_orchestration/             # 3 files  — AI Orchestration
│   ├── interviews/                   # 3 files  — Interview Mgmt
│   ├── recruiter/                    # 3 files  — Recruiter Profiles
│   ├── insights/                     # 3 files  — Business Insights
│   ├── applications/                 # 3 files  — Job Applications
│   ├── edu (sub-domains)/            # SEO: 106, model: 51, repo: 48, service: 68, controller: 42, dto: 56, enums: 29
│   ├── analytics/                    # 1 file   — Formula Engine
│   ├── candidate/                    # 1 file   — Candidate View
│   └── recommendations/             # 1 file   — Job Recommendations
│
└── shared/                           # 80 files across 12 modules
    ├── security/                     # 36 files — JWT, RBAC, Filters, Rate Limiting
    ├── mail/                         # 16 files — Email Templates & Queue
    ├── scheduler/                    # 6 files  — Cron Jobs
    ├── utils/                        # 6 files  — Encryption, Config, Validators
    ├── tenant/                       # 4 files  — Multi-tenancy
    ├── ai/                           # 3 files  — AI abstractions
    ├── monitoring/                   # 2 files  — Metrics, Interceptor
    ├── realtime/                     # 2 files  — WebSocket broadcasting
    ├── async/                        # 2 files  — Async processing
    ├── dto/                          # 1 file   — Shared DTOs
    ├── events/                       # 1 file   — EventPublisher (RabbitMQ stub)
    └── filter/                       # 1 file   — Request filters
```

## 1.4 Cross-Domain Dependency Graph

> [!WARNING]
> **83 cross-domain import relationships detected**, including multiple circular dependency chains.

### Full Dependency Map (Source → Target)

```
_private      → audit_logs, auth, common, user
_public       → com_courses, common
activity      → pipeline
ai_assistant  → ai_orchestration
ai_tool       → subscription
ambassador    → auth, common, payment, plat_courses
announcement  → ai_tool
article       → ai_tool
audit_logs    → ambassador, com_courses, plat_courses
auth          → com_job_portal, common, finance_planning, referral, user, workspace
automation    → notifications
candidate     → applications, jobs, resume
com_courses   → payment, plat_courses, user
com_job_portal→ common
common        → ambassador, auth, com_courses, com_job_portal, plat_courses, sys_tracking, user
community     → user
drive         → auth
edu           → auth, common, referral, subscription, user
finance_planning → auth, user, workspace
leads         → audit_logs
lifeplanner   → user
messaging     → community, user
notifications → pipeline
payment       → audit_logs, auth, com_courses, com_job_portal, plat_courses
plat_courses  → ambassador, audit_logs, auth, com_courses, user
plat_job_portal → auth, common
recommendations → jobs, resume
referral      → edu
reputation    → article, community, user
resume        → ai_tool
software_marketplace → auth
subscription  → edu
sys_tracking  → _private, audit_logs, auth, common
user          → auth, common, plat_courses, plat_job_portal
workspace     → auth
```

### Circular Dependencies Detected

| Cycle | Path |
|---|---|
| **auth ↔ common** | `auth → common`, `common → auth` |
| **auth ↔ user** | `auth → user`, `user → auth` |
| **edu ↔ referral** | `edu → referral`, `referral → edu` |
| **edu ↔ subscription** | `edu → subscription`, `subscription → edu` |
| **com_courses ↔ plat_courses** | `com_courses → plat_courses`, `plat_courses → com_courses` |
| **common multi-cycle** | `common → ambassador → auth → common` |
| **user ↔ plat_courses** | `user → plat_courses`, `plat_courses → user` |

## 1.5 Scheduled Jobs & Cron Services

| Job | Location |
|---|---|
| CouponExpiryScheduler | shared/scheduler |
| CourseEnrollsCleanupScheduler | shared/scheduler |
| CourseReminderScheduler | shared/scheduler |
| GamificationProgressUpdater | shared/scheduler |
| LeaderboardScheduler | shared/scheduler |
| StripeRetryScheduler | shared/scheduler |
| EduAICreditCronService | domains/edu |
| EduHoldingCronService | domains/edu |
| EduWalletScheduler | domains/edu |
| SeoAnalyticsScheduler | domains/edu |
| AutoRefreshScheduler | domains/edu |
| IndexNowScheduler | domains/edu |
| MoodAggregationScheduler | domains/lifeplanner |
| PlanStalenessScheduler | domains/lifeplanner |
| ScheduleRepairScheduler | domains/lifeplanner |
| AIQuotaScheduler | domains/ai_tool |

## 1.6 Event Infrastructure

| Event | Domain | Type |
|---|---|---|
| AnnouncementPublishedEvent | announcement | Spring Event |
| ArticlePublishedEvent | article | Spring Event |
| ArticleLikedEvent | article | Spring Event |
| ArticleBookmarkedEvent | article | Spring Event |
| PostUpvotedEvent | community | Spring Event |
| CommentUpvotedEvent | community | Spring Event |
| ContentCreatedEvent | community | Spring Event |
| CandidateStageChangedEvent | pipeline | Spring Event |
| FinancialsChangedEvent | finance_planning | Spring Event |
| LNewSignalEvent | leads | Spring Event |
| UserProfileUpdatedEvent | user | Spring Event |
| UserPlanChangedEvent | subscription | Spring Event |
| ResumeParsedEvent | resume | Spring Event |
| ResumeUploadedEvent | resume | Spring Event |
| EventPublisher (RabbitMQ stub) | shared/events | Commented out |

## 1.7 Infrastructure (No Docker/CI)

- **No Dockerfile** found in repository
- **No docker-compose** found
- **No CI/CD pipeline** files (.github/workflows, Jenkinsfile, etc.)
- Configuration managed via `.env` file (spring-dotenv)
- Email templates in `src/main/resources/templates/` (21 HTML templates)
- ML training data in `src/main/resources/training/`
- SQL migration: `V2__Add_SEO_Fields.sql`

## 1.8 Test Coverage

Only **48 test files** for a 1,475-file codebase (~3.3% test ratio):
- Ambassador: 10 tests (controller + service)
- Audit Logs: 11 tests
- Com Courses: 4 tests
- Com Job Portal: 5 tests
- Edu: 1 test (payment integration)
- Finance Planning: 8 tests (integration + services)
- Plat Job Portal: 3 tests
- Subscription: 3 tests (architecture + adapter + mapping)
- Application-level: 1 test
