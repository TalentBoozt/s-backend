package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAiUsageRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
@Service
public class EduAccessGuardService {

    private final PlanConfigService planConfigService;
    private final EUserRepository userRepository;
    private final ECoursesRepository courseRepository;
    private final EAiUsageRepository aiUsageRepository;
    private final StringRedisTemplate redisTemplate;
    private final com.talentboozt.s_backend.domains.subscription.service.FeatureFlagService featureFlagService;

    public EduAccessGuardService(PlanConfigService planConfigService, EUserRepository userRepository,
            ECoursesRepository courseRepository, EAiUsageRepository aiUsageRepository,
            StringRedisTemplate redisTemplate,
            com.talentboozt.s_backend.domains.subscription.service.FeatureFlagService featureFlagService) {
        this.planConfigService = planConfigService;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.aiUsageRepository = aiUsageRepository;
        this.redisTemplate = redisTemplate;
        this.featureFlagService = featureFlagService;
    }

    public EUser getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
                });
    }

    // 1. Enforce Course Creation Limits
    public void enforceCourseCreationLimit(String userId) {
        EUser user = getUser(userId);
        int maxCourses = planConfigService.getPlanLimits(user.getPlan()).getMaxCourses();
        long currentCount = courseRepository.findByCreatorId(userId).size();

        if (currentCount >= maxCourses) {
            log.warn("Course creation limit reached for user: {} (count: {}, max: {})", userId, currentCount, maxCourses);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Course creation limit reached for your plan. Please upgrade.");
        }
    }

    // 2. Enforce Ownership
    public void enforceCourseOwnership(String userId, String courseId) {
        ECourses course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    log.warn("Course not found: {} (requested by user: {})", courseId, userId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
                });

        if (!userId.equals(course.getCreatorId())) {
            log.warn("Unauthorized course access attempt: userId={}, courseId={}, ownerId={}", userId, courseId, course.getCreatorId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not have permission to modify this course.");
        }
    }

    // 3. Enforce Feature Access
    public void enforceFeatureAccess(String userId, String feature) {
        if (!featureFlagService.isFeatureEnabled(userId, feature)) {
            log.warn("Feature access denied: userId={}, feature={}", userId, feature);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Feature requires plan upgrade: " + feature);
        }
    }

    // 4. Enforce AI Generation Limits (Monthly + Duplicate Blocker)
    public void enforceAIGenerationLimits(String userId, String promptHash) {
        if (promptHash != null) {
            String key = "edu:ai:lock:" + userId + ":" + promptHash.hashCode();
            // Lock for 30 seconds to prevent credit-wasting duplicate requests
            Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "locked", Duration.ofSeconds(30));
            if (Boolean.FALSE.equals(success)) {
                log.warn("Duplicate AI generation request blocked: userId={}", userId);
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "Duplicate request detected. Please wait 30 seconds before retrying the same prompt.");
            }
        }

        EUser user = getUser(userId);
        int maxGenerations = planConfigService.getPlanLimits(user.getPlan()).getMaxAiGenerationsPerMonth();

        Instant startOfMonth = ZonedDateTime.now(ZoneId.of("UTC")).withDayOfMonth(1).withHour(0).toInstant();
        long monthlyUsageCount = aiUsageRepository.countByUserIdAndCreatedAtGreaterThanEqual(userId, startOfMonth);

        if (monthlyUsageCount >= maxGenerations) {
            log.warn("Monthly AI generation limit reached for user: {} (count: {}, max: {})", userId, monthlyUsageCount, maxGenerations);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Monthly AI generation limit (" + maxGenerations + ") reached. Please upgrade your plan.");
        }
    }
}
