# Models Analysis

| Model Name | Collection | Fields Count | Relationships |
|------------|------------|--------------|---------------|
| AIQuota | ai_quotas | 5 | UNKNOWN(user), EMBEDDED(Instant) |
| AIUsage | ai_usage_logs | 5 | UNKNOWN(user), EMBEDDED(AIUsageType), EMBEDDED(Instant) |
| CreditRecord | course_ai_credit_record | 3 | EMBEDDED(LocalDate) |
| AmbassadorLeaderboardModel | ambassador_leaderboard | 10 | UNKNOWN(ambassador), EMBEDDED(Instant), EMBEDDED(Instant) |
| AmbassadorPointAudit | ambassador_point_audit | 6 | UNKNOWN(ambassador), EMBEDDED(Instant) |
| AmbassadorProfileModel | ambassador_profiles | 28 | EMBEDDED(Instant), EMBEDDED(AmbassadorLifecycle), UNKNOWN(employee), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| AmbassadorRewardModel | ambassador_rewards | 7 | UNKNOWN(ambassador), UNKNOWN(task), EMBEDDED(Instant), EMBEDDED(Instant) |
| AmbassadorSessionModel | ambassador_sessions | 8 | UNKNOWN(ambassador), EMBEDDED(Instant) |
| AmbReferralModel | ambassador_referrals | 9 | UNKNOWN(ambassador), UNKNOWN(referredUser), EMBEDDED(Instant), UNKNOWN(course), EMBEDDED(Instant) |
| BadgeModel | ambassador_badges | 7 | UNKNOWN(ambassador), UNKNOWN(task), UNKNOWN(badge), EMBEDDED(Instant) |
| SwagModel |  | 10 | UNKNOWN(ambassador), UNKNOWN(task), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| Announcement | announcements | 21 | EMBEDDED(AnnouncementStatus), EMBEDDED(AnnouncementType), EMBEDDED(AnnouncementVisibility), EMBEDDED(AnnouncementPriority) |
| Article | articles | 20 | UNKNOWN(author), EMBEDDED(ArticleStatus) |
| ArticleEvaluationLog | article_evaluations | 9 | UNKNOWN(article), EMBEDDED(ArticleEvaluationDTO) |
| Tag | article_tags | 3 |  |
| AsyncUpdateAuditLog | async_update_audit_log | 11 | UNKNOWN(course), UNKNOWN(batch), UNKNOWN(employee), EMBEDDED(Instant) |
| ClientActAuditLog | client_act_audit_log | 8 | UNKNOWN(user), UNKNOWN(session), EMBEDDED(Instant) |
| CourseReminderAuditLog | course_reminder_audit_logs | 15 | UNKNOWN(employee), UNKNOWN(course), UNKNOWN(module), EMBEDDED(Instant), EMBEDDED(Instant) |
| LeadOSAuditLog | leados_audit_logs | 8 | UNKNOWN(workspace), UNKNOWN(user), UNKNOWN(entity) |
| SchedulerLogModel |  | 6 | EMBEDDED(Instant), EMBEDDED(Instant) |
| StripeAuditLog | stripe_audit_logs | 11 | UNKNOWN(event), UNKNOWN(session), UNKNOWN(customer), UNKNOWN(subscription), UNKNOWN(paymentIntent), EMBEDDED(Instant) |
| TaskRewardAuditModel |  | 10 | UNKNOWN(ambassador), UNKNOWN(task), UNKNOWN(reward), EMBEDDED(Instant), EMBEDDED(Instant) |
| CredentialsModel | portal_credentials | 22 | UNKNOWN(employee), UNKNOWN(company), UNKNOWN(referrer), UNKNOWN(ambassador), UNKNOWN(activeWorkspace) |
| PasswordResetTokenModel | portal_password_reset_tokens | 4 | UNKNOWN(user) |
| PermissionModel | permissions | 6 | EMBEDDED(Instant), EMBEDDED(Instant) |
| RoleModel | roles | 5 |  |
| FeatureModel | portal_feature-requests | 4 |  |
| IssueModel | portal_report-issues | 4 |  |
| Login | portal_logins | 4 | UNKNOWN(user), EMBEDDED(LoginMetaDTO) |
| SystemNotificationsModel | portal_systemNotifications | 6 |  |
| Activity | community_activities | 5 | UNKNOWN(user), UNKNOWN(target) |
| Comment | comments | 12 | UNKNOWN(post), UNKNOWN(parent), UNKNOWN(author), EMBEDDED(Reaction), EMBEDDED(Comment) |
| Community | communities | 15 | UNKNOWN(creator), EMBEDDED(CommunityPrivacy) |
| CommunityMember | community_members | 8 | UNKNOWN(community), UNKNOWN(user), EMBEDDED(MemberRole) |
| Notification | community_notifications | 7 | UNKNOWN(recipient), UNKNOWN(sender), EMBEDDED(NotificationType), UNKNOWN(target) |
| Post | posts | 28 | UNKNOWN(author), UNKNOWN(community), UNKNOWN(quotedPost), EMBEDDED(PostContent), EMBEDDED(PostMetrics), EMBEDDED(Reaction), EMBEDDED(LinkPreview) |
| Report | community_reports | 7 | UNKNOWN(reporter), UNKNOWN(target), EMBEDDED(ReportTargetType), EMBEDDED(ReportStatus) |
| CourseBatchModel | course_batches | 26 | UNKNOWN(course), EMBEDDED(InstallmentDTO), EMBEDDED(ModuleDTO), EMBEDDED(MaterialsDTO), EMBEDDED(QuizDTO) |
| CourseModel | job_hunter_courses | 36 | UNKNOWN(company), UNKNOWN(trainer), EMBEDDED(InstallmentDTO), EMBEDDED(ModuleDTO), EMBEDDED(MaterialsDTO), EMBEDDED(QuizDTO), EMBEDDED(CourseMissedNotify) |
| RecordedCourseModel | recorded_courses | 25 | EMBEDDED(BigDecimal), EMBEDDED(RecModuleDTO), EMBEDDED(RecordedCourseReviewDTO), UNKNOWN(company), UNKNOWN(trainer), EMBEDDED(BigDecimal), EMBEDDED(BigDecimal), EMBEDDED(InstallmentDTO) |
| TrainCompanyModel | train_company | 6 | EMBEDDED(TeamMemberDTO) |
| TrainersModel | trainers | 12 |  |
| CmpPostedJobsModel | portal_cmp_posted_jobs | 6 | UNKNOWN(company), EMBEDDED(PostedJobsDTO) |
| CmpSocialModel | portal_cmp_socials | 3 | UNKNOWN(company), EMBEDDED(SocialLinksDTO) |
| CompanyModel | portal_companies | 32 |  |
| StandaloneFileModel | standalone_files | 12 | UNKNOWN(owner), UNKNOWN(parent), EMBEDDED(Instant), EMBEDDED(Instant) |
| EAffiliateCommissions | edu_affiliate_commissions | 7 | UNKNOWN(affiliate), UNKNOWN(transaction), UNKNOWN(course), EMBEDDED(Instant) |
| EAffiliateLinks | edu_affiliate_links | 5 | UNKNOWN(affiliate), UNKNOWN(course), EMBEDDED(Instant) |
| EAffiliates | edu_affiliates | 8 | UNKNOWN(user), EMBEDDED(EAffiliateStatus), EMBEDDED(Instant), EMBEDDED(Instant) |
| EAiCredits | edu_ai_credits | 11 | UNKNOWN(user), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| EAiUsage | edu_ai_usage | 9 | UNKNOWN(user), UNKNOWN(course), EMBEDDED(EAIUsageType), EMBEDDED(Instant) |
| EAnalyticsEvents | edu_analytics_events | 6 | EMBEDDED(EAnalyticsEvent), UNKNOWN(user), UNKNOWN(course), EMBEDDED(Instant) |
| EApiKey | edu_api_keys | 11 | UNKNOWN(owner), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| EAssignments | edu_assignments | 14 | UNKNOWN(course), UNKNOWN(section), UNKNOWN(lesson), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| EAssignmentSubmissions | edu_assignment_submissions | 10 | UNKNOWN(user), UNKNOWN(assignment), EMBEDDED(EGradingStatus), EMBEDDED(Instant), EMBEDDED(Instant) |
| EAuditLog | edu_audit_log | 10 | UNKNOWN(actor), UNKNOWN(target), EMBEDDED(Instant) |
| EBundles | edu_bundles | 7 | UNKNOWN(creator), EMBEDDED(Instant) |
| ECertificates | edu_certificates | 11 | UNKNOWN(course), UNKNOWN(user), UNKNOWN(creator), UNKNOWN(certificate), UNKNOWN(template), EMBEDDED(Instant) |
| ECouponRedemption | edu_coupon_redemptions | 5 | UNKNOWN(coupon), UNKNOWN(user), UNKNOWN(transaction), EMBEDDED(Instant) |
| ECoupons | edu_coupons | 8 | UNKNOWN(creator), EMBEDDED(Instant), EMBEDDED(Instant) |
| ECourses | edu_courses | 44 | UNKNOWN(workspace), UNKNOWN(creator), EMBEDDED(ECourseType), EMBEDDED(ECourseContentType), EMBEDDED(ECourseLevel), EMBEDDED(ECourseStatus), EMBEDDED(ECourseValidationStatus), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| ECourseSections | edu_course_sections | 10 | UNKNOWN(course), EMBEDDED(ELessons), EMBEDDED(Instant), EMBEDDED(Instant) |
| ECreatorFinanceSettings | edu_creator_finance_settings | 26 | UNKNOWN(user), UNKNOWN(stripeAccount), EMBEDDED(PayoutMethod), EMBEDDED(TaxForm), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| ECreditLedger | edu_credit_ledger | 11 | UNKNOWN(user), EMBEDDED(ECreditLedgerActionType), UNKNOWN(reference), EMBEDDED(Instant) |
| EduCourseReviewLog | edu_course_review_logs | 6 | UNKNOWN(course), UNKNOWN(reviewer), EMBEDDED(Instant) |
| EEnrollments | edu_enrollments | 19 | UNKNOWN(course), UNKNOWN(user), UNKNOWN(workspace), UNKNOWN(lastAccessedLesson), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(ECourses) |
| EFraudFlag | edu_fraud_flags | 10 | UNKNOWN(targetUser), UNKNOWN(reviewer), EMBEDDED(Instant), EMBEDDED(Instant) |
| EGifts | edu_gifts | 11 | UNKNOWN(sender), UNKNOWN(course), EMBEDDED(EGiftStatus), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| EHoldingLedger | edu_holding_ledger | 12 | UNKNOWN(beneficiary), EMBEDDED(EBeneficiaryType), UNKNOWN(transaction), UNKNOWN(course), EMBEDDED(EHoldingStatus), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| ELearningPaths | edu_learning_paths | 7 | UNKNOWN(workspace), EMBEDDED(Instant), EMBEDDED(Instant) |
| ELedgerEntry | edu_ledger_entries | 12 | EMBEDDED(EventType), EMBEDDED(EntryType), EMBEDDED(AccountType), UNKNOWN(account), UNKNOWN(course), UNKNOWN(bundle), EMBEDDED(Instant) |
| ELessons | edu_lessons | 20 | UNKNOWN(course), UNKNOWN(section), EMBEDDED(ELessonType), EMBEDDED(Instant), EMBEDDED(Instant) |
| ENotifications | edu_notifications | 14 | UNKNOWN(user), UNKNOWN(workspace), EMBEDDED(ENotificationType), UNKNOWN(relatedEntity), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| EPayouts | edu_payouts | 12 | UNKNOWN(creator), EMBEDDED(EPayoutMethod), EMBEDDED(EPayoutStatus), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| EPayoutSchedule | edu_payout_schedules | 8 | UNKNOWN(creator), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| EProfiles | edu_profiles | 19 | UNKNOWN(user), EMBEDDED(ESocialLinksDTO), EMBEDDED(EPrivacySettingsDTO), EMBEDDED(ENotificationSettingsDTO), EMBEDDED(Instant), EMBEDDED(Instant) |
| EQuizAttempts | edu_quiz_attempts | 10 | UNKNOWN(user), UNKNOWN(quiz), EMBEDDED(EGradingStatus), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| EQuizzes | edu_quizzes | 15 | UNKNOWN(course), UNKNOWN(section), UNKNOWN(lesson), EMBEDDED(EQuizType), EMBEDDED(EQuestionDTO), EMBEDDED(Instant), EMBEDDED(Instant) |
| ERefund | edu_refunds | 17 | UNKNOWN(transaction), UNKNOWN(stripeCheckoutSession), UNKNOWN(stripeCharge), UNKNOWN(stripeRefund), UNKNOWN(buyer), UNKNOWN(seller), UNKNOWN(course), EMBEDDED(RefundType), EMBEDDED(RefundStatus), EMBEDDED(Instant), EMBEDDED(Instant) |
| EReports | edu_reports | 11 | UNKNOWN(reporter), UNKNOWN(targetEntity), EMBEDDED(EReportReason), EMBEDDED(EReportStatus), EMBEDDED(Instant), EMBEDDED(Instant) |
| EReviews | edu_reviews | 10 | UNKNOWN(course), UNKNOWN(user), EMBEDDED(Instant), EMBEDDED(Instant) |
| ESubscriptions | edu_subscriptions | 22 | UNKNOWN(user), EMBEDDED(ESubscriptionPlan), EMBEDDED(ESubscriptionStatus), UNKNOWN(paymentGateway), UNKNOWN(stripeCustomer), UNKNOWN(stripeSubscription), UNKNOWN(stripePrice), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| ESystemSettings | edu_system_settings | 5 | EMBEDDED(Instant) |
| ETransactions | edu_transactions | 33 | UNKNOWN(course), UNKNOWN(buyer), UNKNOWN(seller), UNKNOWN(affiliate), EMBEDDED(EPaymentMethod), EMBEDDED(EPaymentStatus), UNKNOWN(transaction), UNKNOWN(stripeCheckoutSession), EMBEDDED(Instant), UNKNOWN(bundle), UNKNOWN(referrer), EMBEDDED(Instant), EMBEDDED(Instant) |
| ETrustScores | edu_trust_scores | 20 | UNKNOWN(creator), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| EUser | edu_user | 15 | UNKNOWN(ssoProvider), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| EUserPreferences | edu_user_preferences | 10 | UNKNOWN(user), EMBEDDED(Instant), EMBEDDED(Instant) |
| EValidationReports | edu_validation_reports | 10 | UNKNOWN(course), UNKNOWN(user), UNKNOWN(reviewer), EMBEDDED(EValidationBreackdownDTO), EMBEDDED(Instant) |
| EWallet | edu_wallets | 7 | UNKNOWN(user), EMBEDDED(Instant) |
| EWalletTransaction | edu_wallet_transactions | 9 | UNKNOWN(user), EMBEDDED(TransactionType), EMBEDDED(TransactionStatus), UNKNOWN(reference), EMBEDDED(Instant) |
| EWebhookEvent | edu_webhook_events | 10 | UNKNOWN(stripeEvent), EMBEDDED(EventStatus), EMBEDDED(Instant), EMBEDDED(Instant) |
| EWorkspaceMembers | edu_workspace_members | 10 | UNKNOWN(workspace), UNKNOWN(user), EMBEDDED(ERoles), EMBEDDED(Instant), EMBEDDED(Instant) |
| EWorkspaces | edu_workspaces | 13 | UNKNOWN(owner), EMBEDDED(EWorkspaceType), EMBEDDED(ESubscriptionPlan), EMBEDDED(EWSettingsDTO), EMBEDDED(EWProfileDTO), EMBEDDED(Instant), EMBEDDED(Instant) |
| AnalyticsData | fin_analytics_data | 10 | UNKNOWN(organization), UNKNOWN(project), UNKNOWN(scenario), EMBEDDED(Instant) |
| MetricDefinition | fin_metric_definitions | 7 | UNKNOWN(organization) |
| FinAiTrainingSnapshot | fin_ai_training_snapshots | 10 | UNKNOWN(organization), UNKNOWN(project), UNKNOWN(scenario), UNKNOWN(user), EMBEDDED(Instant) |
| FinAssumption | fin_assumptions | 9 | UNKNOWN(organization), UNKNOWN(project), EMBEDDED(Instant) |
| FinAuditLog | fin_audit_logs | 11 | UNKNOWN(organization), UNKNOWN(project), UNKNOWN(user), UNKNOWN(entity), EMBEDDED(Instant) |
| FinBudget | fin_budgets | 9 | UNKNOWN(organization), UNKNOWN(project), EMBEDDED(Instant) |
| FinFinancialSnapshot | fin_financial_snapshots | 10 | UNKNOWN(organization), UNKNOWN(project), UNKNOWN(scenario), EMBEDDED(Instant) |
| FinPricingModel | fin_pricing_models | 10 | UNKNOWN(organization), UNKNOWN(project), EMBEDDED(Instant) |
| FinProject | fin_projects | 10 | UNKNOWN(organization), UNKNOWN(owner), EMBEDDED(Instant), EMBEDDED(Instant) |
| FinProjectMember | fin_project_members | 5 | UNKNOWN(project), UNKNOWN(user), EMBEDDED(ProjectRole), EMBEDDED(Instant) |
| FinSalesPlan | fin_sales_plans | 8 | UNKNOWN(organization), UNKNOWN(project), EMBEDDED(Instant) |
| FinScenario | fin_scenarios | 9 | UNKNOWN(organization), UNKNOWN(project), EMBEDDED(AssumptionOverride), EMBEDDED(Instant) |
| FinWorkspace | fin_workspaces | 9 | UNKNOWN(owner), EMBEDDED(Instant), EMBEDDED(Instant) |
| Scenario | scenarios | 10 | UNKNOWN(organization), UNKNOWN(project), UNKNOWN(parentScenario), UNKNOWN(baseVersion), EMBEDDED(Instant) |
| ScenarioOverride | fin_scenario_overrides | 9 | UNKNOWN(scenario), EMBEDDED(OverrideOperation), UNKNOWN(version), EMBEDDED(Instant) |
| ApplicantModel | applicants | 14 | UNKNOWN(job), UNKNOWN(company), UNKNOWN(candidate), EMBEDDED(Instant), EMBEDDED(Instant) |
| JobPostModel | job_posts | 19 | UNKNOWN(company), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| LAutomation | lead_automations | 8 | UNKNOWN(workspace), UNKNOWN(actionTemplate) |
| LLeadAutomation | leads_automations | 9 | UNKNOWN(workspace) |
| LCampaign | leads_campaigns | 6 | UNKNOWN(workspace), UNKNOWN(source) |
| LLead | leads_leads | 7 | UNKNOWN(workspace), UNKNOWN(sourceSignal) |
| LLeadCandidate | leads_candidates | 9 | UNKNOWN(workspace), UNKNOWN(source), UNKNOWN(rawSignal) |
| LLeadSource | leads_sources | 5 | UNKNOWN(workspace) |
| LLeadWorkspace | leads_workspaces | 4 | UNKNOWN(owner) |
| LNotification | lead_notifications | 7 | UNKNOWN(workspace), UNKNOWN(user) |
| LRawSignal | leads_raw_signals | 11 | UNKNOWN(source), UNKNOWN(workspace), UNKNOWN(platform) |
| LTask | lead_background_tasks | 7 | UNKNOWN(workspace), UNKNOWN(user) |
| LTemplate | lead_templates | 6 | UNKNOWN(workspace) |
| AICacheEntry | lp_ai_cache | 6 | EMBEDDED(Instant) |
| UserCredits | lp_user_credits | 5 | UNKNOWN(user), EMBEDDED(SubscriptionTier), EMBEDDED(Instant) |
| Goal | lp_goals | 11 | UNKNOWN(goal), UNKNOWN(user), EMBEDDED(Instant), EMBEDDED(GoalType), EMBEDDED(GoalTimeline), EMBEDDED(Instant), EMBEDDED(Instant) |
| JournalEntry | lp_journal_entries | 6 | UNKNOWN(user), EMBEDDED(LocalDate), EMBEDDED(Instant) |
| MoodEntry | lp_mood_entries | 6 | UNKNOWN(user), EMBEDDED(LocalDate), EMBEDDED(Instant) |
| WeeklyMoodSummary | lp_weekly_mood_summaries | 9 | UNKNOWN(user), EMBEDDED(LocalDate), EMBEDDED(LocalDate), EMBEDDED(Instant) |
| LPNotification | lp_notifications | 7 | UNKNOWN(user), EMBEDDED(Instant) |
| DailySchedule | lp_daily_schedules | 16 | UNKNOWN(schedule), UNKNOWN(plan), UNKNOWN(user), EMBEDDED(LocalDate), EMBEDDED(ScheduleTask), UNKNOWN(task) |
| StudyPlan | lp_study_plans | 9 | UNKNOWN(plan), UNKNOWN(goal), UNKNOWN(user), EMBEDDED(RoadmapItem), EMBEDDED(WeeklyPlan), EMBEDDED(Instant), EMBEDDED(Instant) |
| User | lp_users | 6 | EMBEDDED(Instant), EMBEDDED(Instant) |
| UserPreferences | lp_preferences | 17 | UNKNOWN(user) |
| UserProfile | lp_profiles | 14 | UNKNOWN(user), EMBEDDED(Instant), EMBEDDED(Instant) |
| ChatRoom | chat_rooms | 9 | EMBEDDED(RoomType), UNKNOWN(community) |
| Message | messages | 19 | UNKNOWN(room), UNKNOWN(sender), EMBEDDED(MessageType), UNKNOWN(forwardedFrom), EMBEDDED(Instant), UNKNOWN(replyTo) |
| UserPresence |  | 3 | UNKNOWN(user), EMBEDDED(PresenceStatus) |
| BillingAddressModel | portal_billing_address | 7 | UNKNOWN(company) |
| BillingHistoryModel | portal_billing_history | 8 | UNKNOWN(company), UNKNOWN(user), UNKNOWN(session) |
| InvoicesModel | portal_invoices | 13 | UNKNOWN(company), UNKNOWN(invoice), UNKNOWN(subscription), UNKNOWN(session) |
| PaymentMethodsModel | portal_payment_methods | 8 | UNKNOWN(company), UNKNOWN(user), UNKNOWN(session) |
| PaymentSubscriptionsModel | portal_subscriptions | 9 | UNKNOWN(company), UNKNOWN(subscription) |
| PrePaymentModel | portal_pre_payment_data | 14 | UNKNOWN(company), UNKNOWN(subscription), UNKNOWN(paymentMethod), UNKNOWN(billingAddress), UNKNOWN(invoice) |
| RecordedCoursePayment | recorded_course_payments | 17 | UNKNOWN(course), UNKNOWN(learner), UNKNOWN(trainer), UNKNOWN(company), EMBEDDED(BigDecimal), EMBEDDED(BigDecimal), EMBEDDED(BigDecimal), EMBEDDED(BigDecimal), UNKNOWN(transaction) |
| UsageDataModel | portal_usage_data | 10 | UNKNOWN(company) |
| AmbassadorTaskProgressModel | ambassador_task_progress | 10 | UNKNOWN(ambassador), UNKNOWN(task), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| BadgeDefinition |  | 7 | EMBEDDED(Instant) |
| CourseCertificateModel | course_certificates | 11 | UNKNOWN(employee), UNKNOWN(course), UNKNOWN(certificate) |
| CourseCouponsModel | course_coupons | 31 | UNKNOWN(stripeCoupon), UNKNOWN(user), EMBEDDED(private String), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Status), UNKNOWN(redeemedForCourse), UNKNOWN(redeemedForInstallment), UNKNOWN(task), UNKNOWN(campaign), EMBEDDED(Instant) |
| CourseReminderLog | course_reminder_logs | 6 | UNKNOWN(employee), UNKNOWN(module), UNKNOWN(course), EMBEDDED(Instant) |
| EmpCoursesModel | portal_emp_courses | 8 | UNKNOWN(employee), EMBEDDED(CourseEnrollment), EMBEDDED(RecordedCourseEnrollment) |
| GamificationTaskModel | gamification_tasks | 13 | UNKNOWN(reward), EMBEDDED(Instant) |
| QuizAttempt | quiz_attempts | 11 | UNKNOWN(employee), UNKNOWN(course), UNKNOWN(module), UNKNOWN(quiz), EMBEDDED(QuestionAnswer) |
| SwagItem |  | 8 | EMBEDDED(Instant) |
| InterviewQuestionModel | portal_interviewQuestion | 15 | EMBEDDED(Question), EMBEDDED(Answer) |
| JobApplyModel | portal_job_applicants | 3 | UNKNOWN(company), UNKNOWN(job) |
| PreOrderModel | portal_preorder | 5 |  |
| Referral | referrals | 8 | UNKNOWN(referrer), UNKNOWN(referredUser), EMBEDDED(ReferralType), EMBEDDED(ReferralStatus), EMBEDDED(Instant), EMBEDDED(Instant) |
| ReferralCode | referral_codes | 4 | UNKNOWN(user), EMBEDDED(Instant) |
| ReferralCommission | referral_commissions | 6 | UNKNOWN(referrer), UNKNOWN(referredCreator), EMBEDDED(Instant), EMBEDDED(Instant) |
| ReputationEvent | reputation_events | 6 | UNKNOWN(user), EMBEDDED(ReputationSourceType), UNKNOWN(source) |
| UserBadge | user_badges | 4 | UNKNOWN(user), EMBEDDED(BadgeType) |
| UserReputation | user_reputation | 7 | UNKNOWN(user) |
| ResumeModel | resumes | 72 | UNKNOWN(employee), UNKNOWN(template), EMBEDDED(PersonalInfo), EMBEDDED(WorkExperience), EMBEDDED(Education), EMBEDDED(Skill), EMBEDDED(Project), EMBEDDED(Certificate), EMBEDDED(CustomSection), EMBEDDED(ResumeSettings), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(CustomSectionItem) |
| SoftwareAppModel | portal_software_apps | 17 | EMBEDDED(Instant), EMBEDDED(Instant), UNKNOWN(company) |
| FeatureFlag | feature_flags | 4 | EMBEDDED(ESubscriptionPlan) |
| Subscription | subscriptions | 16 | UNKNOWN(user), EMBEDDED(ESubscriptionPlan), EMBEDDED(ESubscriptionStatus), EMBEDDED(Instant), EMBEDDED(Instant), UNKNOWN(stripeCustomer), UNKNOWN(stripeSubscription), UNKNOWN(stripePrice), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant), EMBEDDED(Instant) |
| SupportRequestModel | support_requests | 7 | EMBEDDED(Instant) |
| TrackingEvent | events | 79 | UNKNOWN(tracking), UNKNOWN(session), UNKNOWN(user), EMBEDDED(Instant), UNKNOWN(element), UNKNOWN(experiment), UNKNOWN(funnel), UNKNOWN(form), EMBEDDED(Instant) |
| EmpCertificatesModel | portal_emp_certificates | 3 | UNKNOWN(employee), EMBEDDED(EmpCertificatesDTO) |
| EmpContactModel | portal_emp_contact | 5 | UNKNOWN(employee), EMBEDDED(EmpContactDTO), EMBEDDED(SocialLinksDTO) |
| EmpEducationModel | portal_emp_education | 3 | UNKNOWN(employee), EMBEDDED(EmpEducationDTO) |
| EmpExperiencesModel | portal_emp_experiences | 3 | UNKNOWN(employee), EMBEDDED(EmpExperiencesDTO) |
| EmpFollowersModel | portal_emp_followers | 3 | UNKNOWN(employee), EMBEDDED(EmpFollowersDTO) |
| EmpFollowingModel | portal_emp_following | 3 | UNKNOWN(employee), EMBEDDED(EmpFollowingDTO) |
| EmployeeModel | portal_employees | 30 | EMBEDDED(FavJobDTO), UNKNOWN(company) |
| EmpProjectsModel | portal_emp_projects | 3 | UNKNOWN(employee), EMBEDDED(EmpProjectsDTO) |
| EmpSkillsModel | portal_emp_skills | 3 | UNKNOWN(employee), EMBEDDED(EmpSkillsDTO) |
| TrainerProfile | trainer_profiles | 15 | UNKNOWN(employee) |
| WorkspaceModel | workspaces | 8 | UNKNOWN(owner) |
| UserActivity | portal_user_activity | 12 | UNKNOWN(user), EMBEDDED(Instant) |
| WhitelistDomains | portal_whitelist_domains | 4 |  |
| ContactSubmission | contact_form_submissions | 5 |  |
| CtaLeadSubmission | cta_lead_submissions | 7 |  |
| NewsLatterModel | portal_news_letter | 2 |  |
| TokenModel | portal_tokens | 5 |  |

## Detailed Model Fields
### AIQuota
**Collection:** ai_quotas

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed(unique = true) |
| monthlyLimit | Integer |  |
| used | Integer |  |
| resetDate | Instant |  |

### AIUsage
**Collection:** ai_usage_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed |
| type | AIUsageType |  |
| creditsUsed | Integer |  |
| createdAt | Instant | CreatedDate |

### CreditRecord
**Collection:** course_ai_credit_record

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| creditsRemaining | int |  |
| lastReset | LocalDate |  |

### AmbassadorLeaderboardModel
**Collection:** ambassador_leaderboard

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| type | String |  |
| ambassadorId | String |  |
| name | String |  |
| email | String |  |
| level | String |  |
| score | int |  |
| rank | int |  |
| generatedAt | Instant |  |
| expireAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s") |

### AmbassadorPointAudit
**Collection:** ambassador_point_audit

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| ambassadorId | String |  |
| reason | String |  |
| points | int |  |
| createdAt | Instant |  |
| metadata | String |  |

### AmbassadorProfileModel
**Collection:** ambassador_profiles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| employeeId | String | Indexed(unique = true, sparse = true) |
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
| lastPointEarnedAt | Instant |  |
| lastLoginDate | Instant |  |

### AmbassadorRewardModel
**Collection:** ambassador_rewards

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
| ambassadorId | String | Indexed |
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
| id | String | Id |
| referralCode | String | Indexed |
| ambassadorId | String | Indexed |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
| title | String |  |
| slug | String | Indexed(unique = true) |
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
| id | String | Id |
| title | String | TextIndexed |
| slug | String | Indexed(unique = true) |
| content | String | TextIndexed |
| excerpt | String | TextIndexed |
| authorId | String |  |
| coverImage | String |  |
| tagIds | List<String> |  |
| status | ArticleStatus |  |
| readTime | int |  |
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
| id | String | Id |
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
| id | String | Id |
| name | String | Indexed(unique = true) |
| slug | String | Indexed(unique = true) |

### AsyncUpdateAuditLog
**Collection:** async_update_audit_log

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| courseId | String |  |
| batchId | String |  |
| employeeId | String |  |
| operation | String |  |
| status | String |  |
| retryCount | int |  |
| errorMessage | String |  |
| createdAt | LocalDateTime |  |
| updatedAt | LocalDateTime |  |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s") |

### ClientActAuditLog
**Collection:** client_act_audit_log

| Field | Type | Annotations |
|-------|------|-------------|
| userId | String | Id |
| ipAddress | String |  |
| sessionId | String |  |
| action | String |  |
| source | String |  |
| details | Map<String, Object> |  |
| timestamp | LocalDateTime |  |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s") |

### CourseReminderAuditLog
**Collection:** course_reminder_audit_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s") |

### LeadOSAuditLog
**Collection:** leados_audit_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String |  |
| userId | String |  |
| action | String |  |
| entityId | String |  |
| entityType | String |  |
| details | Map<String, Object> |  |
| ipAddress | String |  |

### SchedulerLogModel
**Collection:** 

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| jobName | String |  |
| runAt | Instant |  |
| status | String |  |
| message | String |  |
| expireAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s") |

### StripeAuditLog
**Collection:** stripe_audit_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| eventId | String |  |
| eventType | String |  |
| sessionId | String |  |
| customerId | String |  |
| subscriptionId | String |  |
| paymentIntentId | String |  |
| status | String |  |
| errorMessage | String |  |
| rawPayload | String | Lob |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s") |

### TaskRewardAuditModel
**Collection:** 

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| ambassadorId | String |  |
| taskId | String |  |
| rewardType | String |  |
| rewardId | String |  |
| rewardTitle | String |  |
| status | String |  |
| note | String |  |
| issuedAt | Instant |  |
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s") |

### CredentialsModel
**Collection:** portal_credentials

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
| token | String |  |
| userId | String |  |
| expirationDate | LocalDateTime |  |

### PermissionModel
**Collection:** permissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| name | String |  |
| description | String |  |
| category | String |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### RoleModel
**Collection:** roles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| name | String | Indexed(unique = true) |
| permissions | List<String> |  |
| inheritsFrom | List<String> |  |
| description | String |  |

### FeatureModel
**Collection:** portal_feature-requests

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| featureType | String |  |
| description | String |  |
| attachment | String |  |

### IssueModel
**Collection:** portal_report-issues

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| issueType | String |  |
| description | String |  |
| attachment | String |  |

### Login
**Collection:** portal_logins

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String |  |
| loginDates | List<String> |  |
| metaData | List<LoginMetaDTO> |  |

### SystemNotificationsModel
**Collection:** portal_systemNotifications

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| message | String |  |
| startTime | Date |  |
| endTime | Date |  |
| url | String |  |
| active | boolean |  |

### Activity
**Collection:** community_activities

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String |  |
| action | String |  |
| targetId | String |  |
| timestamp | LocalDateTime |  |

### Comment
**Collection:** comments

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| installment | List<InstallmentDTO> | Field("installment") |
| duration | String |  |
| modules | List<ModuleDTO> | Field("modules") |
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
| materials | List<MaterialsDTO> | Field("materials") |
| quizzes | List<QuizDTO> | Field("quizzes") |
| notifiers | List<CourseMissedNotify> | Field("notifiers") |

### RecordedCourseModel
**Collection:** recorded_courses

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| title | String |  |
| subtitle | String |  |
| description | String |  |
| price | BigDecimal |  |
| published | boolean |  |
| approved | boolean |  |
| createdAt | String |  |
| updatedAt | String |  |
| modules | List<RecModuleDTO> | Field("modules") |
| image | String |  |
| skills | List<String> |  |
| requirements | List<String> |  |
| level | String |  |
| lecturer | String |  |
| lecturerNameTag | String |  |
| lecturerEmail | String |  |
| category | String |  |
| reviews | List<RecordedCourseReviewDTO> | Field("reviews") |
| certificate | boolean |  |
| companyId | String |  |
| trainerId | String |  |
| trainerShare | BigDecimal |  |
| platformShare | BigDecimal |  |
| installment | InstallmentDTO |  |

### TrainCompanyModel
**Collection:** train_company

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| name | String |  |
| email | String |  |
| address | String |  |
| phone | String |  |
| teamMembers | List<TeamMemberDTO> |  |

### TrainersModel
**Collection:** trainers

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
| companyId | String |  |
| companyName | String |  |
| companyLogo | String |  |
| companyLevel | String |  |
| postedJobs | List<PostedJobsDTO> | Field("postedJobs") |

### CmpSocialModel
**Collection:** portal_cmp_socials

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| companyId | String |  |
| socialLinks | List<SocialLinksDTO> | Field("socialLinks") |

### CompanyModel
**Collection:** portal_companies

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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

### StandaloneFileModel
**Collection:** standalone_files

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
| affiliateId | String | Indexed |
| transactionId | String | Indexed |
| courseId | String |  |
| amount | Double |  |
| currency | String |  |
| createdAt | Instant | CreatedDate |

### EAffiliateLinks
**Collection:** edu_affiliate_links

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| affiliateId | String | Indexed |
| courseId | String | Indexed |
| trackingCode | String | Indexed(unique = true) |
| createdAt | Instant | CreatedDate |

### EAffiliates
**Collection:** edu_affiliates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed(unique = true) |
| referralCode | String | Indexed(unique = true) |
| commissionRate | Double |  |
| totalEarnings | Double |  |
| status | EAffiliateStatus | Indexed |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### EAiCredits
**Collection:** edu_ai_credits

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed(unique = true) |
| balance | Integer |  |
| monthlyLimit | Integer |  |
| lifetimePurchased | Integer |  |
| lifetimeUsed | Integer |  |
| expiresAt | Instant | Indexed |
| lastResetDate | Instant | Indexed |
| version | Long | Version |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### EAiUsage
**Collection:** edu_ai_usage

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed |
| courseId | String | Indexed |
| prompt | String |  |
| response | String |  |
| type | EAIUsageType |  |
| usedCredits | Integer |  |
| createdBy | String |  |
| createdAt | Instant | CreatedDate |

### EAnalyticsEvents
**Collection:** edu_analytics_events

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| type | EAnalyticsEvent | Indexed |
| userId | String | Indexed |
| courseId | String | Indexed |
| metadata | Map<String, Object> |  |
| timestamp | Instant |  |

### EApiKey
**Collection:** edu_api_keys

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| apiKey | String | Indexed(unique = true) |
| apiKeyHint | String |  |
| ownerId | String | Indexed |
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
| id | String | Id |
| courseId | String | Indexed |
| sectionId | String | Indexed |
| lessonId | String | Indexed |
| title | String |  |
| description | String |  |
| instructions | String |  |
| maxScore | Double |  |
| weightage | Double |  |
| dueDate | Instant |  |
| isPublished | Boolean |  |
| createdBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### EAssignmentSubmissions
**Collection:** edu_assignment_submissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed |
| assignmentId | String | Indexed |
| content | String |  |
| status | EGradingStatus |  |
| score | Double |  |
| feedback | String |  |
| gradedBy | String |  |
| gradedAt | Instant |  |
| submittedAt | Instant | CreatedDate |

### EAuditLog
**Collection:** edu_audit_log

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| actorId | String | Indexed |
| action | String | Indexed |
| targetId | String |  |
| targetType | String |  |
| previousState | String |  |
| newState | String |  |
| ipAddress | String |  |
| userAgent | String |  |
| createdAt | Instant | Indexed |

### EBundles
**Collection:** edu_bundles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| creatorId | String | Indexed |
| name | String |  |
| bundlePrice | Double |  |
| originalTotalPrice | Double |  |
| savingsPercent | Double |  |
| createdAt | Instant | CreatedDate |

### ECertificates
**Collection:** edu_certificates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| courseId | String | Indexed |
| userId | String | Indexed |
| creatorId | String | Indexed |
| courseName | String |  |
| recipientName | String |  |
| certificateId | String | Indexed(unique = true) |
| url | String |  |
| templateId | String |  |
| shareableLink | String |  |
| issuedAt | Instant | CreatedDate |

### ECouponRedemption
**Collection:** edu_coupon_redemptions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| couponId | String | Indexed |
| userId | String | Indexed |
| transactionId | String |  |
| redeemedAt | Instant | CreatedDate |

### ECoupons
**Collection:** edu_coupons

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| creatorId | String | Indexed |
| code | String | Indexed(unique = true) |
| discountType | String |  |
| discountValue | Double |  |
| maxRedemptions | Integer |  |
| expiresAt | Instant |  |
| createdAt | Instant | CreatedDate |

### ECourses
**Collection:** edu_courses

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String | Indexed |
| creatorId | String | Indexed |
| title | String | TextIndexed |
| description | String | TextIndexed |
| shortDescription | String |  |
| thumbnail | String |  |
| previewVideoUrl | String |  |
| type | ECourseType | Indexed |
| contentType | ECourseContentType | Indexed |
| price | Double |  |
| compareAtPrice | Double |  |
| currency | String |  |
| published | Boolean |  |
| isPrivate | Boolean |  |
| level | ECourseLevel | Indexed |
| language | String |  |
| slug | String | Indexed(unique = true) |
| rating | Double | Indexed |
| totalEnrollments | Integer | Indexed |
| totalReviews | Integer |  |
| totalHours | Integer |  |
| totalLessons | Integer |  |
| isFeatured | Boolean |  |
| isTrending | Boolean |  |
| searchRank | Integer | Indexed |
| status | ECourseStatus | Indexed |
| aiGenerated | Boolean |  |
| validationStatus | ECourseValidationStatus |  |
| aiScore | Double |  |
| plagiarismScore | Double |  |
| overallQualityScore | Double |  |
| validationFindings | String |  |
| talnovaVerified | Boolean |  |
| moderationRejectionReason | String |  |
| trustDisclaimer | String | Transient |
| creatorTier | String | Transient |
| trustWarning | String | Transient |
| instructorName | String | Transient |
| publishedAt | Instant |  |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### ECourseSections
**Collection:** edu_course_sections

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| courseId | String | Indexed |
| order | Integer |  |
| title | String |  |
| description | String |  |
| lessonDetails | List<ELessons> | Transient |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### ECreatorFinanceSettings
**Collection:** edu_creator_finance_settings

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed(unique = true) |
| stripeAccountId | String |  |
| payoutMethods | List<PayoutMethod> |  |
| taxVerificationStatus | String |  |
| profileVerificationStatus | String |  |
| taxForms | List<TaxForm> |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |
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
| id | String | Id |
| userId | String | Indexed |
| actionType | ECreditLedgerActionType |  |
| amount | Integer |  |
| balanceBefore | Integer |  |
| balanceAfter | Integer |  |
| newBalance | Integer |  |
| metadata | String |  |
| referenceId | String |  |
| referenceType | String |  |
| createdAt | Instant | Indexed |

### EduCourseReviewLog
**Collection:** edu_course_review_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| courseId | String | Indexed |
| reviewerId | String | Indexed |
| action | String |  |
| reason | String |  |
| createdAt | Instant | CreatedDate |

### EEnrollments
**Collection:** edu_enrollments

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| courseId | String | Indexed |
| userId | String | Indexed |
| workspaceId | String | Indexed |
| lastAccessedLessonId | String |  |
| lastAccessedAt | Instant |  |
| completedAt | Instant |  |
| enrolledAt | Instant | Indexed |
| lastStreakDate | Instant |  |
| source | String | Indexed |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |
| course | ECourses |  |
| name | String |  |
| email | String |  |
| avatar | String |  |
| coursesCount | Integer |  |

### EFraudFlag
**Collection:** edu_fraud_flags

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| targetUserId | String | Indexed |
| flagType | String |  |
| severity | String |  |
| evidenceBlob | String |  |
| status | String | Indexed |
| reviewerId | String |  |
| resolutionNotes | String |  |
| createdAt | Instant | Indexed |
| resolvedAt | Instant |  |

### EGifts
**Collection:** edu_gifts

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| senderId | String | Indexed |
| recipientEmail | String | Indexed |
| courseId | String | Indexed |
| redeemCode | String | Indexed(unique = true) |
| status | EGiftStatus | Indexed |
| personalMessage | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |
| redeemedAt | Instant |  |
| expiresAt | Instant | Indexed(name = "gifts_expireAt_idx", expireAfter = "0s") |

### EHoldingLedger
**Collection:** edu_holding_ledger

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| beneficiaryId | String | Indexed |
| beneficiaryType | EBeneficiaryType | Indexed |
| transactionId | String | Indexed |
| courseId | String |  |
| amount | Double |  |
| currency | String |  |
| status | EHoldingStatus | Indexed |
| clearanceDate | Instant | Indexed |
| version | Long | Version |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### ELearningPaths
**Collection:** edu_learning_paths

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String | Indexed |
| title | String |  |
| description | String |  |
| createdBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### ELedgerEntry
**Collection:** edu_ledger_entries

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| eventReference | String | Indexed |
| eventType | EventType | Indexed |
| entryType | EntryType |  |
| accountType | AccountType | Indexed |
| accountId | String | Indexed |
| amount | Double |  |
| currency | String |  |
| courseId | String |  |
| bundleId | String |  |
| description | String |  |
| createdAt | Instant | CreatedDate |

### ELessons
**Collection:** edu_lessons

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| courseId | String | Indexed |
| sectionId | String | Indexed |
| order | Integer |  |
| title | String |  |
| description | String |  |
| contentUrl | String |  |
| textContent | String |  |
| markdownContent | String |  |
| type | ELessonType | Indexed |
| duration | Integer |  |
| videoThumbnail | String |  |
| plagiarismScore | Double |  |
| aiScore | Double |  |
| qualityScore | Double |  |
| validationFindings | String |  |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### ENotifications
**Collection:** edu_notifications

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed |
| workspaceId | String | Indexed |
| type | ENotificationType | Indexed |
| title | String |  |
| message | String |  |
| url | String |  |
| icon | String |  |
| actionType | String |  |
| relatedEntityId | String |  |
| entityType | String |  |
| readAt | Instant |  |
| createdAt | Instant | CreatedDate |
| expiresAt | Instant | Indexed(name = "notifications_expireAt_idx", expireAfter = "0s") |

### EPayouts
**Collection:** edu_payouts

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| creatorId | String | Indexed |
| amount | Double |  |
| method | EPayoutMethod | Indexed |
| status | EPayoutStatus | Indexed |
| transactionReference | String |  |
| bankDetails | String |  |
| paypalEmail | String |  |
| platformFee | Double |  |
| createdAt | Instant | CreatedDate |
| paidAt | Instant |  |
| requestedAt | Instant |  |

### EPayoutSchedule
**Collection:** edu_payout_schedules

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| creatorId | String | Indexed(unique = true) |
| frequency | String |  |
| dayTarget | String |  |
| active | Boolean |  |
| lastProcessedAt | Instant |  |
| nextScheduledAt | Instant |  |
| createdAt | Instant | CreatedDate |

### EProfiles
**Collection:** edu_profiles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed(unique = true) |
| firstName | String |  |
| lastName | String |  |
| publicEmail | String |  |
| publicPhone | String |  |
| avatarUrl | String |  |
| bio | String |  |
| socialLinks | ESocialLinksDTO |  |
| industry | String | Indexed |
| company | String |  |
| jobTitle | String |  |
| privacySettings | EPrivacySettingsDTO |  |
| notificationSettings | ENotificationSettingsDTO |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |
| totalStudents | Integer |  |
| totalCourses | Integer |  |
| rating | Double |  |

### EQuizAttempts
**Collection:** edu_quiz_attempts

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed |
| quizId | String | Indexed |
| score | Double |  |
| percentage | Double |  |
| status | EGradingStatus |  |
| isLatest | Boolean |  |
| startedAt | Instant |  |
| completedAt | Instant |  |
| createdAt | Instant | CreatedDate |

### EQuizzes
**Collection:** edu_quizzes

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| courseId | String | Indexed |
| sectionId | String | Indexed |
| lessonId | String | Indexed |
| title | String |  |
| description | String |  |
| type | EQuizType |  |
| durationLimit | Integer |  |
| passingScore | Double |  |
| questions | List<EQuestionDTO> |  |
| isPublished | Boolean |  |
| allowRetakes | Integer |  |
| createdBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### ERefund
**Collection:** edu_refunds

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| transactionId | String | Indexed |
| stripeCheckoutSessionId | String | Indexed |
| stripeChargeId | String | Indexed |
| stripeRefundId | String | Indexed(unique = true, sparse = true) |
| buyerId | String | Indexed |
| sellerId | String | Indexed |
| courseId | String | Indexed |
| refundAmount | Double |  |
| originalAmount | Double |  |
| currency | String |  |
| type | RefundType |  |
| status | RefundStatus | Indexed |
| reason | String |  |
| initiatedBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### EReports
**Collection:** edu_reports

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| reporterId | String | Indexed |
| targetEntityId | String | Indexed |
| entityType | String |  |
| reason | EReportReason | Indexed |
| description | String |  |
| status | EReportStatus | Indexed |
| resolutionNotes | String |  |
| resolvedBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### EReviews
**Collection:** edu_reviews

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| courseId | String | Indexed |
| userId | String | Indexed |
| rating | Double | Indexed |
| title | String |  |
| content | String |  |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### ESubscriptions
**Collection:** edu_subscriptions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed |
| plan | ESubscriptionPlan | Indexed |
| status | ESubscriptionStatus | Indexed |
| remainingCredits | Integer |  |
| totalCredits | Integer |  |
| price | Double |  |
| billingCycle | String |  |
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
| lastCreditResetAt | Instant |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### ESystemSettings
**Collection:** edu_system_settings

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| category | String | Indexed(unique = true) |
| settings | Map<String, Object> |  |
| updatedAt | Instant |  |
| updatedBy | String |  |

### ETransactions
**Collection:** edu_transactions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| courseId | String | Indexed |
| buyerId | String | Indexed |
| sellerId | String | Indexed |
| amount | Double |  |
| currency | String |  |
| platformFee | Double |  |
| commissionRate | Double |  |
| creatorPlanAtPurchase | String |  |
| creatorEarning | Double |  |
| affiliateId | String | Indexed |
| affiliateEarning | Double |  |
| paymentMethod | EPaymentMethod | Indexed |
| paymentStatus | EPaymentStatus | Indexed |
| transactionId | String | Indexed(unique = true) |
| stripeCheckoutSessionId | String | Indexed |
| paymentGateway | String |  |
| paymentGatewayResponse | String |  |
| idempotencyKey | String |  |
| expiresAt | Instant | Indexed |
| appliedCouponCode | String |  |
| discountAmount | Double |  |
| originalAmount | Double |  |
| bundleId | String | Indexed |
| taxAmount | Double |  |
| taxRate | Double |  |
| referrerId | String | Indexed |
| referralCommission | Double |  |
| version | Long | Version |
| createdBy | String |  |
| updatedBy | String |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### ETrustScores
**Collection:** edu_trust_scores

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| creatorId | String | Indexed(unique = true) |
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
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### EUser
**Collection:** edu_user

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| email | String | Indexed(unique = true) |
| phone | String | Indexed(sparse = true) |
| passwordHash | String | JsonIgnore |
| ssoProvider | String |  |
| ssoProviderId | String |  |
| displayName | String |  |
| avatarUrl | String |  |
| banReason | String |  |
| lastLoginAt | Instant |  |
| emailVerificationToken | String |  |
| passwordResetToken | String |  |
| passwordResetExpiry | Instant |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### EUserPreferences
**Collection:** edu_user_preferences

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed(unique = true) |
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
| id | String | Id |
| courseId | String | Indexed |
| userId | String | Indexed |
| reviewerId | String | Indexed |
| aiScore | Double |  |
| status | String | Indexed |
| breakdown | EValidationBreackdownDTO |  |
| feedback | String |  |
| createdBy | String |  |
| createdAt | Instant | CreatedDate |

### EWallet
**Collection:** edu_wallets

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed(unique = true) |
| availableBalance | Double |  |
| pendingBalance | Double |  |
| currency | String |  |
| version | Long | Version |
| updatedAt | Instant |  |

### EWalletTransaction
**Collection:** edu_wallet_transactions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed |
| type | TransactionType |  |
| amount | Double |  |
| currency | String |  |
| status | TransactionStatus |  |
| referenceId | String |  |
| description | String |  |
| createdAt | Instant | CreatedDate |

### EWebhookEvent
**Collection:** edu_webhook_events

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| stripeEventId | String | Indexed(unique = true) |
| eventType | String | Indexed |
| status | EventStatus | Indexed |
| payload | String |  |
| signatureHeader | String |  |
| errorMessage | String |  |
| errorStackTrace | String |  |
| nextRetryAt | Instant |  |
| processedAt | Instant | CreatedDate |

### EWorkspaceMembers
**Collection:** edu_workspace_members

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String | Indexed |
| userId | String | Indexed |
| role | ERoles | Indexed |
| status | String |  |
| department | String |  |
| invitedBy | String |  |
| createdBy | String |  |
| joinedAt | Instant | CreatedDate |
| lastActiveAt | Instant |  |

### EWorkspaces
**Collection:** edu_workspaces

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| ownerId | String | Indexed |
| type | EWorkspaceType | Indexed |
| domain | String | Indexed(unique = true) |
| plan | ESubscriptionPlan |  |
| name | String |  |
| description | String |  |
| logoUrl | String |  |
| maxMembers | Integer |  |
| settings | EWSettingsDTO |  |
| profile | EWProfileDTO |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### AnalyticsData
**Collection:** fin_analytics_data

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
| organizationId | String |  |
| projectId | String |  |
| key | String |  |
| value | String |  |
| unit | String |  |
| category | String |  |
| version | Integer | Version |
| createdAt | Instant |  |

### FinAuditLog
**Collection:** fin_audit_logs

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
| organizationId | String |  |
| projectId | String |  |
| category | String |  |
| type | String |  |
| monthlyAllocations | Map<String, Double> |  |
| formula | String |  |
| version | Integer | Version |
| createdAt | Instant |  |

### FinFinancialSnapshot
**Collection:** fin_financial_snapshots

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
| organizationId | String |  |
| projectId | String |  |
| tier | String |  |
| price | Double |  |
| costPerUser | Double |  |
| commissionPercent | Double |  |
| margin | Double |  |
| version | Integer | Version |
| effectiveDate | Instant |  |

### FinProject
**Collection:** fin_projects

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
| projectId | String |  |
| userId | String |  |
| role | ProjectRole |  |
| joinedAt | Instant |  |

### FinSalesPlan
**Collection:** fin_sales_plans

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| organizationId | String |  |
| projectId | String |  |
| month | String |  |
| userCounts | Map<String, Integer> |  |
| growthRate | Double |  |
| version | Integer | Version |
| createdAt | Instant |  |

### FinScenario
**Collection:** fin_scenarios

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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

### LAutomation
**Collection:** lead_automations

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String |  |
| name | String |  |
| triggerType | String |  |
| triggerScoreThreshold | int |  |
| triggerKeywords | List<String> |  |
| actionType | String |  |
| actionTemplateId | String |  |

### LLeadAutomation
**Collection:** leads_automations

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String |  |
| name | String |  |
| triggerType | String |  |
| triggerScoreThreshold | Double |  |
| triggerKeywords | List<String> |  |
| triggerStatus | String |  |
| actionType | String |  |
| actionWebhookUrl | String |  |

### LCampaign
**Collection:** leads_campaigns

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String |  |
| name | String |  |
| platform | String |  |
| template | String |  |
| sourceId | String |  |

### LLead
**Collection:** leads_leads

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String |  |
| name | String |  |
| platform | String |  |
| score | Double |  |
| sourceSignalId | String |  |
| notes | String |  |

### LLeadCandidate
**Collection:** leads_candidates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| name | String |  |
| workspaceId | String |  |
| sourceId | String |  |
| rawSignalId | String |  |
| summary | String |  |
| intent | String |  |
| leadScore | Double |  |
| tags | List<String> |  |

### LLeadSource
**Collection:** leads_sources

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String |  |
| platform | String |  |
| name | String |  |
| config | Map<String, Object> |  |

### LLeadWorkspace
**Collection:** leads_workspaces

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| name | String |  |
| ownerId | String |  |
| memberIds | List<String> |  |

### LNotification
**Collection:** lead_notifications

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String |  |
| userId | String |  |
| title | String |  |
| message | String |  |
| type | String |  |
| link | String |  |

### LRawSignal
**Collection:** leads_raw_signals

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| sourceId | String |  |
| workspaceId | String |  |
| platformId | String |  |
| content | String |  |
| author | String |  |
| url | String |  |
| metadata | Map<String, Object> |  |
| intent | String |  |
| score | Double |  |
| tags | List<String> |  |

### LTask
**Collection:** lead_background_tasks

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String |  |
| userId | String |  |
| type | String |  |
| resultUrl | String |  |
| errorMessage | String |  |
| metadata | Map<String, Object> |  |

### LTemplate
**Collection:** lead_templates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| workspaceId | String |  |
| name | String |  |
| content | String |  |
| platform | String |  |
| category | String |  |

### AICacheEntry
**Collection:** lp_ai_cache

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| cacheKey | String | Indexed(unique = true) |
| responseJson | String |  |
| provider | String |  |
| promptHash | String |  |
| createdAt | Instant | Indexed(expireAfter = "24h") |

### UserCredits
**Collection:** lp_user_credits

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed(unique = true) |
| tier | SubscriptionTier |  |
| creditsAvailable | int |  |
| lastRefreshedAt | Instant |  |

### Goal
**Collection:** lp_goals

| Field | Type | Annotations |
|-------|------|-------------|
| goalId | String | Id |
| userId | String | Indexed |
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
| id | String | Id |
| userId | String | Indexed |
| date | LocalDate |  |
| reflection | String |  |
| aiInsight | String |  |
| createdAt | Instant |  |

### MoodEntry
**Collection:** lp_mood_entries

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed |
| date | LocalDate |  |
| score | int |  |
| label | String |  |
| createdAt | Instant |  |

### WeeklyMoodSummary
**Collection:** lp_weekly_mood_summaries

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
| userId | String | Indexed |
| title | String |  |
| message | String |  |
| type | String |  |
| isRead | boolean |  |
| createdAt | Instant |  |

### DailySchedule
**Collection:** lp_daily_schedules

| Field | Type | Annotations |
|-------|------|-------------|
| scheduleId | String | Id |
| planId | String | Indexed |
| userId | String | Indexed |
| scheduleDate | LocalDate |  |
| tasks | List<ScheduleTask> |  |
| isCompleted | boolean | JsonProperty("isCompleted") |
| taskId | String |  |
| title | String |  |
| estimatedTime | String |  |
| category | String |  |
| startTime | String |  |
| endTime | String |  |
| isCompleted | boolean | JsonProperty("isCompleted") |
| completedAt | String |  |
| priority | String |  |
| notes | String |  |

### StudyPlan
**Collection:** lp_study_plans

| Field | Type | Annotations |
|-------|------|-------------|
| planId | String | Id |
| goalId | String | Indexed |
| userId | String | Indexed |
| status | String | Indexed |
| roadmap | List<PlanResponse.RoadmapItem> |  |
| weeklyPlans | List<PlanResponse.WeeklyPlan> |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |
| progressPercentage | double |  |

### User
**Collection:** lp_users

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| email | String |  |
| passwordHash | String |  |
| name | String |  |
| createdAt | Instant |  |
| updatedAt | Instant |  |

### UserPreferences
**Collection:** lp_preferences

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
| userId | String | Indexed |
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
| id | String | Id |
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
| id | String | Id |
| roomId | String | Indexed |
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
| expiresAt | Instant | Indexed(expireAfter = "PT0S") |
| isEncrypted | boolean |  |
| replyToId | String |  |

### UserPresence
**Collection:** 

| Field | Type | Annotations |
|-------|------|-------------|
| userId | String | Id |
| status | PresenceStatus |  |
| lastSeen | LocalDateTime |  |

### BillingAddressModel
**Collection:** portal_billing_address

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
| ambassadorId | String | Indexed |
| taskId | String |  |
| taskType | String |  |
| progressValue | int |  |
| completed | boolean |  |
| startedAt | Instant |  |
| completedAt | Instant |  |
| lastResetAt | Instant |  |
| rewardedAt | Instant |  |

### BadgeDefinition
**Collection:** 

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| title | String |  |
| description | String |  |
| level | String |  |
| svgUrl | String |  |
| createdAt | Instant |  |
| tags | List<String> |  |

### CourseCertificateModel
**Collection:** course_certificates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
| stripeCouponId | String |  |
| userId | String |  |
| publicity | boolean |  |
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
| id | String | Id |
| employeeId | String |  |
| moduleId | String |  |
| courseId | String |  |
| reminderType | String |  |
| sentTime | Instant |  |

### EmpCoursesModel
**Collection:** portal_emp_courses

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String |  |
| employeeName | String |  |
| email | String |  |
| phone | String |  |
| timezone | String |  |
| courses | List<CourseEnrollment> | Field("courses") |
| recordedCourses | List<RecordedCourseEnrollment> | Field("recordedCourses") |

### GamificationTaskModel
**Collection:** gamification_tasks

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| title | String |  |
| description | String |  |
| type | String |  |
| level | String |  |
| targetValue | int |  |
| rewardType | String |  |
| rewardId | String |  |
| rewardMetadata | Map<String, Object> |  |
| recurring | boolean |  |
| frequencyInDays | int |  |
| createdAt | Instant |  |
| groupKey | String |  |

### QuizAttempt
**Collection:** quiz_attempts

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
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
| id | String | Id |
| companyId | String |  |
| jobId | String |  |

### PreOrderModel
**Collection:** portal_preorder

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| name | String |  |
| email | String |  |
| product | String |  |
| date | String |  |

### Referral
**Collection:** referrals

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| referrerId | String | Indexed |
| referredUserId | String | Indexed |
| type | ReferralType |  |
| status | ReferralStatus |  |
| rewardIssued | boolean |  |
| createdAt | Instant |  |
| completedAt | Instant |  |

### ReferralCode
**Collection:** referral_codes

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| code | String | Indexed(unique = true) |
| userId | String | Indexed |
| createdAt | Instant |  |

### ReferralCommission
**Collection:** referral_commissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| referrerId | String | Indexed |
| referredCreatorId | String | Indexed |
| percentage | double |  |
| expiryDate | Instant |  |
| createdAt | Instant |  |

### ReputationEvent
**Collection:** reputation_events

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed |
| sourceType | ReputationSourceType |  |
| sourceId | String |  |
| delta | int |  |
| createdAt | LocalDateTime |  |

### UserBadge
**Collection:** user_badges

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed |
| badgeType | BadgeType |  |
| awardedAt | LocalDateTime |  |

### UserReputation
**Collection:** user_reputation

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed(unique = true) |
| totalScore | long |  |
| articleScore | long |  |
| communityScore | long |  |
| announcementScore | long |  |
| lastUpdated | LocalDateTime |  |

### ResumeModel
**Collection:** resumes

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String | Indexed |
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
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |
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
| id | String | Id |
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
| id | String | Id |
| plan | ESubscriptionPlan | Indexed |
| featureKey | String | Indexed |
| enabled | boolean |  |

### Subscription
**Collection:** subscriptions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| userId | String | Indexed(unique = true) |
| plan | ESubscriptionPlan | Indexed |
| status | ESubscriptionStatus | Indexed |
| startDate | Instant |  |
| endDate | Instant |  |
| stripeCustomerId | String | Indexed(sparse = true) |
| stripeSubscriptionId | String | Indexed(sparse = true) |
| stripePriceId | String |  |
| billingCycle | String |  |
| trialEndDate | Instant |  |
| cancelledAt | Instant |  |
| lastPaymentAt | Instant |  |
| lastCreditResetAt | Instant |  |
| createdAt | Instant | CreatedDate |
| updatedAt | Instant | LastModifiedDate |

### SupportRequestModel
**Collection:** support_requests

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| name | String |  |
| email | String |  |
| service | String |  |
| message | String |  |
| status | String |  |
| createdAt | Instant | CreatedDate |

### TrackingEvent
**Collection:** events

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| trackingId | String |  |
| eventType | String |  |
| url | String |  |
| referrer | String |  |
| sessionId | String | Indexed |
| userId | String |  |
| timestamp | Instant | Indexed |
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
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s") |

### EmpCertificatesModel
**Collection:** portal_emp_certificates

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String |  |
| certificates | List<EmpCertificatesDTO> | Field("certificates") |

### EmpContactModel
**Collection:** portal_emp_contact

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String |  |
| contact | List<EmpContactDTO> | Field("contact") |
| socialLinks | List<SocialLinksDTO> | Field("social_links") |
| publicity | boolean |  |

### EmpEducationModel
**Collection:** portal_emp_education

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String |  |
| education | List<EmpEducationDTO> | Field("education") |

### EmpExperiencesModel
**Collection:** portal_emp_experiences

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String |  |
| experiences | List<EmpExperiencesDTO> | Field("experiences") |

### EmpFollowersModel
**Collection:** portal_emp_followers

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String |  |
| followers | List<EmpFollowersDTO> | Field("followers") |

### EmpFollowingModel
**Collection:** portal_emp_following

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String |  |
| followings | List<EmpFollowingDTO> | Field("followings") |

### EmployeeModel
**Collection:** portal_employees

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| firstname | String |  |
| lastname | String |  |
| occupation | String | Indexed(name = "occupation_1") |
| image | String |  |
| coverImage | String |  |
| dob | String |  |
| email | String | Indexed(unique = true, name = "email_1") |
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
| savedJobs | List<FavJobDTO> | Field("savedJobs") |
| savedPosts | List<String> |  |
| accountNotifications | Object |  |
| marketingNotifications | Object |  |
| profileCompleted | Object |  |
| profileStatus | String |  |
| companyId | String |  |
| expectedSalaryRange | String |  |
| currentExperience | String |  |
| keywords | String |  |
| messagingPublicKey | String |  |

### EmpProjectsModel
**Collection:** portal_emp_projects

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String |  |
| projects | List<EmpProjectsDTO> | Field("projects") |

### EmpSkillsModel
**Collection:** portal_emp_skills

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String |  |
| skills | List<EmpSkillsDTO> | Field("skills") |

### TrainerProfile
**Collection:** trainer_profiles

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| employeeId | String | Indexed(unique = true) |
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

### WorkspaceModel
**Collection:** workspaces

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
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
| id | String | Id |
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
| expiresAt | Instant | Indexed(name = "expireAtIndex", expireAfter = "0s") |

### WhitelistDomains
**Collection:** portal_whitelist_domains

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| domain | String |  |
| active | boolean |  |
| requestBy | String |  |

### ContactSubmission
**Collection:** contact_form_submissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| name | String |  |
| email | String |  |
| subject | String |  |
| message | String |  |

### CtaLeadSubmission
**Collection:** cta_lead_submissions

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| name | String |  |
| email | String |  |
| serviceType | String |  |
| ctaType | String |  |
| focusArea | String |  |
| message | String |  |

### NewsLatterModel
**Collection:** portal_news_letter

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| email | String |  |

### TokenModel
**Collection:** portal_tokens

| Field | Type | Annotations |
|-------|------|-------------|
| id | String | Id |
| token | String |  |
| username | String |  |
| expiration | Date |  |
| used | boolean |  |

