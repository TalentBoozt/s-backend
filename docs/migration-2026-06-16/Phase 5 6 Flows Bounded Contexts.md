# Phase 5: Flow Discovery

## 5.1 Major Business Flows

### Flow 1: User Registration
```
AuthController.register (/api/auth/register)
  → AuthService.register()
    → CredentialsService.addCredentials()
      → RoleService.getRoleByName() / addRole()
      → EmployeeRepository.save()           # Creates user profile
      → CompanyRepository.save()             # If employer level
      → CredentialsRepository.save()         # Saves credentials
    → JwtService.generateToken()             # Issues JWT
  → Response: { token, refreshToken, user }

Cross-domain calls: auth → user (EmployeeRepository), auth → com_job_portal (CompanyRepository),
                    auth → finance_planning (FinWorkspaceRepository via syncWorkspaces)
```

### Flow 2: OAuth2 Social Login (Google)
```
SecurityConfig.oidcUserService()
  → OidcUserService.loadUser()
    → CredentialsService.getCredentialsByEmail()
      IF exists → loginUser() → return OidcUser
      IF new    → registerGoogleUser()
        → CredentialsService.addCredentials()  # Same as Flow 1
  → OAuth2LoginController.oauth2Success (/oauth2/success)
    → Redirect to frontend with token

Cross-domain calls: config → auth (CredentialsService), auth → user/com_job_portal
```

### Flow 3: Job Posting & Application
```
CmpPostedJobsController.addJob (/api/v2/portal_posted-jobs/add)
  → CmpPostedJobsService.addPostedJob()
    → CmpPostedJobsRepository.save()

JobApplyController.applyJob (/api/v2/portal_job-apply/add)
  → JobApplyService.applyForJob()
    → JobApplyRepository.save()
    → HTMLEmailService.sendEmail()   # Notification to employer

Cross-domain calls: plat_job_portal → auth (credential lookup),
                    plat_job_portal → common (batch data)
```

### Flow 4: Course Enrollment (Platform Courses)
```
EmpCoursesController.enrollCourse (/api/v2/emp_courses/add)
  → EmpCoursesService.addEmpCourse()
    → EmpCoursesRepository.save()
    → ProgressUpdater.initializeProgress()
    → CourseCertificateService.checkEligibility()

Cross-domain calls: plat_courses → auth (user verification),
                    plat_courses → user (employee lookup),
                    plat_courses → com_courses (course details)
```

### Flow 5: Edu Course Purchase
```
EduCoursePurchaseController (/api/edu/purchase)
  → EduCoursePurchaseService.initiatePurchase()
    → EduCourseService.getCourse()             # Validate course
    → EduCouponService.validateCoupon()         # Apply discount
    → Stripe API (create checkout session)      # External payment
    → EduWalletService.holdFunds()              # Hold in wallet
    → EduEnrollmentService.createPending()      # Pending enrollment

Webhook callback:
  EduWebhookController → WebhookEventProcessor
    → EduWalletService.releaseFunds()
    → EduEnrollmentService.activate()
    → EduLedgerService.recordTransaction()
    → EduCommissionCalculator.calculate()       # Creator commission
    → EduNotificationService.notifyEnrollment()

Cross-domain calls: edu → auth (user lookup), edu → subscription (plan check)
```

### Flow 6: Stripe Payment Webhook Processing
```
StripeWebhookController.handleWebhook (/api/v2/stripe/webhook)
  → Event deserialization (Stripe SDK)
    CASE invoice.paid:
      → PaymentService.processPayment()
        → StripeAuditLogService.log()           # Audit trail
    CASE customer.subscription.updated:
      → SubscriptionService.syncSubscription()
        → UserPlanChangedEvent (published)      # Spring event
    CASE checkout.session.completed:
      → PaymentService.handleCheckoutComplete()
        → CredentialsService.findAndUpdateCompanyLevel()

Cross-domain calls: payment → audit_logs, payment → auth,
                    payment → com_courses, payment → plat_courses
```

### Flow 7: Resume AI Scoring
```
AtsController.scoreResume (/api/v2/resumes/ai/score)
  → AtsService.analyzeResume()
    → DocumentExtractionService.extractText()   # PDF/DOCX parsing
    → AIToolService.callOpenAI()                # AI scoring
    → ResumeUploadedEvent (published)
    → ResumeParsedEvent (published)

Cross-domain calls: resume → ai_tool (OpenAI)
```

### Flow 8: Ambassador Reward Flow
```
AmbassadorRewardController (/api/v2/ambassador/reward)
  → AmbassadorRewardService.processReward()
    → AmbassadorPointService.addPoints()
    → RewardAuditService.logReward()            # In audit_logs domain
    → LeaderboardService.updateRanking()

Cross-domain calls: ambassador → auth, ambassador → payment,
                    ambassador → plat_courses, audit_logs → ambassador
```

### Flow 9: Community Post Creation
```
PostController.createPost (/api/v2/posts)
  → PostService.createPost()
    → PostRepository.save()
    → ContentCreatedEvent (published)
      → ReputationEventListener.onContentCreated()
        → ReputationService.addPoints()

Cross-domain calls: community → user (author lookup),
                    reputation → community (event subscription)
```

### Flow 10: Finance Scenario Simulation
```
FinScenarioController (/api/finance/scenarios)
  → FinScenarioService.runSimulation()
    → FinComputationEngine.compute()
    → FinancialsChangedEvent (published)
      → AnalyticsEventListener.onFinancialsChanged()
      → FinAiTrainingEventListener.onFinancialsChanged()

Cross-domain calls: finance_planning → auth, finance_planning → workspace
```

### Flow 11: LifePlanr AI Coaching
```
LifePlannerAIController (/api/lifeplanner/ai/coach)
  → LifePlannerAIService.getCoachingResponse()
    → CreditService.deductCredits()             # Check/deduct credits
    → LLMClient.chat()                          # AI call
    → JournalService.logInteraction()            # Save context

Cross-domain calls: lifeplanner → user (profile context)
```

### Flow 12: Lead Campaign Automation
```
LeadCampaignController (/api/leads/campaigns)
  → LeadCampaignService.executeCampaign()
    → LeadAutomationService.triggerWorkflows()
    → LeadScoringService.scoreLeads()
    → LNewSignalEvent (published)
    → HTMLEmailService.sendCampaignEmail()

Cross-domain calls: leads → audit_logs (logging)
```

---

# Phase 6: DDD Boundary Analysis

## 6.1 Bounded Contexts

### Context 1: Identity & Access Management
```
Aggregates:
- Credentials (root: CredentialsModel)
- Role (root: RoleModel)

Entities:
- CredentialsModel, RoleModel, PermissionModel, PasswordResetToken

Value Objects:
- PlatformRole, SecurityRole, EntitlementPlan, CustomUserDetails

Repositories:
- CredentialsRepository, RoleRepository, PermissionRepository
- PasswordResetTokenRepository, TokenRepository

Domain Services:
- CredentialsService, AuthService, RoleService, PermissionService
- UserPermissionsService, CustomUserDetailsService
- JwtService, ValidateTokenService

External Dependencies:
- OAuth2 providers (Google, GitHub, Facebook, LinkedIn)
- com_job_portal (CompanyModel — LEAK)
- finance_planning (FinWorkspace — LEAK)
- workspace (WorkspaceModel — LEAK)
- user (EmployeeModel — LEAK)
```

### Context 2: User Profile
```
Aggregates:
- Employee (root: EmployeeModel)
- TrainerProfile (root: TrainerProfile)

Entities:
- EmployeeModel, EmpSkillsModel, EmpEducationModel, EmpExperiencesModel
- EmpCertificatesModel, EmpContactModel, EmpProjectsModel
- EmpFollowersModel, EmpFollowingModel, TrainerProfile, NotificationPreferences

Value Objects:
- ProfileCompleted (Map), SocialLinksDTO

Repositories:
- EmployeeRepository, EmpSkillsRepository, EmpEducationRepository, etc.

Domain Services:
- EmployeeService, ProfileUpdateService, TrainerProfileService

External Dependencies:
- auth (CredentialsModel — bidirectional LEAK)
- common (batch lookups)
- plat_courses, plat_job_portal (LEAKS)
```

### Context 3: Education Platform
```
Aggregates:
- Course (root: ECourse)
- Enrollment (root: EEnrollment)
- Wallet (root: EWallet)

Entities:
- 51 models including ECourse, ELesson, EQuiz, EAssignment, ECertificate
- EEnrollment, EProgress, EWallet, ELedgerEntry, EPayout
- EWorkspace, EUser, ESubscription

Value Objects:
- 29 enums (course status, payment status, etc.)

Repositories:
- 48 repositories

Domain Services:
- 68 services

External Dependencies:
- auth (user verification)
- subscription (plan checks — circular)
- referral (referral tracking — circular)
- Stripe, OpenAI/LLM
```

### Context 4: Financial Planning
```
Aggregates:
- FinWorkspace (root)
- FinPlan (root)
- FinScenario (root)

Entities:
- FinWorkspace, FinPlan, FinScenario, FinCollabSession, etc.

Repositories:
- 12 repositories

Domain Services:
- FinComputationEngine, FinScenarioService, FinCollaborationService

External Dependencies:
- auth, user, workspace
```

### Context 5: Life Planning
```
Aggregates:
- Planner (root: DailySchedule)
- Journal (root: JournalEntry)
- Goal (root: Goal)

Entities:
- DailySchedule, JournalEntry, Goal, MoodEntry, CreditBalance

Repositories:
- DailyScheduleRepository, JournalRepository, GoalRepository, etc.

Domain Services:
- PlannerService, JournalService, GoalService, LifePlannerAIService

External Dependencies:
- user (profile), Stripe (credits)
```

### Context 6: Job Portal & Recruitment
```
Aggregates:
- Company (root: CompanyModel)
- JobPost (root: CmpPostedJobsModel / JobPostModel)
- Application (root: JobApplyModel)

Entities:
- CompanyModel, CmpPostedJobsModel, CmpSocialModel
- JobApplyModel, PreOrderModel, InterviewQuestionModel
- RecruiterModel

Repositories:
- CompanyRepository, CmpPostedJobsRepository, JobApplyRepository, etc.

External Dependencies:
- auth, common, user
```

### Context 7: Community & Social
```
Aggregates:
- Community (root: CommunityModel)
- Post (root: PostModel)

Entities:
- CommunityModel, PostModel, CommentModel
- ReputationEvent, ReputationScore

Events:
- PostUpvotedEvent, CommentUpvotedEvent, ContentCreatedEvent

External Dependencies:
- user (profiles), article (content)
```

### Context 8: Payment & Billing
```
Aggregates:
- Payment (root: PaymentModel)
- Subscription (root: Subscription)

Entities:
- PaymentModel, InvoiceModel, UsageDataModel
- Subscription, FeatureFlag

External Dependencies:
- Stripe, auth, audit_logs, com_courses, plat_courses
```

### Context 9: Lead Management
```
Aggregates:
- Lead (root: LeadModel)
- Campaign (root: CampaignModel)

Entities:
- LeadModel, LTimelineEvent, CampaignModel

External Dependencies:
- audit_logs
```

### Context 10: Ambassador & Referral
```
Aggregates:
- AmbassadorProfile (root)
- Referral (root: ReferralModel)

Entities:
- AmbassadorProfile, AmbassadorReward, AmbassadorSession
- ReferralModel

External Dependencies:
- auth, payment, plat_courses, edu (circular via referral)
```

### Context 11: Content Management
```
Aggregates:
- Article (root: ArticleModel)
- Announcement (root: AnnouncementModel)

Events:
- ArticlePublishedEvent, AnnouncementPublishedEvent

External Dependencies:
- ai_tool
```

### Context 12: Platform Infrastructure
```
Aggregates:
- TrackingEvent (root)
- AuditLog (root, multiple types)
- SystemNotification (root)

Entities:
- TrackingEvent, SchedulerLogModel, StripeAuditLog
- AsyncUpdateAuditLog, ClientActAuditLog, CourseReminderLog
- SystemNotificationsModel, FeatureModel, IssueModel, Login

External Dependencies:
- auth, _private, common, multiple domains for audit
```

## 6.2 Context Integrity Issues

### Context Leaks (7 Critical)

| Source Context | Leaked Into | Via |
|---|---|---|
| Identity (auth) | Job Portal | `CompanyRepository` direct access in `CredentialsService` |
| Identity (auth) | Finance | `FinWorkspaceRepository` direct access in `CredentialsService` |
| User Profile | Auth | Bidirectional: `EmployeeRepository` in auth, `CredentialsModel` in user |
| Education | Subscription | Circular: edu → subscription → edu |
| Education | Referral | Circular: edu → referral → edu |
| Payment | Courses | Direct `com_courses`/`plat_courses` repository access |
| Common | Everything | Catches all via batch controller, ambassador, auth, courses |

### Misplaced Classes (5 Groups)

| Class | Current Location | Should Be |
|---|---|---|
| `CompanyRepository` access | auth/CredentialsService | Should use a port/interface |
| `FinWorkspaceRepository` access | auth/CredentialsService | Should use a port/interface |
| `BatchController` | common/ | Should be a gateway/facade per product |
| `FileUploadController` | common/ | Should be shared/storage |
| `LoginController` + Login model | common/ | Should be in auth or user |

### Duplicated Logic (4 Instances)

| Logic | Locations | Resolution |
|---|---|---|
| JWT handling | `shared/security/JwtService` + `edu/service/EduJwtService` | Consolidate into shared |
| User lookup | Direct repo access in 10+ domains | Use shared UserLookupPort |
| Audit logging | 3 separate audit packages | Consolidate into shared/audit |
| AI/LLM client | `ai_tool/`, `shared/ai/`, `edu/ai/`, `lifeplanner/ai/` | Consolidate into shared/ai |
