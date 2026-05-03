# Models Analysis

| Model Name | Collection | Fields Count | Relationships |
|------------|------------|--------------|---------------|
| AIQuota | ai_quotas | 5 | UNKNOWN(User) |
| AIUsage | ai_usage_logs | 5 | UNKNOWN(User), EMBEDDED(AIUsageType) |
| CreditRecord | course_ai_credit_record | 3 |  |
| AmbassadorLeaderboardModel | ambassador_leaderboard | 10 | UNKNOWN(Ambassador) |
| AmbassadorPointAudit | ambassador_point_audit | 6 | UNKNOWN(Ambassador) |
| AmbassadorProfileModel | ambassador_profiles | 31 | EMBEDDED(AmbassadorLifecycle), UNKNOWN(Employee), EMBEDDED(Map<String, Object>) |
| AmbassadorRewardModel | ambassador_rewards | 7 | UNKNOWN(Ambassador), UNKNOWN(Task) |
| AmbassadorSessionModel | ambassador_sessions | 8 | UNKNOWN(Ambassador) |
| AmbReferralModel | ambassador_referrals | 9 | UNKNOWN(Ambassador), UNKNOWN(ReferredUser), UNKNOWN(Course) |
| BadgeModel | ambassador_badges | 7 | UNKNOWN(Ambassador), UNKNOWN(Task), UNKNOWN(Badge) |
| SwagModel |  | 10 | UNKNOWN(Ambassador), UNKNOWN(Task) |
| Announcement | announcements | 21 | EMBEDDED(AnnouncementStatus), EMBEDDED(AnnouncementType), EMBEDDED(AnnouncementVisibility), EMBEDDED(AnnouncementPriority) |
| Article | articles | 22 | UNKNOWN(Author), EMBEDDED(ArticleStatus) |
| ArticleEvaluationLog | article_evaluations | 9 | UNKNOWN(Article), EMBEDDED(ArticleEvaluationDTO) |
| Tag | article_tags | 3 |  |
| AsyncUpdateAuditLog | async_update_audit_log | 11 | UNKNOWN(Course), UNKNOWN(Batch), UNKNOWN(Employee) |
| ClientActAuditLog | client_act_audit_log | 8 | UNKNOWN(User), UNKNOWN(Session), EMBEDDED(Map<String, Object>) |
| CourseReminderAuditLog | course_reminder_audit_logs | 15 | UNKNOWN(Employee), UNKNOWN(Course), UNKNOWN(Module) |
| LeadOSAuditLog | leados_audit_logs | 9 | UNKNOWN(Workspace), UNKNOWN(User), UNKNOWN(Entity), EMBEDDED(Map<String, Object>) |
| SchedulerLogModel |  | 6 |  |
| StripeAuditLog | stripe_audit_logs | 14 | UNKNOWN(Event), UNKNOWN(Session), UNKNOWN(Customer), UNKNOWN(Subscription), UNKNOWN(PaymentIntent) |
| TaskRewardAuditModel |  | 10 | UNKNOWN(Ambassador), UNKNOWN(Task), UNKNOWN(Reward) |
| CredentialsModel | portal_credentials | 22 | UNKNOWN(Employee), UNKNOWN(Company), EMBEDDED(List<Map<String, String>>), UNKNOWN(Referrer), UNKNOWN(Ambassador), UNKNOWN(ActiveWorkspace) |
| PasswordResetTokenModel | portal_password_reset_tokens | 4 | UNKNOWN(User) |
| PermissionModel | permissions | 6 |  |
| RoleModel | roles | 5 |  |
| FeatureModel | portal_feature-requests | 4 |  |
| IssueModel | portal_report-issues | 4 |  |
| Login | portal_logins | 5 | UNKNOWN(User), EMBEDDED(LoginMetaDTO), EMBEDDED(LoginEventDTO) |
| SystemNotificationsModel | portal_systemNotifications | 6 |  |
| Activity | community_activities | 5 | UNKNOWN(User), UNKNOWN(Target) |
| Comment | comments | 12 | UNKNOWN(Post), UNKNOWN(Parent), UNKNOWN(Author), EMBEDDED(Reaction), EMBEDDED(Comment) |
| Community | communities | 15 | UNKNOWN(Creator), EMBEDDED(CommunityPrivacy) |
| CommunityMember | community_members | 8 | UNKNOWN(Community), UNKNOWN(User), EMBEDDED(MemberRole) |
| Notification | community_notifications | 7 | UNKNOWN(Recipient), UNKNOWN(Sender), EMBEDDED(NotificationType), UNKNOWN(Target) |
| Post | posts | 29 | UNKNOWN(Author), UNKNOWN(Community), UNKNOWN(QuotedPost), EMBEDDED(PostContent), EMBEDDED(PostMetrics), EMBEDDED(Reaction), EMBEDDED(LinkPreview) |
| Report | community_reports | 7 | UNKNOWN(Reporter), UNKNOWN(Target), EMBEDDED(ReportTargetType), EMBEDDED(ReportStatus) |
| CourseBatchModel | course_batches | 26 | UNKNOWN(Course), EMBEDDED(InstallmentDTO), EMBEDDED(ModuleDTO), EMBEDDED(MaterialsDTO), EMBEDDED(QuizDTO) |
| CourseModel | job_hunter_courses | 36 | UNKNOWN(Company), UNKNOWN(Trainer), EMBEDDED(InstallmentDTO), EMBEDDED(ModuleDTO), EMBEDDED(MaterialsDTO), EMBEDDED(QuizDTO), EMBEDDED(CourseMissedNotify) |
| RecordedCourseModel | recorded_courses | 30 | EMBEDDED(RecModuleDTO), EMBEDDED(RecordedCourseReviewDTO), UNKNOWN(Company), UNKNOWN(Trainer), EMBEDDED(InstallmentDTO) |
| TrainCompanyModel | train_company | 6 | EMBEDDED(TeamMemberDTO) |
| TrainersModel | trainers | 12 |  |
| CmpPostedJobsModel | portal_cmp_posted_jobs | 6 | UNKNOWN(Company), EMBEDDED(PostedJobsDTO) |
| CmpSocialModel | portal_cmp_socials | 3 | UNKNOWN(Company), EMBEDDED(SocialLinksDTO) |
| CompanyModel | portal_companies | 33 |  |
| StandaloneFileModel | standalone_files | 12 | UNKNOWN(Owner), UNKNOWN(Parent) |
| EAffiliateCommissions | edu_affiliate_commissions | 7 | UNKNOWN(Affiliate), UNKNOWN(Transaction), UNKNOWN(Course) |
| EAffiliateLinks | edu_affiliate_links | 6 | UNKNOWN(Affiliate), UNKNOWN(Course) |
| EAffiliates | edu_affiliates | 8 | UNKNOWN(User), EMBEDDED(EAffiliateStatus) |
| EAiCredits | edu_ai_credits | 11 | UNKNOWN(User) |
| EAiUsage | edu_ai_usage | 9 | UNKNOWN(User), UNKNOWN(Course), EMBEDDED(EAIUsageType) |
| EAnalyticsEvents | edu_analytics_events | 6 | EMBEDDED(EAnalyticsEvent), UNKNOWN(User), UNKNOWN(Course), EMBEDDED(Map<String, Object>) |
| EApiKey | edu_api_keys | 11 | UNKNOWN(Owner) |
| EAssignments | edu_assignments | 14 | UNKNOWN(Course), UNKNOWN(Section), UNKNOWN(Lesson) |
| EAssignmentSubmissions | edu_assignment_submissions | 10 | UNKNOWN(User), UNKNOWN(Assignment), EMBEDDED(EGradingStatus) |
| EAuditLog | edu_audit_log | 10 | UNKNOWN(Actor), UNKNOWN(Target) |
| EBundles | edu_bundles | 9 | UNKNOWN(Creator) |
| ECertificates | edu_certificates | 12 | UNKNOWN(Course), UNKNOWN(User), UNKNOWN(Creator), UNKNOWN(Certificate), UNKNOWN(Template) |
| ECouponRedemption | edu_coupon_redemptions | 5 | UNKNOWN(Coupon), UNKNOWN(User), UNKNOWN(Transaction) |
| ECoupons | edu_coupons | 10 | UNKNOWN(Creator) |
| ECourses | edu_courses | 44 | UNKNOWN(Workspace), UNKNOWN(Creator), EMBEDDED(ECourseType), EMBEDDED(ECourseContentType), EMBEDDED(ECourseLevel), EMBEDDED(ECourseStatus), EMBEDDED(ECourseValidationStatus) |
| ECourseSections | edu_course_sections | 10 | UNKNOWN(Course), EMBEDDED(ELessons) |
| ECreatorFinanceSettings | edu_creator_finance_settings | 26 | UNKNOWN(User), UNKNOWN(StripeAccount), EMBEDDED(PayoutMethod), EMBEDDED(TaxForm) |
| ECreditLedger | edu_credit_ledger | 11 | UNKNOWN(User), EMBEDDED(ECreditLedgerActionType), UNKNOWN(Reference) |
| EduCourseReviewLog | edu_course_review_logs | 6 | UNKNOWN(Course), UNKNOWN(Reviewer) |
| EEnrollments | edu_enrollments | 34 | UNKNOWN(Course), UNKNOWN(User), UNKNOWN(Workspace), UNKNOWN(LastAccessedLesson), EMBEDDED(ECourses) |
| EFraudFlag | edu_fraud_flags | 10 | UNKNOWN(TargetUser), UNKNOWN(Reviewer) |
| EGifts | edu_gifts | 11 | UNKNOWN(Sender), UNKNOWN(Course), EMBEDDED(EGiftStatus) |
| EHoldingLedger | edu_holding_ledger | 12 | UNKNOWN(Beneficiary), EMBEDDED(EBeneficiaryType), UNKNOWN(Transaction), UNKNOWN(Course), EMBEDDED(EHoldingStatus) |
| ELearningPaths | edu_learning_paths | 7 | UNKNOWN(Workspace) |
| ELedgerEntry | edu_ledger_entries | 12 | EMBEDDED(EventType), EMBEDDED(EntryType), EMBEDDED(AccountType), UNKNOWN(Account), UNKNOWN(Course), UNKNOWN(Bundle) |
| ELessons | edu_lessons | 23 | UNKNOWN(Course), UNKNOWN(Section), EMBEDDED(ELessonType) |
| ENotifications | edu_notifications | 16 | UNKNOWN(User), UNKNOWN(Workspace), EMBEDDED(ENotificationType), UNKNOWN(RelatedEntity) |
| EPayouts | edu_payouts | 13 | UNKNOWN(Creator), EMBEDDED(EPayoutMethod), EMBEDDED(EPayoutStatus) |
| EPayoutSchedule | edu_payout_schedules | 8 | UNKNOWN(Creator) |
| EProfiles | edu_profiles | 19 | UNKNOWN(User), EMBEDDED(ESocialLinksDTO), EMBEDDED(EPrivacySettingsDTO), EMBEDDED(ENotificationSettingsDTO) |
| EQuizAttempts | edu_quiz_attempts | 10 | UNKNOWN(User), UNKNOWN(Quiz), EMBEDDED(EGradingStatus) |
| EQuizzes | edu_quizzes | 15 | UNKNOWN(Course), UNKNOWN(Section), UNKNOWN(Lesson), EMBEDDED(EQuizType), EMBEDDED(EQuestionDTO) |
| ERefund | edu_refunds | 19 | UNKNOWN(Transaction), UNKNOWN(StripeCheckoutSession), UNKNOWN(StripeCharge), UNKNOWN(StripeRefund), UNKNOWN(Buyer), UNKNOWN(Seller), UNKNOWN(Course), EMBEDDED(RefundType), EMBEDDED(RefundStatus) |
| EReports | edu_reports | 11 | UNKNOWN(Reporter), UNKNOWN(TargetEntity), EMBEDDED(EReportReason), EMBEDDED(EReportStatus) |
| EReviews | edu_reviews | 14 | UNKNOWN(Course), UNKNOWN(User) |
| ESubscriptions | edu_subscriptions | 25 | UNKNOWN(User), EMBEDDED(ESubscriptionPlan), EMBEDDED(ESubscriptionStatus), UNKNOWN(PaymentGateway), UNKNOWN(StripeCustomer), UNKNOWN(StripeSubscription), UNKNOWN(StripePrice) |
| ESystemSettings | edu_system_settings | 5 | EMBEDDED(Map<String, Object>) |
| ETransactions | edu_transactions | 33 | UNKNOWN(Course), UNKNOWN(Buyer), UNKNOWN(Seller), UNKNOWN(Affiliate), EMBEDDED(EPaymentMethod), EMBEDDED(EPaymentStatus), UNKNOWN(Transaction), UNKNOWN(StripeCheckoutSession), UNKNOWN(Bundle), UNKNOWN(Referrer) |
| ETrustScores | edu_trust_scores | 20 | UNKNOWN(Creator) |
| EUser | edu_user | 22 | UNKNOWN(SsoProvider), EMBEDDED(ESubscriptionPlan), EMBEDDED(ESubscriptionStatus) |
| EUserPreferences | edu_user_preferences | 10 | UNKNOWN(User) |
| EValidationReports | edu_validation_reports | 10 | UNKNOWN(Course), UNKNOWN(User), UNKNOWN(Reviewer), EMBEDDED(EValidationBreackdownDTO) |
| EWallet | edu_wallets | 7 | UNKNOWN(User) |
| EWalletTransaction | edu_wallet_transactions | 9 | UNKNOWN(User), EMBEDDED(TransactionType), EMBEDDED(TransactionStatus), UNKNOWN(Reference) |
| EWebhookEvent | edu_webhook_events | 13 | EMBEDDED(Failed events are stored with), UNKNOWN(StripeEvent), EMBEDDED(EventStatus) |
| EWorkspaceMembers | edu_workspace_members | 10 | UNKNOWN(Workspace), UNKNOWN(User), EMBEDDED(ERoles) |
| EWorkspaces | edu_workspaces | 17 | UNKNOWN(Owner), EMBEDDED(EWorkspaceType), EMBEDDED(ESubscriptionPlan), EMBEDDED(EWSettingsDTO), EMBEDDED(EWProfileDTO) |
| AnalyticsData | fin_analytics_data | 10 | UNKNOWN(Organization), UNKNOWN(Project), UNKNOWN(Scenario), EMBEDDED(Map<String, Object>) |
| MetricDefinition | fin_metric_definitions | 7 | UNKNOWN(Organization) |
| FinAiTrainingSnapshot | fin_ai_training_snapshots | 10 | UNKNOWN(Organization), UNKNOWN(Project), UNKNOWN(Scenario), UNKNOWN(User) |
| FinAssumption | fin_assumptions | 9 | UNKNOWN(Organization), UNKNOWN(Project) |
| FinAuditLog | fin_audit_logs | 11 | UNKNOWN(Organization), UNKNOWN(Project), UNKNOWN(User), UNKNOWN(Entity) |
| FinBudget | fin_budgets | 9 | UNKNOWN(Organization), UNKNOWN(Project), EMBEDDED(Map<String, Double>) |
| FinFinancialSnapshot | fin_financial_snapshots | 10 | UNKNOWN(Organization), UNKNOWN(Project), UNKNOWN(Scenario), EMBEDDED(Map<String, Double>) |
| FinPricingModel | fin_pricing_models | 10 | UNKNOWN(Organization), UNKNOWN(Project) |
| FinProject | fin_projects | 10 | UNKNOWN(Organization), UNKNOWN(Owner) |
| FinProjectMember | fin_project_members | 5 | UNKNOWN(Project), UNKNOWN(User), EMBEDDED(ProjectRole) |
| FinSalesPlan | fin_sales_plans | 8 | UNKNOWN(Organization), UNKNOWN(Project), EMBEDDED(Map<String, Integer>) |
| FinScenario | fin_scenarios | 9 | UNKNOWN(Organization), UNKNOWN(Project), EMBEDDED(AssumptionOverride) |
| FinWorkspace | fin_workspaces | 9 | UNKNOWN(Owner) |
| Scenario | scenarios | 10 | UNKNOWN(Organization), UNKNOWN(Project), UNKNOWN(ParentScenario), UNKNOWN(BaseVersion) |
| ScenarioOverride | fin_scenario_overrides | 9 | UNKNOWN(Scenario), EMBEDDED(OverrideOperation), UNKNOWN(Version) |
| ApplicantModel | applicants | 14 | UNKNOWN(Job), UNKNOWN(Company), UNKNOWN(Candidate) |
| JobPostModel | job_posts | 21 | UNKNOWN(Company) |
| LAutomation | lead_automations | 12 | UNKNOWN(Workspace), UNKNOWN(ActionTemplate) |
| LLeadAutomation | leads_automations | 13 | UNKNOWN(Workspace) |
| LCampaign | leads_campaigns | 10 | UNKNOWN(Workspace), UNKNOWN(Source), EMBEDDED(Map<String, Integer>) |
| LLead | leads_leads | 12 | UNKNOWN(Workspace), UNKNOWN(SourceSignal), EMBEDDED(LTimelineEvent) |
| LLeadCandidate | leads_candidates | 12 | UNKNOWN(Workspace), UNKNOWN(Source), UNKNOWN(RawSignal) |
| LLeadSource | leads_sources | 8 | UNKNOWN(Workspace), EMBEDDED(Map<String, Object>) |
| LLeadWorkspace | leads_workspaces | 6 | UNKNOWN(Owner) |
| LNotification | lead_notifications | 9 | UNKNOWN(Workspace), UNKNOWN(User) |
| LRawSignal | leads_raw_signals | 13 | UNKNOWN(Source), UNKNOWN(Workspace), UNKNOWN(Platform), EMBEDDED(Map<String, Object>) |
| LTask | lead_background_tasks | 11 | UNKNOWN(Workspace), UNKNOWN(User), EMBEDDED(Map<String, Object>) |
| LTemplate | lead_templates | 9 | UNKNOWN(Workspace) |
| AICacheEntry | lp_ai_cache | 6 |  |
| UserCredits | lp_user_credits | 5 | UNKNOWN(User), EMBEDDED(SubscriptionTier) |
| Goal | lp_goals | 11 | UNKNOWN(Goal), UNKNOWN(User), EMBEDDED(GoalType), EMBEDDED(GoalTimeline) |
| JournalEntry | lp_journal_entries | 6 | UNKNOWN(User) |
| MoodEntry | lp_mood_entries | 6 | UNKNOWN(User) |
| WeeklyMoodSummary | lp_weekly_mood_summaries | 9 | UNKNOWN(User) |
| LPNotification | lp_notifications | 7 | UNKNOWN(User) |
| DailySchedule | lp_daily_schedules | 16 | UNKNOWN(Schedule), UNKNOWN(Plan), UNKNOWN(User), EMBEDDED(ScheduleTask), UNKNOWN(Task) |
| StudyPlan | lp_study_plans | 10 | UNKNOWN(Plan), UNKNOWN(Goal), UNKNOWN(User), EMBEDDED(RoadmapItem), EMBEDDED(WeeklyPlan) |
| User | lp_users | 6 |  |
| UserPreferences | lp_preferences | 17 | UNKNOWN(User), EMBEDDED(Map<String, Object>) |
| UserProfile | lp_profiles | 14 | UNKNOWN(User), EMBEDDED(Map<String, Object>) |
| ChatRoom | chat_rooms | 9 | EMBEDDED(RoomType), UNKNOWN(Community) |
| Message | messages | 19 | UNKNOWN(Room), UNKNOWN(Sender), EMBEDDED(MessageType), EMBEDDED(Map<String, LocalDateTime>), EMBEDDED(Map<String, Object>), UNKNOWN(ForwardedFrom), UNKNOWN(ReplyTo) |
| UserPresence |  | 3 | UNKNOWN(User), EMBEDDED(PresenceStatus) |
| BillingAddressModel | portal_billing_address | 7 | UNKNOWN(Company) |
| BillingHistoryModel | portal_billing_history | 8 | UNKNOWN(Company), UNKNOWN(User), UNKNOWN(Session) |
| InvoicesModel | portal_invoices | 13 | UNKNOWN(Company), UNKNOWN(Invoice), UNKNOWN(Subscription), UNKNOWN(Session) |
| PaymentMethodsModel | portal_payment_methods | 8 | UNKNOWN(Company), UNKNOWN(User), UNKNOWN(Session) |
| PaymentSubscriptionsModel | portal_subscriptions | 9 | UNKNOWN(Company), UNKNOWN(Subscription) |
| PrePaymentModel | portal_pre_payment_data | 14 | UNKNOWN(Company), UNKNOWN(Subscription), UNKNOWN(PaymentMethod), UNKNOWN(BillingAddress), UNKNOWN(Invoice) |
| RecordedCoursePayment | recorded_course_payments | 17 | UNKNOWN(Course), UNKNOWN(Learner), UNKNOWN(Trainer), UNKNOWN(Company), UNKNOWN(Transaction) |
| UsageDataModel | portal_usage_data | 10 | UNKNOWN(Company) |
| AmbassadorTaskProgressModel | ambassador_task_progress | 12 | UNKNOWN(Ambassador), UNKNOWN(Task) |
| BadgeDefinition |  | 8 |  |
| CourseCertificateModel | course_certificates | 11 | UNKNOWN(Employee), UNKNOWN(Course), UNKNOWN(Certificate) |
| CourseCouponsModel | course_coupons | 33 | UNKNOWN(StripeCoupon), UNKNOWN(User), EMBEDDED(public,), EMBEDDED(private String), EMBEDDED(Null or), UNKNOWN(RedeemedForCourse), UNKNOWN(RedeemedForInstallment), UNKNOWN(Task), UNKNOWN(Campaign) |
| CourseReminderLog | course_reminder_logs | 6 | UNKNOWN(Employee), UNKNOWN(Module), UNKNOWN(Course) |
| EmpCoursesModel | portal_emp_courses | 8 | UNKNOWN(Employee), EMBEDDED(CourseEnrollment), EMBEDDED(RecordedCourseEnrollment) |
| GamificationTaskModel | gamification_tasks | 17 | EMBEDDED(BRONZE, GOLD, PLATINUM, or), UNKNOWN(Reward), EMBEDDED(Map<String, Object>), EMBEDDED(lowest,) |
| QuizAttempt | quiz_attempts | 11 | UNKNOWN(Employee), UNKNOWN(Course), UNKNOWN(Module), UNKNOWN(Quiz), EMBEDDED(QuestionAnswer) |
| SwagItem |  | 8 |  |
| InterviewQuestionModel | portal_interviewQuestion | 15 | EMBEDDED(Question), EMBEDDED(Answer) |
| JobApplyModel | portal_job_applicants | 5 | UNKNOWN(Company), UNKNOWN(Job), EMBEDDED(JobApplicantDTO), EMBEDDED(JobViewerDTO) |
| PreOrderModel | portal_preorder | 5 |  |
| Referral | referrals | 8 | UNKNOWN(Referrer), UNKNOWN(ReferredUser), EMBEDDED(ReferralType), EMBEDDED(ReferralStatus) |
| ReferralCode | referral_codes | 4 | UNKNOWN(User) |
| ReferralCommission | referral_commissions | 6 | UNKNOWN(Referrer), UNKNOWN(ReferredCreator) |
| ReputationEvent | reputation_events | 6 | UNKNOWN(User), EMBEDDED(ReputationSourceType), UNKNOWN(Source) |
| UserBadge | user_badges | 4 | UNKNOWN(User), EMBEDDED(BadgeType) |
| UserReputation | user_reputation | 7 | UNKNOWN(User) |
| ResumeModel | resumes | 72 | UNKNOWN(Employee), UNKNOWN(Template), EMBEDDED(PersonalInfo), EMBEDDED(WorkExperience), EMBEDDED(Education), EMBEDDED(Skill), EMBEDDED(Project), EMBEDDED(Certificate), EMBEDDED(CustomSection), EMBEDDED(ResumeSettings), EMBEDDED(CustomSectionItem) |
| SoftwareAppModel | portal_software_apps | 17 | UNKNOWN(Company) |
| FeatureFlag | feature_flags | 4 | EMBEDDED(ESubscriptionPlan) |
| Subscription | subscriptions | 18 | UNKNOWN(User), EMBEDDED(ESubscriptionPlan), EMBEDDED(ESubscriptionStatus), UNKNOWN(StripeCustomer), UNKNOWN(StripeSubscription), UNKNOWN(StripePrice) |
| SupportRequestModel | support_requests | 7 |  |
| TrackingEvent | events | 80 | UNKNOWN(Tracking), UNKNOWN(Session), UNKNOWN(User), UNKNOWN(Element), EMBEDDED(0), UNKNOWN(Experiment), UNKNOWN(Funnel), UNKNOWN(Form), EMBEDDED(Map<String, Object>) |
| EmpCertificatesModel | portal_emp_certificates | 3 | UNKNOWN(Employee), EMBEDDED(EmpCertificatesDTO) |
| EmpContactModel | portal_emp_contact | 5 | UNKNOWN(Employee), EMBEDDED(EmpContactDTO), EMBEDDED(SocialLinksDTO) |
| EmpEducationModel | portal_emp_education | 3 | UNKNOWN(Employee), EMBEDDED(EmpEducationDTO) |
| EmpExperiencesModel | portal_emp_experiences | 3 | UNKNOWN(Employee), EMBEDDED(EmpExperiencesDTO) |
| EmpFollowersModel | portal_emp_followers | 3 | UNKNOWN(Employee), EMBEDDED(EmpFollowersDTO) |
| EmpFollowingModel | portal_emp_following | 3 | UNKNOWN(Employee), EMBEDDED(EmpFollowingDTO) |
| EmployeeModel | portal_employees | 31 | EMBEDDED(FavJobDTO), EMBEDDED(PlatformRole), UNKNOWN(Company) |
| EmpProjectsModel | portal_emp_projects | 3 | UNKNOWN(Employee), EMBEDDED(EmpProjectsDTO) |
| EmpSkillsModel | portal_emp_skills | 3 | UNKNOWN(Employee), EMBEDDED(EmpSkillsDTO) |
| TrainerProfile | trainer_profiles | 16 | UNKNOWN(Employee) |
| WorkspaceModel | workspaces | 8 | UNKNOWN(Owner) |
| UserActivity | portal_user_activity | 12 | UNKNOWN(User) |
| WhitelistDomains | portal_whitelist_domains | 4 |  |
| ContactSubmission | contact_form_submissions | 6 |  |
| CtaLeadSubmission | cta_lead_submissions | 8 |  |
| NewsLatterModel | portal_news_letter | 2 |  |
| TokenModel | portal_tokens | 5 |  |

## Detailed Model Fields
### AIQuota
**Collection:** ai_quotas

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed(unique = true)
    private |
| monthlyLimit | Integer |  |
| used | Integer |  |
| resetDate | Instant |  |

### AIUsage
**Collection:** ai_usage_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| type | AIUsageType |  |
| creditsUsed | Integer |  |
| createdAt | Instant | CreatedDate
    private |

### CreditRecord
**Collection:** course_ai_credit_record

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| creditsRemaining | int |  |
| lastReset | LocalDate |  |

### AmbassadorLeaderboardModel
**Collection:** ambassador_leaderboard

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| type | String |  |
| ambassadorId | String |  |
| name | String |  |
| email | String |  |
| level | String |  |
| score | int |  |
| rank | int |  |
| generatedAt | Instant |  |
| expireAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s")
    private |

### AmbassadorPointAudit
**Collection:** ambassador_point_audit

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| ambassadorId | String |  |
| reason | String |  |
| points | int |  |
| createdAt | Instant |  |
| metadata | String |  |

### AmbassadorProfileModel
**Collection:** ambassador_profiles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| firstName | String |  |
| lastName | String |  |
| name | String |  |
| email | String |  |
| motivation | String |  |
| profileLink | String |  |
| consentGiven | boolean |  |
| appliedAt | Instant |  |
| lifecycle | AmbassadorLifecycle |  |
| applicationStatus | String |  |
| employeeId | String | Indexed(unique = true, sparse = true)
    private |
| level | String |  |
| totalReferrals | int |  |
| coursePurchasesByReferrals | int |  |
| hostedSessions | int |  |
| trainingSessionsAttended | int |  |
| active | boolean |  |
| joinedAt | Instant |  |
| lastActivity | Instant |  |
| badges | List<String> |  |
| referralCode | String |  |
| status | String |  |
| interviewNote | String |  |
| badgeHistory | List<String> |  |
| perks | Map<String, Object> |  |
| points | int |  |
| lastPointEarnedAt | Instant |  |
| totalLogins | int |  |
| consecutiveLoginDays | int |  |
| lastLoginDate | Instant |  |

### AmbassadorRewardModel
**Collection:** ambassador_rewards

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| ambassadorId | String |  |
| taskId | String |  |
| rewardType | String |  |
| status | String |  |
| issuedAt | Instant |  |
| redeemedAt | Instant |  |

### AmbassadorSessionModel
**Collection:** ambassador_sessions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| ambassadorId | String | Indexed
    private |
| type | String |  |
| topic | String |  |
| sessionLink | String |  |
| date | Instant |  |
| attendeeCount | int |  |
| completed | boolean |  |

### AmbReferralModel
**Collection:** ambassador_referrals

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| referralCode | String | Indexed
    private |
| ambassadorId | String | Indexed
    private |
| referredUserId | String |  |
| referredAt | Instant |  |
| courseId | String |  |
| referredPlatform | String |  |
| courseEnrolled | boolean |  |
| enrolledAt | Instant |  |

### BadgeModel
**Collection:** ambassador_badges

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| ambassadorId | String |  |
| taskId | String |  |
| badgeId | String |  |
| title | String |  |
| description | String |  |
| earnedAt | Instant |  |

### SwagModel
**Collection:** 

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| ambassadorId | String |  |
| taskId | String |  |
| swagType | String |  |
| status | String |  |
| requestedAt | Instant |  |
| shippedAt | Instant |  |
| deliveredAt | Instant |  |
| shippingAddress | String |  |
| trackingCode | String |  |

### Announcement
**Collection:** announcements

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| title | String |  |
| slug | String | Indexed(unique = true)
    private |
| summary | String |  |
| content | String |  |
| coverImage | String |  |
| status | AnnouncementStatus |  |
| type | AnnouncementType |  |
| visibility | AnnouncementVisibility |  |
| priority | AnnouncementPriority |  |
| publishedAt | LocalDateTime |  |
| expiresAt | LocalDateTime |  |
| createdBy | String |  |
| pinned | boolean |  |
| generateSummary | boolean |  |
| createdAt | LocalDateTime |  |
| updatedAt | LocalDateTime |  |
| aiSummary | String |  |
| aiHighlights | List<String> |  |
| aiSnippet | String |  |
| aiSeoDescription | String |  |

### Article
**Collection:** articles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| title | String | TextIndexed
    private |
| slug | String | Indexed(unique = true)
    private |
| content | String | TextIndexed
    private |
| excerpt | String | TextIndexed
    private |
| authorId | String |  |
| coverImage | String |  |
| tagIds | List<String> |  |
| status | ArticleStatus |  |
| readTime | int |  |
| views | long |  |
| likes | long |  |
| featured | boolean |  |
| createdAt | LocalDateTime |  |
| updatedAt | LocalDateTime |  |
| aiSummary | String |  |
| aiHighlights | List<String> |  |
| aiSnippet | String |  |
| aiSeoDescription | String |  |
| markAsHighValue | boolean |  |
| markAsInformative | boolean |  |
| manualReviewRequired | boolean |  |

### ArticleEvaluationLog
**Collection:** article_evaluations

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| articleId | String |  |
| validationVersion | String |  |
| aiProvider | String |  |
| promptVersion | String |  |
| responseHash | String |  |
| rawApiResponse | String |  |
| evaluationResult | ArticleEvaluationDTO |  |
| evaluatedAt | LocalDateTime |  |

### Tag
**Collection:** article_tags

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String | Indexed(unique = true)
    private |
| slug | String | Indexed(unique = true)
    private |

### AsyncUpdateAuditLog
**Collection:** async_update_audit_log

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String |  |
| batchId | String |  |
| employeeId | String |  |
| operation | String |  |
| status | String |  |
| retryCount | int |  |
| errorMessage | String |  |
| createdAt | LocalDateTime |  |
| updatedAt | LocalDateTime |  |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s")
    private |

### ClientActAuditLog
**Collection:** client_act_audit_log

| Field | Type | Annotations |
|-------|------|-------------|
| userId | String | Id
    private |
| ipAddress | String |  |
| sessionId | String |  |
| action | String |  |
| source | String |  |
| details | Map<String, Object> |  |
| timestamp | LocalDateTime |  |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s")
    private |

### CourseReminderAuditLog
**Collection:** course_reminder_audit_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| employeeName | String |  |
| email | String |  |
| courseId | String |  |
| courseName | String |  |
| moduleId | String |  |
| moduleName | String |  |
| reminderType | String |  |
| status | String |  |
| timezone | String |  |
| scheduledStartTime | String |  |
| message | String |  |
| timestamp | Instant |  |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s")
    private |

### LeadOSAuditLog
**Collection:** leados_audit_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String |  |
| userId | String |  |
| action | String |  |
| entityId | String |  |
| entityType | String |  |
| details | Map<String, Object> |  |
| ipAddress | String |  |
| timestamp | LocalDateTime |  |

### SchedulerLogModel
**Collection:** 

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| jobName | String |  |
| runAt | Instant |  |
| status | String |  |
| message | String |  |
| expireAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s")
    private |

### StripeAuditLog
**Collection:** stripe_audit_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| eventId | String |  |
| eventType | String |  |
| sessionId | String |  |
| customerId | String |  |
| subscriptionId | String |  |
| paymentIntentId | String |  |
| status | String |  |
| errorMessage | String |  |
| rawPayload | String | Lob
    private |
| retryCount | int |  |
| createdAt | Date |  |
| updatedAt | Date |  |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s")
    private |

### TaskRewardAuditModel
**Collection:** 

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| ambassadorId | String |  |
| taskId | String |  |
| rewardType | String |  |
| rewardId | String |  |
| rewardTitle | String |  |
| status | String |  |
| note | String |  |
| issuedAt | Instant |  |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s")
    private |

### CredentialsModel
**Collection:** portal_credentials

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| companyId | String |  |
| firstname | String |  |
| lastname | String |  |
| email | String |  |
| password | String |  |
| role | String |  |
| roles | List<String> |  |
| permissions | List<String> |  |
| organizations | List<Map<String, String>> |  |
| userLevel | String |  |
| platformRole | String |  |
| registeredFrom | String |  |
| promotion | String |  |
| referrerId | String |  |
| accessedPlatforms | List<String> |  |
| active | boolean |  |
| disabled | boolean |  |
| ambassador | boolean |  |
| ambassadorId | String |  |
| activeWorkspaceId | String |  |

### PasswordResetTokenModel
**Collection:** portal_password_reset_tokens

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| token | String |  |
| userId | String |  |
| expirationDate | LocalDateTime |  |

### PermissionModel
**Collection:** permissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| description | String |  |
| category | String |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### RoleModel
**Collection:** roles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String | Indexed(unique = true)
    private |
| permissions | List<String> |  |
| inheritsFrom | List<String> |  |
| description | String |  |

### FeatureModel
**Collection:** portal_feature-requests

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| featureType | String |  |
| description | String |  |
| attachment | String |  |

### IssueModel
**Collection:** portal_report-issues

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| issueType | String |  |
| description | String |  |
| attachment | String |  |

### Login
**Collection:** portal_logins

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String |  |
| loginDates | List<String> |  |
| metaData | List<LoginMetaDTO> |  |
| events | List<LoginEventDTO> |  |

### SystemNotificationsModel
**Collection:** portal_systemNotifications

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| message | String |  |
| startTime | Date |  |
| endTime | Date |  |
| url | String |  |
| active | boolean |  |

### Activity
**Collection:** community_activities

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String |  |
| action | String |  |
| targetId | String |  |
| timestamp | LocalDateTime |  |

### Comment
**Collection:** comments

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| postId | String |  |
| parentId | String |  |
| authorId | String |  |
| mentionIds | List<String> |  |
| text | String |  |
| upvotes | int |  |
| downvotes | int |  |
| reactions | List<Post.Reaction> |  |
| timestamp | LocalDateTime |  |
| updatedAt | LocalDateTime |  |
| replies | List<Comment> |  |

### Community
**Collection:** communities

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| description | String |  |
| icon | String |  |
| bannerImage | String |  |
| creatorId | String |  |
| adminIds | List<String> |  |
| moderatorIds | List<String> |  |
| privacy | CommunityPrivacy |  |
| createdAt | LocalDateTime |  |
| updatedAt | LocalDateTime |  |
| rules | String |  |
| category | String |  |
| tags | List<String> |  |
| isVerified | boolean |  |

### CommunityMember
**Collection:** community_members

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| communityId | String |  |
| userId | String |  |
| role | MemberRole |  |
| joinedAt | LocalDateTime |  |
| isBanned | boolean |  |
| bannedAt | LocalDateTime |  |
| bannedReason | String |  |

### Notification
**Collection:** community_notifications

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| recipientId | String |  |
| senderId | String |  |
| type | NotificationType |  |
| targetId | String |  |
| isRead | boolean |  |
| timestamp | LocalDateTime |  |

### Post
**Collection:** posts

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| authorId | String |  |
| communityId | String |  |
| mentionIds | List<String> |  |
| quotedPostId | String |  |
| type | String |  |
| content | PostContent |  |
| metrics | PostMetrics |  |
| reactions | List<Reaction> |  |
| timestamp | LocalDateTime |  |
| updatedAt | LocalDateTime |  |
| trendingScore | double |  |
| title | String |  |
| text | String |  |
| url | String |  |
| linkPreview | LinkPreview |  |
| media | List<String> |  |
| tags | List<String> |  |
| title | String |  |
| description | String |  |
| image | String |  |
| siteName | String |  |
| upvotes | int |  |
| downvotes | int |  |
| comments | int |  |
| shares | int |  |
| emoji | String |  |
| count | int |  |
| userIds | List<String> |  |

### Report
**Collection:** community_reports

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| reporterId | String |  |
| targetId | String |  |
| targetType | ReportTargetType |  |
| reason | String |  |
| status | ReportStatus |  |
| timestamp | LocalDateTime |  |

### CourseBatchModel
**Collection:** course_batches

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String |  |
| batchName | String |  |
| startDate | String |  |
| fromTime | String |  |
| toTime | String |  |
| utcStart | String |  |
| utcEnd | String |  |
| trainerTimezone | String |  |
| courseStatus | String |  |
| publicity | boolean |  |
| currency | String |  |
| price | String |  |
| onetimePayment | boolean |  |
| paymentMethod | String |  |
| duration | String |  |
| language | String |  |
| platform | String |  |
| location | String |  |
| image | String |  |
| lecturer | String |  |
| installment | List<InstallmentDTO> |  |
| modules | List<ModuleDTO> |  |
| materials | List<MaterialsDTO> |  |
| quizzes | List<QuizDTO> |  |
| enrolledUserIds | List<String> |  |

### CourseModel
**Collection:** job_hunter_courses

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| trainerId | String |  |
| name | String |  |
| description | String |  |
| overview | String |  |
| category | String |  |
| organizer | String |  |
| level | String |  |
| currency | String |  |
| price | String |  |
| onetimePayment | boolean |  |
| installment | List<InstallmentDTO> | Field("installment")
    private |
| duration | String |  |
| modules | List<ModuleDTO> | Field("modules")
    private |
| rating | String |  |
| language | String |  |
| lecturer | String |  |
| image | String |  |
| skills | List<String> |  |
| requirements | List<String> |  |
| certificate | boolean |  |
| platform | String |  |
| location | String |  |
| startDate | String |  |
| fromTime | String |  |
| toTime | String |  |
| utcStart | String |  |
| utcEnd | String |  |
| trainerTimezone | String |  |
| courseStatus | String |  |
| paymentMethod | String |  |
| publicity | boolean |  |
| materials | List<MaterialsDTO> | Field("materials")
    private |
| quizzes | List<QuizDTO> | Field("quizzes")
    private |
| notifiers | List<CourseMissedNotify> | Field("notifiers")
    private |

### RecordedCourseModel
**Collection:** recorded_courses

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| title | String |  |
| subtitle | String |  |
| description | String |  |
| courseType | String |  |
| price | BigDecimal |  |
| published | boolean |  |
| approved | boolean |  |
| createdAt | String |  |
| updatedAt | String |  |
| modules | List<RecModuleDTO> | Field("modules")
    private |
| image | String |  |
| skills | List<String> |  |
| requirements | List<String> |  |
| level | String |  |
| lecturer | String |  |
| lecturerNameTag | String |  |
| lecturerEmail | String |  |
| language | String |  |
| category | String |  |
| reviews | List<RecordedCourseReviewDTO> | Field("reviews")
    private |
| rating | double |  |
| reviewCount | int |  |
| certificate | boolean |  |
| currency | String |  |
| companyId | String |  |
| trainerId | String |  |
| trainerShare | BigDecimal |  |
| platformShare | BigDecimal |  |
| installment | InstallmentDTO |  |

### TrainCompanyModel
**Collection:** train_company

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| email | String |  |
| address | String |  |
| phone | String |  |
| teamMembers | List<TeamMemberDTO> |  |

### TrainersModel
**Collection:** trainers

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| trainerName | String |  |
| trainerEmail | String |  |
| trainerPhone | String |  |
| trainerImage | String |  |
| trainerDescription | String |  |
| trainerExperience | String |  |
| trainerQualification | String |  |
| trainerStatus | String |  |
| trainerDateJoined | String |  |
| trainerDateUpdated | String |  |
| trainerRole | String |  |

### CmpPostedJobsModel
**Collection:** portal_cmp_posted_jobs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| companyName | String |  |
| companyLogo | String |  |
| companyLevel | String |  |
| postedJobs | List<PostedJobsDTO> | Field("postedJobs")
    private |

### CmpSocialModel
**Collection:** portal_cmp_socials

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| socialLinks | List<SocialLinksDTO> | Field("socialLinks")
    private |

### CompanyModel
**Collection:** portal_companies

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| shortDescription | String |  |
| companyStory | String |  |
| companyLevel | String |  |
| logo | String |  |
| location | String |  |
| profileBanner | String |  |
| image1 | String |  |
| image2 | String |  |
| image3 | String |  |
| foundedDate | String |  |
| founderName | String |  |
| headquarters | String |  |
| numberOfEmployees | String |  |
| website | String |  |
| socialLinks | String |  |
| contactEmail | String |  |
| contactNumber | String |  |
| postedJobs | String |  |
| joinedDate | String |  |
| isVerified | String |  |
| followers | String |  |
| following | String |  |
| accountNotifications | Object |  |
| marketingNotifications | Object |  |
| profileCompleted | Object |  |
| profileStatus | String |  |
| companyType | String |  |
| subscription_id | String |  |
| payment_method_id | String |  |
| billing_address_id | String |  |
| systemOwner | boolean |  |

### StandaloneFileModel
**Collection:** standalone_files

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| contentType | String |  |
| size | long |  |
| path | String |  |
| ownerId | String |  |
| parentId | String |  |
| isDirectory | boolean |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |
| storageType | String |  |
| isPublic | boolean |  |

### EAffiliateCommissions
**Collection:** edu_affiliate_commissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| affiliateId | String | Indexed
    private |
| transactionId | String | Indexed
    private |
| courseId | String |  |
| amount | Double |  |
| currency | String |  |
| createdAt | Instant | CreatedDate
    private |

### EAffiliateLinks
**Collection:** edu_affiliate_links

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| affiliateId | String | Indexed
    private |
| courseId | String | Indexed
    private |
| trackingCode | String | Indexed(unique = true)
    private |
| clicks | Long |  |
| createdAt | Instant | CreatedDate
    private |

### EAffiliates
**Collection:** edu_affiliates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed(unique = true)
    private |
| referralCode | String | Indexed(unique = true)
    private |
| commissionRate | Double |  |
| totalEarnings | Double |  |
| status | EAffiliateStatus | Indexed
    private |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### EAiCredits
**Collection:** edu_ai_credits

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed(unique = true)
    private |
| balance | Integer |  |
| monthlyLimit | Integer |  |
| lifetimePurchased | Integer |  |
| lifetimeUsed | Integer |  |
| expiresAt | Instant | Indexed
    private |
| lastResetDate | Instant | Indexed
    private |
| version | Long | Version
    private |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### EAiUsage
**Collection:** edu_ai_usage

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| courseId | String | Indexed
    private |
| prompt | String |  |
| response | String |  |
| type | EAIUsageType |  |
| usedCredits | Integer |  |
| createdBy | String |  |
| createdAt | Instant | CreatedDate
    private |

### EAnalyticsEvents
**Collection:** edu_analytics_events

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| type | EAnalyticsEvent | Indexed
    private |
| userId | String | Indexed
    private |
| courseId | String | Indexed
    private |
| metadata | Map<String, Object> |  |
| timestamp | Instant |  |

### EApiKey
**Collection:** edu_api_keys

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| apiKey | String | Indexed(unique = true)
    private |
| apiKeyHint | String |  |
| ownerId | String | Indexed
    private |
| name | String |  |
| isActive | boolean |  |
| allowedIps | List<String> |  |
| scopes | List<String> |  |
| expiresAt | Instant |  |
| lastUsedAt | Instant |  |
| createdAt | Instant |  |

### EAssignments
**Collection:** edu_assignments

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String | Indexed
    private |
| sectionId | String | Indexed
    private |
| lessonId | String | Indexed
    private |
| title | String |  |
| description | String |  |
| instructions | String |  |
| maxScore | Double |  |
| weightage | Double |  |
| dueDate | Instant |  |
| isPublished | Boolean |  |
| createdBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### EAssignmentSubmissions
**Collection:** edu_assignment_submissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| assignmentId | String | Indexed
    private |
| content | String |  |
| status | EGradingStatus |  |
| score | Double |  |
| feedback | String |  |
| gradedBy | String |  |
| gradedAt | Instant |  |
| submittedAt | Instant | CreatedDate
    private |

### EAuditLog
**Collection:** edu_audit_log

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| actorId | String | Indexed
    private |
| action | String | Indexed
    private |
| targetId | String |  |
| targetType | String |  |
| previousState | String |  |
| newState | String |  |
| ipAddress | String |  |
| userAgent | String |  |
| createdAt | Instant | Indexed
    private |

### EBundles
**Collection:** edu_bundles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| creatorId | String | Indexed
    private |
| name | String |  |
| bundlePrice | Double |  |
| originalTotalPrice | Double |  |
| savingsPercent | Double |  |
| status | String |  |
| totalSales | Integer |  |
| createdAt | Instant | CreatedDate
    private |

### ECertificates
**Collection:** edu_certificates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String | Indexed
    private |
| userId | String | Indexed
    private |
| creatorId | String | Indexed
    private |
| courseName | String |  |
| recipientName | String |  |
| certificateId | String | Indexed(unique = true)
    private |
| url | String |  |
| templateId | String |  |
| isVerified | Boolean |  |
| shareableLink | String |  |
| issuedAt | Instant | CreatedDate
    private |

### ECouponRedemption
**Collection:** edu_coupon_redemptions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| couponId | String | Indexed
    private |
| userId | String | Indexed
    private |
| transactionId | String |  |
| redeemedAt | Instant | CreatedDate
    private |

### ECoupons
**Collection:** edu_coupons

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| creatorId | String | Indexed
    private |
| code | String | Indexed(unique = true)
    private |
| discountType | String |  |
| discountValue | Double |  |
| maxRedemptions | Integer |  |
| currentRedemptions | Integer |  |
| isActive | Boolean |  |
| expiresAt | Instant |  |
| createdAt | Instant | CreatedDate
    private |

### ECourses
**Collection:** edu_courses

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String | Indexed
    private |
| creatorId | String | Indexed
    private |
| title | String | TextIndexed
    private |
| description | String | TextIndexed
    private |
| shortDescription | String |  |
| thumbnail | String |  |
| previewVideoUrl | String |  |
| type | ECourseType | Indexed
    private |
| contentType | ECourseContentType | Indexed
    private |
| price | Double |  |
| compareAtPrice | Double |  |
| currency | String |  |
| published | Boolean |  |
| isPrivate | Boolean |  |
| level | ECourseLevel | Indexed
    private |
| language | String |  |
| slug | String | Indexed(unique = true)
    private |
| rating | Double | Indexed
    private |
| totalEnrollments | Integer | Indexed
    private |
| totalReviews | Integer |  |
| totalHours | Integer |  |
| totalLessons | Integer |  |
| isFeatured | Boolean |  |
| isTrending | Boolean |  |
| searchRank | Integer | Indexed
    private |
| status | ECourseStatus | Indexed
    private |
| aiGenerated | Boolean |  |
| validationStatus | ECourseValidationStatus |  |
| aiScore | Double |  |
| plagiarismScore | Double |  |
| overallQualityScore | Double |  |
| validationFindings | String |  |
| talnovaVerified | Boolean |  |
| moderationRejectionReason | String |  |
| trustDisclaimer | String | Transient
    private |
| creatorTier | String | Transient
    private |
| trustWarning | String | Transient
    private |
| instructorName | String | Transient
    private |
| publishedAt | Instant |  |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### ECourseSections
**Collection:** edu_course_sections

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String | Indexed
    private |
| order | Integer |  |
| title | String |  |
| description | String |  |
| lessonDetails | List<ELessons> | Transient
    private |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### ECreatorFinanceSettings
**Collection:** edu_creator_finance_settings

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed(unique = true)
    private |
| stripeAccountId | String |  |
| payoutMethods | List<PayoutMethod> |  |
| taxVerificationStatus | String |  |
| profileVerificationStatus | String |  |
| taxForms | List<TaxForm> |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |
| id | String |  |
| type | String |  |
| identifier | String |  |
| accountHolderName | String |  |
| bankName | String |  |
| swiftBic | String |  |
| routingNumber | String |  |
| iban | String |  |
| countryCode | String |  |
| currency | String |  |
| isDefault | boolean |  |
| isVerified | boolean |  |
| id | String |  |
| label | String |  |
| status | String |  |
| url | String |  |
| uploadedAt | Instant |  |

### ECreditLedger
**Collection:** edu_credit_ledger

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| actionType | ECreditLedgerActionType |  |
| amount | Integer |  |
| balanceBefore | Integer |  |
| balanceAfter | Integer |  |
| newBalance | Integer |  |
| metadata | String |  |
| referenceId | String |  |
| referenceType | String |  |
| createdAt | Instant | Indexed
    private |

### EduCourseReviewLog
**Collection:** edu_course_review_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String | Indexed
    private |
| reviewerId | String | Indexed
    private |
| action | String |  |
| reason | String |  |
| createdAt | Instant | CreatedDate
    private |

### EEnrollments
**Collection:** edu_enrollments

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String | Indexed
    private |
| userId | String | Indexed
    private |
| workspaceId | String | Indexed
    private |
| progress | Integer |  |
| completedLessons | Integer |  |
| totalLessons | Integer |  |
| completedSections | Integer |  |
| totalSections | Integer |  |
| completedQuizzes | Integer |  |
| totalQuizzes | Integer |  |
| completedAssignments | Integer |  |
| totalAssignments | Integer |  |
| completedProjects | Integer |  |
| totalProjects | Integer |  |
| completed | Boolean |  |
| lastAccessedLessonId | String |  |
| lastAccessedAt | Instant |  |
| totalWatchTime | Long |  |
| completedAt | Instant |  |
| enrolledAt | Instant | Indexed
    private |
| currentStreak | Integer |  |
| longestStreak | Integer |  |
| lastStreakDate | Instant |  |
| source | String | Indexed
    private |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |
| course | ECourses |  |
| name | String |  |
| email | String |  |
| avatar | String |  |
| coursesCount | Integer |  |

### EFraudFlag
**Collection:** edu_fraud_flags

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| targetUserId | String | Indexed
    private |
| flagType | String |  |
| severity | String |  |
| evidenceBlob | String |  |
| status | String | Indexed
    private |
| reviewerId | String |  |
| resolutionNotes | String |  |
| createdAt | Instant | Indexed
    private |
| resolvedAt | Instant |  |

### EGifts
**Collection:** edu_gifts

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| senderId | String | Indexed
    private |
| recipientEmail | String | Indexed
    private |
| courseId | String | Indexed
    private |
| redeemCode | String | Indexed(unique = true)
    private |
| status | EGiftStatus | Indexed
    private |
| personalMessage | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |
| redeemedAt | Instant |  |
| expiresAt | Instant | Indexed(name = "gifts_expireAt_idx", expireAfter = "0s")
    private |

### EHoldingLedger
**Collection:** edu_holding_ledger

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| beneficiaryId | String | Indexed
    private |
| beneficiaryType | EBeneficiaryType | Indexed
    private |
| transactionId | String | Indexed
    private |
| courseId | String |  |
| amount | Double |  |
| currency | String |  |
| status | EHoldingStatus | Indexed
    private |
| clearanceDate | Instant | Indexed
    private |
| version | Long | Version
    private |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### ELearningPaths
**Collection:** edu_learning_paths

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String | Indexed
    private |
| title | String |  |
| description | String |  |
| createdBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### ELedgerEntry
**Collection:** edu_ledger_entries

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| eventReference | String | Indexed
    private |
| eventType | EventType | Indexed
    private |
| entryType | EntryType |  |
| accountType | AccountType | Indexed
    private |
| accountId | String | Indexed
    private |
| amount | Double |  |
| currency | String |  |
| courseId | String |  |
| bundleId | String |  |
| description | String |  |
| createdAt | Instant | CreatedDate
    private |

### ELessons
**Collection:** edu_lessons

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String | Indexed
    private |
| sectionId | String | Indexed
    private |
| order | Integer |  |
| title | String |  |
| description | String |  |
| contentUrl | String |  |
| textContent | String |  |
| markdownContent | String |  |
| type | ELessonType | Indexed
    private |
| duration | Integer |  |
| isFreePreview | Boolean |  |
| isPublished | Boolean |  |
| isDrmProtected | Boolean |  |
| videoThumbnail | String |  |
| plagiarismScore | Double |  |
| aiScore | Double |  |
| qualityScore | Double |  |
| validationFindings | String |  |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### ENotifications
**Collection:** edu_notifications

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| workspaceId | String | Indexed
    private |
| type | ENotificationType | Indexed
    private |
| title | String |  |
| message | String |  |
| url | String |  |
| icon | String |  |
| actionType | String |  |
| relatedEntityId | String |  |
| entityType | String |  |
| isRead | Boolean |  |
| isArchived | Boolean |  |
| readAt | Instant |  |
| createdAt | Instant | CreatedDate
    private |
| expiresAt | Instant | Indexed(name = "notifications_expireAt_idx", expireAfter = "0s")
    private |

### EPayouts
**Collection:** edu_payouts

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| creatorId | String | Indexed
    private |
| amount | Double |  |
| currency | String |  |
| method | EPayoutMethod | Indexed
    private |
| status | EPayoutStatus | Indexed
    private |
| transactionReference | String |  |
| bankDetails | String |  |
| paypalEmail | String |  |
| platformFee | Double |  |
| createdAt | Instant | CreatedDate
    private |
| paidAt | Instant |  |
| requestedAt | Instant |  |

### EPayoutSchedule
**Collection:** edu_payout_schedules

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| creatorId | String | Indexed(unique = true)
    private |
| frequency | String |  |
| dayTarget | String |  |
| active | Boolean |  |
| lastProcessedAt | Instant |  |
| nextScheduledAt | Instant |  |
| createdAt | Instant | CreatedDate
    private |

### EProfiles
**Collection:** edu_profiles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed(unique = true)
    private |
| firstName | String |  |
| lastName | String |  |
| publicEmail | String |  |
| publicPhone | String |  |
| avatarUrl | String |  |
| bio | String |  |
| socialLinks | ESocialLinksDTO |  |
| industry | String | Indexed
    private |
| company | String |  |
| jobTitle | String |  |
| privacySettings | EPrivacySettingsDTO |  |
| notificationSettings | ENotificationSettingsDTO |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |
| totalStudents | Integer |  |
| totalCourses | Integer |  |
| rating | Double |  |

### EQuizAttempts
**Collection:** edu_quiz_attempts

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| quizId | String | Indexed
    private |
| score | Double |  |
| percentage | Double |  |
| status | EGradingStatus |  |
| isLatest | Boolean |  |
| startedAt | Instant |  |
| completedAt | Instant |  |
| createdAt | Instant | CreatedDate
    private |

### EQuizzes
**Collection:** edu_quizzes

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String | Indexed
    private |
| sectionId | String | Indexed
    private |
| lessonId | String | Indexed
    private |
| title | String |  |
| description | String |  |
| type | EQuizType |  |
| durationLimit | Integer |  |
| passingScore | Double |  |
| questions | List<EQuestionDTO> |  |
| isPublished | Boolean |  |
| allowRetakes | Integer |  |
| createdBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### ERefund
**Collection:** edu_refunds

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| transactionId | String | Indexed
    private |
| stripeCheckoutSessionId | String | Indexed
    private |
| stripeChargeId | String | Indexed
    private |
| stripeRefundId | String | Indexed(unique = true, sparse = true)
    private |
| buyerId | String | Indexed
    private |
| sellerId | String | Indexed
    private |
| courseId | String | Indexed
    private |
| refundAmount | Double |  |
| originalAmount | Double |  |
| currency | String |  |
| type | RefundType |  |
| status | RefundStatus | Indexed
    private |
| reason | String |  |
| enrollmentRevoked | Boolean |  |
| holdingReversed | Boolean |  |
| initiatedBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### EReports
**Collection:** edu_reports

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| reporterId | String | Indexed
    private |
| targetEntityId | String | Indexed
    private |
| entityType | String |  |
| reason | EReportReason | Indexed
    private |
| description | String |  |
| status | EReportStatus | Indexed
    private |
| resolutionNotes | String |  |
| resolvedBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### EReviews
**Collection:** edu_reviews

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String | Indexed
    private |
| userId | String | Indexed
    private |
| rating | Double | Indexed
    private |
| title | String |  |
| content | String |  |
| isVerifiedPurchase | Boolean |  |
| helpfulVotes | Integer |  |
| isReported | Boolean |  |
| isVisible | Boolean |  |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### ESubscriptions
**Collection:** edu_subscriptions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| plan | ESubscriptionPlan | Indexed
    private |
| status | ESubscriptionStatus | Indexed
    private |
| remainingCredits | Integer |  |
| totalCredits | Integer |  |
| price | Double |  |
| currency | String |  |
| billingCycle | String |  |
| autoRenew | Boolean |  |
| paymentGatewayId | String |  |
| stripeCustomerId | String |  |
| stripeSubscriptionId | String |  |
| stripePriceId | String |  |
| commissionRate | Double |  |
| maxCourses | Integer |  |
| startDate | Instant |  |
| endDate | Instant |  |
| trialEndDate | Instant |  |
| cancelledAt | Instant |  |
| lastPaymentAt | Instant |  |
| cancelAtPeriodEnd | Boolean |  |
| lastCreditResetAt | Instant |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### ESystemSettings
**Collection:** edu_system_settings

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| category | String | Indexed(unique = true)
    private |
| settings | Map<String, Object> |  |
| updatedAt | Instant |  |
| updatedBy | String |  |

### ETransactions
**Collection:** edu_transactions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String | Indexed
    private |
| buyerId | String | Indexed
    private |
| sellerId | String | Indexed
    private |
| amount | Double |  |
| currency | String |  |
| platformFee | Double |  |
| commissionRate | Double |  |
| creatorPlanAtPurchase | String |  |
| creatorEarning | Double |  |
| affiliateId | String | Indexed
    private |
| affiliateEarning | Double |  |
| paymentMethod | EPaymentMethod | Indexed
    private |
| paymentStatus | EPaymentStatus | Indexed
    private |
| transactionId | String | Indexed(unique = true)
    private |
| stripeCheckoutSessionId | String | Indexed
    private |
| paymentGateway | String |  |
| paymentGatewayResponse | String |  |
| idempotencyKey | String |  |
| expiresAt | Instant | Indexed
    private |
| appliedCouponCode | String |  |
| discountAmount | Double |  |
| originalAmount | Double |  |
| bundleId | String | Indexed
    private |
| taxAmount | Double |  |
| taxRate | Double |  |
| referrerId | String | Indexed
    private |
| referralCommission | Double |  |
| version | Long | Version
    private |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### ETrustScores
**Collection:** edu_trust_scores

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| creatorId | String | Indexed(unique = true)
    private |
| currentScore | Double |  |
| previousScore | Double |  |
| averageRating | Double |  |
| totalReviews | Integer |  |
| completionRate | Double |  |
| refundRate | Double |  |
| currentTier | String |  |
| previousTier | String |  |
| lastCalculatedAt | Instant |  |
| tierChangedAt | Instant |  |
| validationScore | Double |  |
| ratingScore | Double |  |
| completionScore | Double |  |
| refundHealthScore | Double |  |
| reportHealthScore | Double |  |
| activityScore | Double |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### EUser
**Collection:** edu_user

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| email | String | Indexed(unique = true)
    private |
| phone | String | Indexed(sparse = true)
    private |
| passwordHash | String | JsonIgnore
    private |
| ssoProvider | String |  |
| ssoProviderId | String |  |
| isEmailVerified | Boolean |  |
| isPhoneVerified | Boolean |  |
| displayName | String |  |
| avatarUrl | String |  |
| isActive | Boolean |  |
| isBanned | Boolean |  |
| banReason | String |  |
| lastLoginAt | Instant |  |
| plan | ESubscriptionPlan |  |
| subscriptionStatus | ESubscriptionStatus |  |
| emailVerificationToken | String |  |
| passwordResetToken | String |  |
| passwordResetExpiry | Instant |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |
| isMfaEnabled | Boolean |  |

### EUserPreferences
**Collection:** edu_user_preferences

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed(unique = true)
    private |
| interests | List<String> |  |
| preferredLanguage | String |  |
| dailyLearningGoalMinutes | Integer |  |
| isNotificationsEnabled | Boolean |  |
| preferredDifficulty | String |  |
| careerGoal | String |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### EValidationReports
**Collection:** edu_validation_reports

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String | Indexed
    private |
| userId | String | Indexed
    private |
| reviewerId | String | Indexed
    private |
| aiScore | Double |  |
| status | String | Indexed
    private |
| breakdown | EValidationBreackdownDTO |  |
| feedback | String |  |
| createdBy | String |  |
| createdAt | Instant | CreatedDate
    private |

### EWallet
**Collection:** edu_wallets

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed(unique = true)
    private |
| availableBalance | Double |  |
| pendingBalance | Double |  |
| currency | String |  |
| version | Long | Version
    private |
| updatedAt | Instant |  |

### EWalletTransaction
**Collection:** edu_wallet_transactions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| type | TransactionType |  |
| amount | Double |  |
| currency | String |  |
| status | TransactionStatus |  |
| referenceId | String |  |
| description | String |  |
| createdAt | Instant | CreatedDate
    private |

### EWebhookEvent
**Collection:** edu_webhook_events

| Field | Type | Annotations |
|-------|------|-------------|
| status | Failed events are stored with |  |
| id | String | Id
    private |
| stripeEventId | String | Indexed(unique = true)
    private |
| eventType | String | Indexed
    private |
| status | EventStatus | Indexed
    private |
| payload | String |  |
| signatureHeader | String |  |
| errorMessage | String |  |
| errorStackTrace | String |  |
| retryCount | Integer |  |
| maxRetries | Integer |  |
| nextRetryAt | Instant |  |
| processedAt | Instant | CreatedDate
    private |

### EWorkspaceMembers
**Collection:** edu_workspace_members

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String | Indexed
    private |
| userId | String | Indexed
    private |
| role | ERoles | Indexed
    private |
| status | String |  |
| department | String |  |
| invitedBy | String |  |
| createdBy | String |  |
| joinedAt | Instant | CreatedDate
    private |
| lastActiveAt | Instant |  |

### EWorkspaces
**Collection:** edu_workspaces

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| ownerId | String | Indexed
    private |
| type | EWorkspaceType | Indexed
    private |
| domain | String | Indexed(unique = true)
    private |
| plan | ESubscriptionPlan |  |
| name | String |  |
| description | String |  |
| logoUrl | String |  |
| isActive | Boolean |  |
| maxMembers | Integer |  |
| totalMembers | Integer |  |
| totalCourses | Integer |  |
| totalLearningPaths | Integer |  |
| settings | EWSettingsDTO |  |
| profile | EWProfileDTO |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### AnalyticsData
**Collection:** fin_analytics_data

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| projectId | String |  |
| scenarioId | String |  |
| metric | String |  |
| granularity | String |  |
| period | String |  |
| value | Double |  |
| metadata | Map<String, Object> |  |
| computedAt | Instant |  |

### MetricDefinition
**Collection:** fin_metric_definitions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| name | String |  |
| key | String |  |
| formula | String |  |
| isCustom | boolean |  |
| unit | String |  |

### FinAiTrainingSnapshot
**Collection:** fin_ai_training_snapshots

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| projectId | String |  |
| scenarioId | String |  |
| userId | String |  |
| inputSnapshot | String |  |
| outputSnapshot | String |  |
| changedFields | List<String> |  |
| tags | List<String> |  |
| createdAt | Instant |  |

### FinAssumption
**Collection:** fin_assumptions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| projectId | String |  |
| key | String |  |
| value | String |  |
| unit | String |  |
| category | String |  |
| version | Integer | Version
    private |
| createdAt | Instant |  |

### FinAuditLog
**Collection:** fin_audit_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| projectId | String |  |
| userId | String |  |
| action | String |  |
| entityType | String |  |
| entityId | String |  |
| previousValue | Object |  |
| newValue | Object |  |
| timestamp | Instant |  |
| ipAddress | String |  |

### FinBudget
**Collection:** fin_budgets

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| projectId | String |  |
| category | String |  |
| type | String |  |
| monthlyAllocations | Map<String, Double> |  |
| formula | String |  |
| version | Integer | Version
    private |
| createdAt | Instant |  |

### FinFinancialSnapshot
**Collection:** fin_financial_snapshots

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| projectId | String |  |
| scenarioId | String |  |
| month | String |  |
| revenue | Double |  |
| cost | Double |  |
| profit | Double |  |
| breakdown | Map<String, Double> |  |
| computedAt | Instant |  |

### FinPricingModel
**Collection:** fin_pricing_models

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| projectId | String |  |
| tier | String |  |
| price | Double |  |
| costPerUser | Double |  |
| commissionPercent | Double |  |
| margin | Double |  |
| version | Integer | Version
    private |
| effectiveDate | Instant |  |

### FinProject
**Collection:** fin_projects

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| name | String |  |
| description | String |  |
| type | String |  |
| status | String |  |
| ownerId | String |  |
| teamMemberIds | List<String> |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### FinProjectMember
**Collection:** fin_project_members

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| projectId | String |  |
| userId | String |  |
| role | ProjectRole |  |
| joinedAt | Instant |  |

### FinSalesPlan
**Collection:** fin_sales_plans

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| projectId | String |  |
| month | String |  |
| userCounts | Map<String, Integer> |  |
| growthRate | Double |  |
| version | Integer | Version
    private |
| createdAt | Instant |  |

### FinScenario
**Collection:** fin_scenarios

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| projectId | String |  |
| name | String |  |
| baseVersion | Integer |  |
| overrides | List<AssumptionOverride> |  |
| createdAt | Instant |  |
| key | String |  |
| value | String |  |

### FinWorkspace
**Collection:** fin_workspaces

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| slug | String |  |
| ownerId | String |  |
| memberIds | List<String> |  |
| subscriptionType | String |  |
| isActive | Boolean |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### Scenario
**Collection:** scenarios

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| organizationId | String |  |
| projectId | String |  |
| name | String |  |
| parentScenarioId | String |  |
| baseVersionId | String |  |
| createdBy | String |  |
| createdAt | Instant |  |
| tags | List<String> |  |
| notes | String |  |

### ScenarioOverride
**Collection:** fin_scenario_overrides

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| scenarioId | String |  |
| path | String |  |
| month | String |  |
| value | Object |  |
| type | OverrideOperation |  |
| versionId | String |  |
| createdBy | String |  |
| createdAt | Instant |  |

### ApplicantModel
**Collection:** applicants

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| jobId | String |  |
| companyId | String |  |
| candidateId | String |  |
| candidateName | String |  |
| candidateEmail | String |  |
| candidatePhone | String |  |
| status | String |  |
| appliedAt | Instant |  |
| updatedAt | Instant |  |
| resumeUrl | String |  |
| coverLetter | String |  |
| interviewNotes | String |  |
| rejectionReason | String |  |

### JobPostModel
**Collection:** job_posts

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| companyName | String |  |
| companyLogo | String |  |
| title | String |  |
| description | String |  |
| responsibilities | String |  |
| requirements | String |  |
| skills | List<String> |  |
| location | String |  |
| type | String |  |
| experienceLevel | String |  |
| minSalary | Double |  |
| maxSalary | Double |  |
| currency | String |  |
| status | String |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |
| expiryDate | Instant |  |
| applicationsCount | int |  |
| viewsCount | int |  |

### LAutomation
**Collection:** lead_automations

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String |  |
| name | String |  |
| active | boolean |  |
| triggerType | String |  |
| triggerScoreThreshold | int |  |
| triggerKeywords | List<String> |  |
| actionType | String |  |
| actionTemplateId | String |  |
| requiresHumanApproval | boolean |  |
| createdAt | LocalDateTime |  |
| updatedAt | LocalDateTime |  |

### LLeadAutomation
**Collection:** leads_automations

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String |  |
| name | String |  |
| active | boolean |  |
| triggerType | String |  |
| triggerScoreThreshold | Double |  |
| triggerKeywords | List<String> |  |
| triggerStatus | String |  |
| actionType | String |  |
| actionWebhookUrl | String |  |
| requiresHumanApproval | boolean |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### LCampaign
**Collection:** leads_campaigns

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String |  |
| name | String |  |
| platform | String |  |
| status | String |  |
| template | String |  |
| sourceId | String |  |
| metrics | Map<String, Integer> |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### LLead
**Collection:** leads_leads

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String |  |
| name | String |  |
| platform | String |  |
| status | String |  |
| tags | List<String> |  |
| score | Double |  |
| sourceSignalId | String |  |
| notes | String |  |
| timeline | List<LTimelineEvent> |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### LLeadCandidate
**Collection:** leads_candidates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| workspaceId | String |  |
| sourceId | String |  |
| rawSignalId | String |  |
| summary | String |  |
| intent | String |  |
| leadScore | Double |  |
| tags | List<String> |  |
| pipelineStage | String |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### LLeadSource
**Collection:** leads_sources

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String |  |
| platform | String |  |
| name | String |  |
| config | Map<String, Object> |  |
| active | boolean |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### LLeadWorkspace
**Collection:** leads_workspaces

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| ownerId | String |  |
| memberIds | List<String> |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### LNotification
**Collection:** lead_notifications

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String |  |
| userId | String |  |
| title | String |  |
| message | String |  |
| type | String |  |
| read | boolean |  |
| createdAt | LocalDateTime |  |
| link | String |  |

### LRawSignal
**Collection:** leads_raw_signals

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| sourceId | String |  |
| workspaceId | String |  |
| platformId | String |  |
| content | String |  |
| author | String |  |
| url | String |  |
| metadata | Map<String, Object> |  |
| status | String |  |
| intent | String |  |
| score | Double |  |
| tags | List<String> |  |
| capturedAt | Instant |  |

### LTask
**Collection:** lead_background_tasks

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String |  |
| userId | String |  |
| type | String |  |
| status | String |  |
| progress | int |  |
| resultUrl | String |  |
| errorMessage | String |  |
| metadata | Map<String, Object> |  |
| createdAt | LocalDateTime |  |
| updatedAt | LocalDateTime |  |

### LTemplate
**Collection:** lead_templates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| workspaceId | String |  |
| name | String |  |
| content | String |  |
| platform | String |  |
| category | String |  |
| usageCount | int |  |
| createdAt | LocalDateTime |  |
| updatedAt | LocalDateTime |  |

### AICacheEntry
**Collection:** lp_ai_cache

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| cacheKey | String | Indexed(unique = true)
    private |
| responseJson | String |  |
| provider | String |  |
| promptHash | String |  |
| createdAt | Instant | Indexed(expireAfter = "24h")
    private |

### UserCredits
**Collection:** lp_user_credits

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed(unique = true)
    private |
| tier | SubscriptionTier |  |
| creditsAvailable | int |  |
| lastRefreshedAt | Instant |  |

### Goal
**Collection:** lp_goals

| Field | Type | Annotations |
|-------|------|-------------|
| goalId | String | Id
    private |
| userId | String | Indexed
    private |
| title | String |  |
| description | String |  |
| deadline | Instant |  |
| difficulty | String |  |
| status | String |  |
| type | GoalType |  |
| timeline | GoalTimeline |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### JournalEntry
**Collection:** lp_journal_entries

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| date | LocalDate |  |
| reflection | String |  |
| aiInsight | String |  |
| createdAt | Instant |  |

### MoodEntry
**Collection:** lp_mood_entries

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| date | LocalDate |  |
| score | int |  |
| label | String |  |
| createdAt | Instant |  |

### WeeklyMoodSummary
**Collection:** lp_weekly_mood_summaries

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String |  |
| weekStartDate | LocalDate |  |
| weekEndDate | LocalDate |  |
| averageScore | double |  |
| entryCount | int |  |
| dominantMood | String |  |
| trendDirection | String |  |
| computedAt | Instant |  |

### LPNotification
**Collection:** lp_notifications

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| title | String |  |
| message | String |  |
| type | String |  |
| isRead | boolean |  |
| createdAt | Instant |  |

### DailySchedule
**Collection:** lp_daily_schedules

| Field | Type | Annotations |
|-------|------|-------------|
| scheduleId | String | Id
    private |
| planId | String | Indexed
    private |
| userId | String | Indexed
    private |
| scheduleDate | LocalDate |  |
| tasks | List<ScheduleTask> |  |
| isCompleted | boolean | JsonProperty("isCompleted")
    private |
| taskId | String |  |
| title | String |  |
| estimatedTime | String |  |
| category | String |  |
| startTime | String |  |
| endTime | String |  |
| isCompleted | boolean | JsonProperty("isCompleted")
        private |
| completedAt | String |  |
| priority | String |  |
| notes | String |  |

### StudyPlan
**Collection:** lp_study_plans

| Field | Type | Annotations |
|-------|------|-------------|
| planId | String | Id
    private |
| goalId | String | Indexed
    private |
| userId | String | Indexed
    private |
| status | String | Indexed
    private |
| roadmap | List<PlanResponse.RoadmapItem> |  |
| weeklyPlans | List<PlanResponse.WeeklyPlan> |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |
| progressPercentage | double |  |
| reachedMilestones | List<Integer> |  |

### User
**Collection:** lp_users

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| email | String |  |
| passwordHash | String |  |
| name | String |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### UserPreferences
**Collection:** lp_preferences

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String |  |
| notificationsEnabled | boolean |  |
| theme | String |  |
| colorPalette | String |  |
| fontFamily | String |  |
| uiDensity | String |  |
| journalingStyle | String |  |
| plannerLayoutStyle | String |  |
| workHoursStart | String |  |
| workHoursEnd | String |  |
| preferredStudyTime | String |  |
| productivityCycle | String |  |
| breakFrequency | int |  |
| studySessionLength | int |  |
| careerType | String |  |
| personalityProfile | Map<String, Object> |  |

### UserProfile
**Collection:** lp_profiles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| lifestyleData | String |  |
| studyPreferences | List<String> |  |
| focusTime | String |  |
| hobbies | List<String> |  |
| stressLevel | String |  |
| age | Integer |  |
| gender | String |  |
| educationLevel | String |  |
| careerType | String |  |
| personalityQuizResults | Map<String, Object> |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### ChatRoom
**Collection:** chat_rooms

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| type | RoomType |  |
| name | String |  |
| participants | List<String> |  |
| communityId | String |  |
| createdAt | LocalDateTime |  |
| pinnedBy | List<String> |  |
| archivedBy | List<String> |  |
| favoritedBy | List<String> |  |

### Message
**Collection:** messages

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| roomId | String | Indexed
    private |
| senderId | String |  |
| content | String |  |
| messageType | MessageType |  |
| createdAt | LocalDateTime |  |
| readByUsers | Map<String, LocalDateTime> |  |
| isEdited | boolean |  |
| updatedAt | LocalDateTime |  |
| isDeleted | boolean |  |
| deletedForUsers | List<String> |  |
| reactions | Map<String, List<String>> |  |
| metadata | Map<String, Object> |  |
| isForwarded | boolean |  |
| forwardedFromId | String |  |
| isPinned | boolean |  |
| expiresAt | Instant | Indexed(expireAfter = "PT0S")
    private |
| isEncrypted | boolean |  |
| replyToId | String |  |

### UserPresence
**Collection:** 

| Field | Type | Annotations |
|-------|------|-------------|
| userId | String | Id
    private |
| status | PresenceStatus |  |
| lastSeen | LocalDateTime |  |

### BillingAddressModel
**Collection:** portal_billing_address

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| street | String |  |
| city | String |  |
| state | String |  |
| postal_code | String |  |
| country | String |  |

### BillingHistoryModel
**Collection:** portal_billing_history

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| userId | String |  |
| amount | String |  |
| date | String |  |
| invoice_id | String |  |
| sessionId | String |  |
| status | String |  |

### InvoicesModel
**Collection:** portal_invoices

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| invoiceId | String |  |
| subscriptionId | String |  |
| amountDue | String |  |
| billingDate | Date |  |
| dueDate | Date |  |
| periodStart | Date |  |
| periodEnd | Date |  |
| invoice_pdf | String |  |
| hosted_invoice_url | String |  |
| status | String |  |
| sessionId | String |  |

### PaymentMethodsModel
**Collection:** portal_payment_methods

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| userId | String |  |
| type | String |  |
| last_four | String |  |
| expiry_date | String |  |
| sessionId | String |  |
| is_default | String |  |

### PaymentSubscriptionsModel
**Collection:** portal_subscriptions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| subscriptionId | String |  |
| plan_name | String |  |
| start_date | String |  |
| end_date | String |  |
| cost | String |  |
| billing_cycle | String |  |
| is_active | boolean |  |

### PrePaymentModel
**Collection:** portal_pre_payment_data

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| firstname | String |  |
| lastname | String |  |
| country | String |  |
| address | String |  |
| phone | String |  |
| subscriptionId | String |  |
| paymentMethodId | String |  |
| billingAddressId | String |  |
| invoiceId | String |  |
| slipUrl | String |  |
| payType | String |  |
| status | String |  |

### RecordedCoursePayment
**Collection:** recorded_course_payments

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| courseId | String |  |
| courseName | String |  |
| learnerId | String |  |
| trainerId | String |  |
| companyId | String |  |
| grossAmount | BigDecimal |  |
| netAmount | BigDecimal |  |
| currency | String |  |
| trainerAmount | BigDecimal |  |
| platformAmount | BigDecimal |  |
| splitType | String |  |
| paymentMethod | String |  |
| paymentStatus | String |  |
| transactionId | String |  |
| createdAt | String |  |
| updatedAt | String |  |

### UsageDataModel
**Collection:** portal_usage_data

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| job_posts_allowed | String |  |
| job_posts_used | String |  |
| applicant_views_allowed | String |  |
| applicant_views_used | String |  |
| overage_charges | String |  |
| users | Integer |  |
| storage | Integer |  |
| bandwidth | Integer |  |

### AmbassadorTaskProgressModel
**Collection:** ambassador_task_progress

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| ambassadorId | String | Indexed
    private |
| taskId | String |  |
| taskType | String |  |
| progressValue | int |  |
| completed | boolean |  |
| startedAt | Instant |  |
| completedAt | Instant |  |
| lastResetAt | Instant |  |
| rewardStatus | String |  |
| rewarded | boolean |  |
| rewardedAt | Instant |  |

### BadgeDefinition
**Collection:** 

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| title | String |  |
| description | String |  |
| level | String |  |
| svgUrl | String |  |
| visible | boolean |  |
| createdAt | Instant |  |
| tags | List<String> |  |

### CourseCertificateModel
**Collection:** course_certificates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| courseId | String |  |
| certificateId | String |  |
| type | String |  |
| url | String |  |
| issuedBy | String |  |
| issuedDate | String |  |
| delivered | boolean |  |
| fileName | String |  |
| description | String |  |

### CourseCouponsModel
**Collection:** course_coupons

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| stripeCouponId | String |  |
| userId | String |  |
| publicity | boolean |  |
| false | public, |  |
| unlockedBy | private String |  |
| redeemedBy | String |  |
| code | String |  |
| token | String |  |
| discount | String |  |
| discountType | String |  |
| validityInMillis | long |  |
| createdAt | Instant |  |
| earnedAt | Instant |  |
| unlockedAt | Instant |  |
| redeemedAt | Instant |  |
| expiresAt | Instant |  |
| status | Status |  |
| level | String |  |
| applicableCourseIds | List<String> |  |
| empty | Null or |  |
| applicableForInstallment | boolean |  |
| redeemedForCourseId | String |  |
| redeemedForInstallmentId | String |  |
| createdBy | String |  |
| taskId | String |  |
| campaignId | String |  |
| tag | String |  |
| type | String |  |
| activationType | String |  |
| expiredAt | Instant |  |
| maxRedemptions | int |  |
| currentRedemptions | int |  |

### CourseReminderLog
**Collection:** course_reminder_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| moduleId | String |  |
| courseId | String |  |
| reminderType | String |  |
| sentTime | Instant |  |

### EmpCoursesModel
**Collection:** portal_emp_courses

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| employeeName | String |  |
| email | String |  |
| phone | String |  |
| timezone | String |  |
| courses | List<CourseEnrollment> | Field("courses")
    private |
| recordedCourses | List<RecordedCourseEnrollment> | Field("recordedCourses")
    private |

### GamificationTaskModel
**Collection:** gamification_tasks

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| title | String |  |
| description | String |  |
| type | String |  |
| level | String |  |
| null | BRONZE, GOLD, PLATINUM, or |  |
| targetValue | int |  |
| rewardType | String |  |
| rewardId | String |  |
| rewardMetadata | Map<String, Object> |  |
| recurring | boolean |  |
| frequencyInDays | int |  |
| createdAt | Instant |  |
| priority | int |  |
| higher | lowest, |  |
| groupKey | String |  |
| points | int |  |

### QuizAttempt
**Collection:** quiz_attempts

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| courseId | String |  |
| moduleId | String |  |
| quizId | String |  |
| attemptNumber | int |  |
| answers | List<QuestionAnswer> |  |
| score | double |  |
| correctCount | int |  |
| totalQuestions | int |  |
| submittedAt | String |  |

### SwagItem
**Collection:** 

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| title | String |  |
| description | String |  |
| imageUrl | String |  |
| inventory | int |  |
| sizeOptions | List<String> |  |
| enabled | boolean |  |
| createdAt | Instant |  |

### InterviewQuestionModel
**Collection:** portal_interviewQuestion

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| category | String |  |
| questions | List<Question> |  |
| id | String |  |
| question | String |  |
| overview | String |  |
| viewCount | int |  |
| answers | List<Answer> |  |
| id | String |  |
| by | String |  |
| position | String |  |
| date | String |  |
| answer | String |  |
| video | String |  |
| viewCount | int |  |

### JobApplyModel
**Collection:** portal_job_applicants

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| companyId | String |  |
| jobId | String |  |
| applicants | List<JobApplicantDTO> | Field("applicants") |
| viewers | List<JobViewerDTO> | Field("viewers") |

### PreOrderModel
**Collection:** portal_preorder

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| email | String |  |
| product | String |  |
| date | String |  |

### Referral
**Collection:** referrals

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| referrerId | String | Indexed
    private |
| referredUserId | String | Indexed
    private |
| type | ReferralType |  |
| status | ReferralStatus |  |
| rewardIssued | boolean |  |
| createdAt | Instant |  |
| completedAt | Instant |  |

### ReferralCode
**Collection:** referral_codes

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| code | String | Indexed(unique = true)
    private |
| userId | String | Indexed
    private |
| createdAt | Instant |  |

### ReferralCommission
**Collection:** referral_commissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| referrerId | String | Indexed
    private |
| referredCreatorId | String | Indexed
    private |
| percentage | double |  |
| expiryDate | Instant |  |
| createdAt | Instant |  |

### ReputationEvent
**Collection:** reputation_events

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| sourceType | ReputationSourceType |  |
| sourceId | String |  |
| delta | int |  |
| createdAt | LocalDateTime |  |

### UserBadge
**Collection:** user_badges

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed
    private |
| badgeType | BadgeType |  |
| awardedAt | LocalDateTime |  |

### UserReputation
**Collection:** user_reputation

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed(unique = true)
    private |
| totalScore | long |  |
| articleScore | long |  |
| communityScore | long |  |
| announcementScore | long |  |
| lastUpdated | LocalDateTime |  |

### ResumeModel
**Collection:** resumes

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String | Indexed
    private |
| title | String |  |
| templateId | String |  |
| personalInfo | PersonalInfo |  |
| workExperience | List<WorkExperience> |  |
| education | List<Education> |  |
| skills | List<Skill> |  |
| projects | List<Project> |  |
| certificates | List<Certificate> |  |
| customSections | List<CustomSection> |  |
| sectionOrder | List<String> |  |
| settings | ResumeSettings |  |
| completionScore | int |  |
| atsScore | int |  |
| aiUsageCount | int |  |
| deleted | boolean |  |
| platform | String |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |
| fullName | String |  |
| title | String |  |
| phone | String |  |
| email | String |  |
| location | String |  |
| linkedin | String |  |
| portfolio | String |  |
| summary | String |  |
| id | String |  |
| jobTitle | String |  |
| company | String |  |
| location | String |  |
| startDate | String |  |
| endDate | String |  |
| current | boolean |  |
| responsibilities | List<String> |  |
| achievements | List<String> |  |
| id | String |  |
| degree | String |  |
| institution | String |  |
| location | String |  |
| graduationDate | String |  |
| gpa | String |  |
| description | String |  |
| id | String |  |
| name | String |  |
| level | String |  |
| category | String |  |
| id | String |  |
| title | String |  |
| description | String |  |
| technologies | List<String> |  |
| githubLink | String |  |
| liveLink | String |  |
| achievements | List<String> |  |
| id | String |  |
| name | String |  |
| organization | String |  |
| issueDate | String |  |
| credentialLink | String |  |
| id | String |  |
| title | String |  |
| items | List<CustomSectionItem> |  |
| id | String |  |
| content | String |  |
| primaryColor | String |  |
| fontFamily | String |  |
| fontSize | String |  |
| spacing | String |  |
| showPhoto | boolean |  |
| showIcons | boolean |  |
| showSkillLevels | boolean |  |

### SoftwareAppModel
**Collection:** portal_software_apps

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| description | String |  |
| icon | String |  |
| category | String |  |
| developer | String |  |
| version | String |  |
| status | String |  |
| purchaseDate | Instant |  |
| expiryDate | Instant |  |
| companyId | String |  |
| planName | String |  |
| cost | Double |  |
| billingCycle | String |  |
| permissions | List<String> |  |
| accessUrl | String |  |
| isGlobal | boolean |  |

### FeatureFlag
**Collection:** feature_flags

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| plan | ESubscriptionPlan | Indexed
    private |
| featureKey | String | Indexed
    private |
| enabled | boolean |  |

### Subscription
**Collection:** subscriptions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String | Indexed(unique = true)
    private |
| plan | ESubscriptionPlan | Indexed
    private |
| status | ESubscriptionStatus | Indexed
    private |
| startDate | Instant |  |
| endDate | Instant |  |
| stripeCustomerId | String | Indexed(sparse = true)
    private |
| stripeSubscriptionId | String | Indexed(sparse = true)
    private |
| stripePriceId | String |  |
| billingCycle | String |  |
| autoRenew | Boolean |  |
| cancelAtPeriodEnd | Boolean |  |
| trialEndDate | Instant |  |
| cancelledAt | Instant |  |
| lastPaymentAt | Instant |  |
| lastCreditResetAt | Instant |  |
| createdAt | Instant | CreatedDate
    private |
| updatedAt | Instant | LastModifiedDate
    private |

### SupportRequestModel
**Collection:** support_requests

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| email | String |  |
| service | String |  |
| message | String |  |
| status | String |  |
| createdAt | Instant | CreatedDate
    private |

### TrackingEvent
**Collection:** events

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| trackingId | String |  |
| eventType | String |  |
| url | String |  |
| referrer | String |  |
| sessionId | String | Indexed
    private |
| userId | String |  |
| timestamp | Instant | Indexed
    private |
| screenResolution | String |  |
| browser | String |  |
| language | String |  |
| viewportWidth | Integer |  |
| viewportHeight | Integer |  |
| ip | String |  |
| country | String |  |
| region | String |  |
| city | String |  |
| countryCode | String |  |
| isp | String |  |
| proxy | Boolean |  |
| suspectedVpn | Boolean |  |
| suspectedBot | Boolean |  |
| pageTitle | String |  |
| pagePath | String |  |
| elementId | String |  |
| elementText | String |  |
| elementType | String |  |
| elementClass | String |  |
| elementAriaLabel | String |  |
| elementRouterLink | String |  |
| clickX | Integer |  |
| clickY | Integer |  |
| scrollPercent | Integer |  |
| scrollDepth | Integer |  |
| durationMs | Long |  |
| activeTimeMs | Long |  |
| domLoadTime | Long |  |
| fullLoadTime | Long |  |
| ttfb | Long |  |
| dnsTime | Long |  |
| tcpTime | Long |  |
| downloadTime | Long |  |
| lcpValue | Long |  |
| fidValue | Long |  |
| downlink | Integer |  |
| effectiveType | String |  |
| rtt | Integer |  |
| saveData | Boolean |  |
| errorMessage | String |  |
| errorSource | String |  |
| errorLine | Integer |  |
| errorColumn | Integer |  |
| errorStack | String |  |
| rejectionReason | String |  |
| FEATURES | NEW v2.0 |  |
| experimentId | String |  |
| variant | String |  |
| conversionType | String |  |
| conversionValue | Double |  |
| funnelId | String |  |
| stepName | String |  |
| stepIndex | Integer |  |
| totalSteps | Integer |  |
| formId | String |  |
| fieldName | String |  |
| fieldType | String |  |
| fieldsInteracted | Integer |  |
| totalFields | Integer |  |
| timeSpentMs | Long |  |
| validationMessage | String |  |
| clickCount | Integer |  |
| areaX | Integer |  |
| areaY | Integer |  |
| heatmapPath | String |  |
| attentionDurationMs | Long |  |
| viewportPercentage | Integer |  |
| timezone | String |  |
| timezoneOffset | Integer |  |
| customData | Map<String, Object> |  |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s")
    private |

### EmpCertificatesModel
**Collection:** portal_emp_certificates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| certificates | List<EmpCertificatesDTO> | Field("certificates")
    private |

### EmpContactModel
**Collection:** portal_emp_contact

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| contact | List<EmpContactDTO> | Field("contact")
    private |
| socialLinks | List<SocialLinksDTO> | Field("social_links")
    private |
| publicity | boolean |  |

### EmpEducationModel
**Collection:** portal_emp_education

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| education | List<EmpEducationDTO> | Field("education")
    private |

### EmpExperiencesModel
**Collection:** portal_emp_experiences

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| experiences | List<EmpExperiencesDTO> | Field("experiences")
    private |

### EmpFollowersModel
**Collection:** portal_emp_followers

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| followers | List<EmpFollowersDTO> | Field("followers")
    private |

### EmpFollowingModel
**Collection:** portal_emp_following

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| followings | List<EmpFollowingDTO> | Field("followings")
    private |

### EmployeeModel
**Collection:** portal_employees

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| firstname | String |  |
| lastname | String |  |
| occupation | String | Indexed(name = "occupation_1")
    private |
| image | String |  |
| coverImage | String |  |
| dob | String |  |
| email | String | Indexed(unique = true, name = "email_1")
    private |
| resume | String |  |
| intro | String |  |
| skills | String |  |
| experiences | String |  |
| education | String |  |
| projects | String |  |
| certificates | String |  |
| contactInfo | String |  |
| courses | String |  |
| followings | String |  |
| followers | String |  |
| savedJobs | List<FavJobDTO> | Field("savedJobs")
    private |
| savedPosts | List<String> |  |
| accountNotifications | Object |  |
| marketingNotifications | Object |  |
| profileCompleted | Object |  |
| profileStatus | String |  |
| platformRole | PlatformRole |  |
| companyId | String |  |
| expectedSalaryRange | String |  |
| currentExperience | String |  |
| keywords | String |  |
| messagingPublicKey | String |  |

### EmpProjectsModel
**Collection:** portal_emp_projects

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| projects | List<EmpProjectsDTO> | Field("projects")
    private |

### EmpSkillsModel
**Collection:** portal_emp_skills

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String |  |
| skills | List<EmpSkillsDTO> | Field("skills")
    private |

### TrainerProfile
**Collection:** trainer_profiles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| employeeId | String | Indexed(unique = true)
    private |
| headline | String |  |
| bio | String |  |
| specialties | List<String> |  |
| languages | List<String> |  |
| hourlyRate | String |  |
| availability | String |  |
| certifications | List<String> |  |
| rating | Double |  |
| totalReviews | Integer |  |
| trainerVideoIntro | String |  |
| website | String |  |
| linkedIn | String |  |
| youtube | String |  |
| publicProfile | boolean |  |

### WorkspaceModel
**Collection:** workspaces

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| slug | String |  |
| ownerId | String |  |
| memberIds | List<String> |  |
| createdAt | LocalDateTime |  |
| updatedAt | LocalDateTime |  |
| subscriptionType | String |  |

### UserActivity
**Collection:** portal_user_activity

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| userId | String |  |
| encryptedIpAddress | String |  |
| timestamp | LocalDateTime |  |
| endpointAccessed | String |  |
| lastActive | LocalDateTime |  |
| sessionStart | LocalDateTime |  |
| sessionEnd | LocalDateTime |  |
| country | String |  |
| suspectedVpn | boolean |  |
| suspectedBot | boolean |  |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s")
    private |

### WhitelistDomains
**Collection:** portal_whitelist_domains

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| domain | String |  |
| active | boolean |  |
| requestBy | String |  |

### ContactSubmission
**Collection:** contact_form_submissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| email | String |  |
| subject | String |  |
| message | String |  |
| createdAt | LocalDateTime |  |

### CtaLeadSubmission
**Collection:** cta_lead_submissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| name | String |  |
| email | String |  |
| serviceType | String |  |
| ctaType | String |  |
| focusArea | String |  |
| message | String |  |
| createdAt | LocalDateTime |  |

### NewsLatterModel
**Collection:** portal_news_letter

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| email | String |  |

### TokenModel
**Collection:** portal_tokens

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id
    private |
| token | String |  |
| username | String |  |
| expiration | Date |  |
| used | boolean |  |

