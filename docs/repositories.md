# Repository Analysis

| Repository | Entity | Custom Queries |
|------------|--------|----------------|
| AIQuotaRepository | AIQuota | findByUserId |
| AIUsageRepository | AIUsage | findByUserId, findByUserId |
| CreditRepository | CreditRecord | findById |
| AmbassadorLeaderboardRepository | AmbassadorLeaderboardModel | findByTypeOrderByRankAsc |
| AmbassadorPointAuditRepository | AmbassadorPointAudit | findByAmbassadorId |
| AmbassadorProfileRepository | AmbassadorProfileModel | findByEmployeeId, findByEmail |
| AmbassadorRewardRepository | AmbassadorRewardModel | findAllByAmbassadorId, existsByAmbassadorIdAndTaskId |
| AmbassadorSessionRepository | AmbassadorSessionModel | findByAmbassadorId, countByAmbassadorIdAndType |
| AmbReferralRepository | AmbReferralModel | findAllByReferralCode, findAllByAmbassadorId, countByAmbassadorId |
| BadgeRepository | BadgeModel | existsByAmbassadorIdAndBadgeId |
| SwagRepository | SwagModel | existsByAmbassadorIdAndTaskId |
| AnnouncementRepository | Announcement | findBySlug, findActiveAnnouncements, findByStatus, findByType, findByPriority |
| ArticleEvaluationLogRepository | ArticleEvaluationLog | findFirstByArticleIdOrderByEvaluatedAtDesc |
| ArticleRepository | Article | findBySlug, findByStatus, findFeaturedArticles, findByTagIdsContainingAndStatus, searchArticles... |
| TagRepository | Tag | findByName, findBySlug |
| AsyncUpdateAuditLogRepository | AsyncUpdateAuditLog | findByStatus, findTopByOrderByCreatedAtDesc |
| ClientActAuditLogRepository | ClientActAuditLog |  |
| CourseReminderAuditLogRepository | CourseReminderAuditLog | findTopByOrderByTimestampDesc |
| LeadOSAuditLogRepository | LeadOSAuditLog | findByWorkspaceIdOrderByTimestampDesc |
| SchedulerLogRepository | SchedulerLogModel | findByJobNameOrderByRunAtDesc |
| StripeAuditLogRepository | StripeAuditLog | findTop20ByStatusAndRetryCountLessThanOrderByCreatedAtAsc |
| TaskRewardAuditRepository | TaskRewardAuditModel | findByAmbassadorIdOrderByIssuedAtDesc, findByTaskIdOrderByIssuedAtDesc |
| CredentialsRepository | CredentialsModel | findByEmail, findByEmployeeId, findByCompanyId, deleteByEmployeeId, existsByEmail... |
| CredentialsRepositoryCustom | UNKNOWN | findUsersByFilters |
| PasswordResetTokenRepository | PasswordResetTokenModel | findByToken |
| PermissionRepository | PermissionModel | findByName, existsByName, count |
| RoleRepository | RoleModel | findByName, existsByName, count |
| FeatureRepository | FeatureModel |  |
| IssueRepository | IssueModel |  |
| LoginRepository | Login | findByUserId, countDistinctUserIdByEventDate, countByPlatform, aggregateLoginLocations |
| SystemNotificationsRepository | SystemNotificationsModel | findByActive |
| ActivityRepository | Activity | findByUserIdOrderByTimestampDesc |
| CommentRepository | Comment | findByPostId, countByPostIdIn, findByParentId |
| CommunityMemberRepository | CommunityMember | findByCommunityId, findByUserId, findByCommunityIdAndUserId, countByCommunityId, existsByCommunityIdAndUserId... |
| CommunityRepository | Community |  |
| NotificationRepository | Notification | findByRecipientIdOrderByTimestampDesc, findByRecipientId, countByRecipientIdAndIsReadFalse |
| PostRepository | Post | findByCommunityId, countByCommunityId, findByCommunityId, findByAuthorId, findByAuthorId... |
| ReportRepository | Report |  |
| CourseBatchRepository | CourseBatchModel | findByCourseId, findTopByCourseIdOrderByStartDateDesc, findTopByCourseIdOrderByStartDateDescIdDesc, existsByCourseId |
| CourseRepository | CourseModel | findByCompanyId |
| RecordedCourseRepository | RecordedCourseModel | findByPublishedTrue, findByPublishedTrueAndApprovedTrue, findByCompanyId, findByTrainerId |
| CmpPostedJobsRepository | CmpPostedJobsModel | findByCompanyId |
| CmpSocialRepository | CmpSocialModel | findByCompanyId, deleteByCompanyId |
| CompanyRepository | CompanyModel |  |
| StandaloneFileRepository | StandaloneFileModel | findByParentIdAndOwnerId, findByOwnerId, deleteByParentId |
| EAffiliateCommissionsRepository | EAffiliateCommissions | findByAffiliateId, findByTransactionId |
| EAffiliateLinksRepository | EAffiliateLinks | findByTrackingCode, findByAffiliateId, findByAffiliateIdAndCourseId |
| EAffiliatesRepository | EAffiliates | findByReferralCode, findByUserId |
| EAiCreditsRepository | EAiCredits | findByUserId, findByExpiresAtBeforeAndBalanceGreaterThan |
| EAiUsageRepository | EAiUsage | findByUserId, countByUserIdAndCreatedAtGreaterThanEqual |
| EAnalyticsEventsRepository | EAnalyticsEvents | findByCourseId, findByUserId, findByUserIdOrderByTimestampDesc, countByType |
| EApiKeyRepository | EApiKey | findByApiKey, findByOwnerId |
| EAssignmentsRepository | EAssignments | findByLessonId |
| EAssignmentSubmissionsRepository | EAssignmentSubmissions | findByAssignmentIdAndUserId, findByAssignmentIdInAndUserId |
| EAuditLogRepository | EAuditLog | findByActorIdOrderByCreatedAtDesc, findByTargetIdOrderByCreatedAtDesc |
| EBundlesRepository | EBundles | findByCreatorId, findByCreatorIdAndStatus |
| ECertificatesRepository | ECertificates | findByCertificateId, findByUserId |
| ECouponRedemptionRepository | ECouponRedemption | findByUserId, findByCouponId, countByUserIdAndCouponId, findByUserIdAndCouponId |
| ECouponsRepository | ECoupons | findByCreatorId, findByCreatorIdAndIsActiveTrue, findByCode |
| ECourseSectionsRepository | ECourseSections | findByCourseId |
| ECoursesRepository | ECourses | findByCreatorId, findByWorkspaceId, findByStatus, findByValidationStatusIn, findByPublishedTrueAndIsPrivateFalseAndStatus |
| ECreatorFinanceSettingsRepository | ECreatorFinanceSettings | findByUserId |
| ECreditLedgerRepository | ECreditLedger | findByUserIdOrderByCreatedAtDesc |
| EduCourseReviewLogRepository | EduCourseReviewLog | findByCourseId |
| EEnrollmentsRepository | EEnrollments | findByUserIdAndCourseId, findByUserId, findByCourseId |
| EFraudFlagRepository | EFraudFlag | findByTargetUserId, findByStatus, findByFlagType |
| EGiftsRepository | EGifts | findByRedeemCode |
| EHoldingLedgerRepository | EHoldingLedger | findByBeneficiaryIdOrderByCreatedAtDesc, findByBeneficiaryIdAndStatus, findByTransactionId, findByStatusAndClearanceDateBefore |
| ELearningPathsRepository | ELearningPaths | findByWorkspaceId |
| ELedgerEntryRepository | ELedgerEntry | event, findByEventReference, event, existsByEventReference, account... |
| ELessonsRepository | ELessons | findByCourseId, findBySectionId, countByCourseId |
| ENotificationsRepository | ENotifications | findByUserIdOrderByCreatedAtDesc, findByUserIdAndIsReadFalse, countByUserIdAndIsReadFalse |
| EPayoutScheduleRepository | EPayoutSchedule | findByCreatorId, findByActiveTrueAndNextScheduledAtBefore |
| EPayoutsRepository | EPayouts | findByCreatorId, countByCreatorIdAndRequestedAtAfter |
| EProfilesRepository | EProfiles | findByUserId, deleteByUserId |
| EQuizAttemptsRepository | EQuizAttempts | findByQuizIdAndUserId |
| EQuizzesRepository | EQuizzes | findByLessonId |
| ERefundRepository | ERefund | findByTransactionId, ID, findByStripeRefundId, existsByStripeRefundId, findByBuyerId... |
| EReportsRepository | EReports | findByStatus |
| EReviewsRepository | EReviews | findByCourseId, findByUserId, findByCourseIdIn, existsByUserIdAndCourseId |
| ESubscriptionsRepository | ESubscriptions | findByUserId, findByStripeCustomerId |
| ESystemSettingsRepository | ESystemSettings | findByCategory |
| ETransactionsRepository | ETransactions | findBySellerId, findByBuyerId, findByCourseId, findByStripeCheckoutSessionId, findAllByStripeCheckoutSessionId... |
| ETrustScoresRepository | ETrustScores | findByCreatorId |
| EUserPreferencesRepository | EUserPreferences | findByUserId |
| EUserRepository | EUser | findByEmail, findByEmailVerificationToken, findByPasswordResetToken, findAllByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase |
| EValidationReportsRepository | EValidationReports | findByCourseId, findFirstByCourseIdOrderByCreatedAtDesc |
| EWalletRepository | EWallet | findByUserId |
| EWalletTransactionRepository | EWalletTransaction | findByUserId |
| EWebhookEventRepository | EWebhookEvent | existsByStripeEventId, ID, findByStripeEventId, retry, findByStatusAndNextRetryAtBefore... |
| EWorkspaceMembersRepository | EWorkspaceMembers | findByWorkspaceId, findByUserId, findByWorkspaceIdAndUserId |
| EWorkspacesRepository | EWorkspaces | findByOwnerId, findAllByNameContainingIgnoreCaseOrDomainContainingIgnoreCase |
| AnalyticsRepository | AnalyticsData | findByOrganizationIdAndProjectIdAndScenarioIdAndMetricAndGranularity, findByOrganizationIdAndProjectIdAndScenarioIdAndMetricAndGranularityAndPeriod, deleteByProjectIdAndScenarioId |
| MetricDefinitionRepository | MetricDefinition | findByOrganizationId, findByOrganizationIdAndKey |
| FinAiTrainingSnapshotRepository | FinAiTrainingSnapshot | findByOrganizationIdAndProjectId |
| FinAssumptionRepository | FinAssumption | findByOrganizationIdAndProjectId, findByOrganizationIdAndProjectIdAndKey |
| FinAuditLogRepository | FinAuditLog | findByProjectIdOrderByTimestampDesc, findByOrganizationIdOrderByTimestampDesc, findByOrganizationIdAndProjectId |
| FinBudgetRepository | FinBudget | findByOrganizationIdAndProjectId, findByOrganizationIdAndProjectId, findByOrganizationIdAndProjectIdAndCategory |
| FinFinancialSnapshotRepository | FinFinancialSnapshot | findByOrganizationId, findByOrganizationIdAndProjectId, findByOrganizationIdAndProjectIdAndScenarioId, findByOrganizationIdAndProjectIdAndMonth, findByOrganizationIdAndProjectIdAndScenarioIdAndMonth |
| FinPricingModelRepository | FinPricingModel | findByOrganizationIdAndProjectId, findByOrganizationIdAndProjectIdAndTier |
| FinProjectMemberRepository | FinProjectMember | findByProjectId, findByProjectIdAndUserId |
| FinProjectRepository | FinProject | findByOrganizationId, findByOrganizationIdAndId, countByOrganizationId |
| FinSalesPlanRepository | FinSalesPlan | findByOrganizationIdAndProjectId, findByOrganizationIdAndProjectId, findByOrganizationIdAndProjectIdAndMonth |
| FinScenarioRepository | FinScenario | findByOrganizationIdAndProjectId |
| FinWorkspaceRepository | FinWorkspace | findByMemberIdsContaining, findBySlug |
| ScenarioOverrideRepository | ScenarioOverride | findByScenarioId |
| ScenarioRepository | Scenario | findByProjectId, findByOrganizationIdAndProjectId |
| ApplicantRepository | ApplicantModel | findByJobId, findByCompanyId, findByCandidateId |
| JobPostRepository | JobPostModel | findByCompanyId, findByStatus |
| LAutomationRepository | LAutomation | findByWorkspaceId |
| LLeadAutomationRepository | LLeadAutomation | findByWorkspaceIdAndActiveTrue, findByWorkspaceId |
| LCampaignRepository | LCampaign | findByWorkspaceId |
| LLeadRepository | LLead | findByFilters, findByWorkspaceId, findBySourceSignalId |
| LLeadCandidateRepository | LLeadCandidate | findByWorkspaceId |
| LLeadSourceRepository | LLeadSource | findByWorkspaceId, findByPlatformAndActive |
| LLeadWorkspaceRepository | LLeadWorkspace | findByOwnerId |
| LNotificationRepository | LNotification | findByWorkspaceId, findByWorkspaceIdOrderByCreatedAtDesc, countByWorkspaceIdAndReadFalse |
| LRawSignalRepository | LRawSignal | findByWorkspaceId, findBySourceId, existsByPlatformId |
| LTaskRepository | LTask | findByWorkspaceIdOrderByCreatedAtDesc, findByWorkspaceIdAndStatusIn |
| LTemplateRepository | LTemplate | findByWorkspaceId, findByWorkspaceIdOrderByUsageCountDesc |
| AICacheEntryRepository | AICacheEntry | findByCacheKey |
| UserCreditsRepository | UserCredits | findByUserId, countByTier |
| GoalRepository | Goal | findByUserId |
| JournalEntryRepository | JournalEntry | findByUserIdAndDate, findByUserIdOrderByDateDesc |
| MoodEntryRepository | MoodEntry | findByUserIdAndDate, findByUserIdAndDateBetweenOrderByDateAsc |
| WeeklyMoodSummaryRepository | WeeklyMoodSummary | findByUserIdAndWeekStartDate, findByUserIdOrderByWeekStartDateDesc |
| LPNotificationRepository | LPNotification | findByUserIdOrderByCreatedAtDesc, countByUserIdAndIsReadFalse |
| DailyScheduleRepository | DailySchedule | findByUserIdAndScheduleDate, findByPlanId, deleteByPlanId, findByUserId |
| StudyPlanRepository | StudyPlan | findByGoalId, deleteByGoalId, findByUserId, countByStatus, findByStatus |
| UserPreferencesRepository | UserPreferences | findByUserId |
| UserProfileRepository | UserProfile | findByUserId |
| UserRepository | User | findByEmail |
| ChatRoomRepository | ChatRoom | findByParticipantsContaining, findByCommunityId, findByTypeAndParticipantsAllIgnoreCase |
| MessageRepository | Message | findByRoomIdAndDeletedForUsersNotContaining, countUnreadMessages, findLatestActiveMessage, findUnreadMessagesInRoom |
| UserPresenceRepository | UserPresence |  |
| BillingAddressRepository | BillingAddressModel | findByCompanyId |
| BillingHistoryRepository | BillingHistoryModel | findByCompanyId, existsBySessionId |
| InvoiceRepository | InvoicesModel | findByCompanyId, findByInvoiceId, existsBySessionId |
| PaymentMethodRepository | PaymentMethodsModel | findByCompanyId, existsBySessionId |
| PaymentSubscriptionRepository | PaymentSubscriptionsModel | findByCompanyId, findBySubscriptionId |
| PrePaymentRepository | PrePaymentModel | findByCompanyId, findBySubscriptionId, findByPaymentMethodId, findByInvoiceId, findAllByPayType... |
| RecordedCoursePaymentRepository | RecordedCoursePayment | findByTrainerId, findByCourseId, findSuccessfulPaymentsByTrainer |
| UsageDataRepository | UsageDataModel | findByCompanyId |
| BadgeDefinitionRepository | BadgeDefinition |  |
| CourseCertificateRepository | CourseCertificateModel | findByCertificateId |
| CourseCouponsRepository | CourseCouponsModel | findByToken, findByUserId, findByStatus, findByTaskIdAndStatus, existsByUserIdAndTaskId... |
| CourseReminderLogRepository | CourseReminderLog | existsByEmployeeIdAndModuleIdAndReminderType |
| EmpCoursesRepository | EmpCoursesModel | findAllByEmployeeId, deleteByEmployeeId, findByCoursesCourseId, findByCoursesCourseIdAndCoursesBatchId, findByEmployeeId |
| GamificationTaskRepository | GamificationTaskModel | findByTypeOrderByPriorityDesc |
| QuizAttemptRepository | QuizAttempt | findByEmployeeIdAndQuizId, findByQuizIdOrderByScoreDesc, findByCourseId |
| SwagItemRepository | SwagItem | findByEnabled |
| TaskProgressRepository | AmbassadorTaskProgressModel | findByAmbassadorIdAndTaskId, findByTaskType |
| InterviewQuestionRepository | InterviewQuestionModel | findByQuestions_Id, findByQuestions_Answers_Id |
| JobApplyRepository | JobApplyModel | findByJobId, findByEmployeeIdInApplicants |
| PreOrderRepository | PreOrderModel |  |
| ReferralCodeRepository | ReferralCode | findByCode, findByUserId |
| ReferralCommissionRepository | ReferralCommission | findByReferredCreatorId |
| ReferralRepository | Referral | findByReferredUserId, findAllByReferrerId, existsByReferrerIdAndReferredUserId |
| ReputationEventRepository | ReputationEvent | findByUserId |
| UserBadgeRepository | UserBadge | findByUserId |
| UserReputationRepository | UserReputation | findByUserId, findByUserIdIn |
| ResumeRepository | ResumeModel | findByEmployeeIdAndDeletedFalseOrderByUpdatedAtDesc, employee, findByIdAndEmployeeIdAndDeletedFalse, existsByIdAndEmployeeIdAndDeletedFalse, countByEmployeeIdAndDeletedFalse |
| SoftwareAppRepository | SoftwareAppModel | findByCompanyId, findByIsGlobalTrue |
| FeatureFlagRepository | FeatureFlag | findByPlanAndFeatureKey, findAllByPlan |
| SubscriptionRepository | Subscription | findByUserId, findByStripeSubscriptionId, findByStripeCustomerId |
| SupportRequestRepository | SupportRequestModel |  |
| TrackingEventRepository | TrackingEvent | findByTrackingId, findByTrackingIdAndTimestampBetween, findByTrackingIdAndEventTypeAndTimestampBetween, findTopByOrderByTimestampDesc, findTopByOrderByTimestampAsc... |
| EmpCertificatesRepository | EmpCertificatesModel | findAllByEmployeeId, findByEmployeeId, deleteByEmployeeId |
| EmpContactRepository | EmpContactModel | findByEmployeeId, deleteByEmployeeId |
| EmpEducationRepository | EmpEducationModel | findByEmployeeId, deleteByEmployeeId |
| EmpExperiencesRepository | EmpExperiencesModel | findByEmployeeId, deleteByEmployeeId |
| EmpFollowersRepository | EmpFollowersModel | findByEmployeeId, deleteByEmployeeId |
| EmpFollowingRepository | EmpFollowingModel | findByEmployeeId, deleteByEmployeeId |
| EmployeeRepository | EmployeeModel | findByEmail, existsByEmail, findAllBy, findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCaseOrEmailContainingIgnoreCase, findByIdNotIn |
| EmpProjectsRepository | EmpProjectsModel | findByEmployeeId, deleteByEmployeeId |
| EmpSkillsRepository | EmpSkillsModel | findByEmployeeId, deleteByEmployeeId |
| TrainerProfileRepository | TrainerProfile | findByEmployeeId |
| WorkspaceRepository | WorkspaceModel | findByOwnerId, findByMemberIdsContaining |
| UserActivityRepository | UserActivity | countActiveUsers, findRecentActivity, countByLastActiveAfter |
| WhitelistDomainsRepository | WhitelistDomains | findByActive, findByDomain, findByDomainAndActive, findByRequestBy |
| ContactSubmissionRepository | ContactSubmission |  |
| CtaLeadRepository | CtaLeadSubmission |  |
| NewsLetterRepository | NewsLatterModel | findByEmail |
| TokenRepository | TokenModel | findByToken |
