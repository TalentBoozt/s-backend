package com.talentboozt.s_backend.domains.reputation.service;

import com.talentboozt.s_backend.domains.reputation.model.*;
import com.talentboozt.s_backend.domains.reputation.repository.mongodb.*;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.repository.mongodb.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReputationService {
    private final UserReputationRepository userReputationRepository;
    private final ReputationEventRepository reputationEventRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final ReputationScoringStrategy scoringStrategy;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmployeeRepository employeeRepository;

    private static final String LEADERBOARD_KEY = "leaderboard:global";

    public UserReputation applyEvent(String userId, ReputationSourceType type, String sourceId) {
        int delta = scoringStrategy.getScoreFor(type);

        ReputationEvent event = ReputationEvent.builder()
                .userId(userId)
                .sourceType(type)
                .sourceId(sourceId)
                .delta(delta)
                .createdAt(LocalDateTime.now())
                .build();
        reputationEventRepository.save(event);

        UserReputation reputation = userReputationRepository.findByUserId(userId)
                .orElse(UserReputation.builder()
                        .userId(userId)
                        .totalScore(0)
                        .articleScore(0)
                        .communityScore(0)
                        .announcementScore(0)
                        .build());

        reputation.setTotalScore(reputation.getTotalScore() + delta);
        updateCategoryScore(reputation, type, delta);
        reputation.setLastUpdated(LocalDateTime.now());

        UserReputation saved = userReputationRepository.save(reputation);

        // Update Leaderboard in Redis
        redisTemplate.opsForZSet().add(LEADERBOARD_KEY, userId, saved.getTotalScore());

        checkBadges(userId, saved.getTotalScore());

        return saved;
    }

    private void updateCategoryScore(UserReputation reputation, ReputationSourceType type, int delta) {
        switch (type) {
            case ARTICLE_PUBLISH:
            case ARTICLE_LIKE:
            case ARTICLE_BOOKMARK:
                reputation.setArticleScore(reputation.getArticleScore() + delta);
                break;
            case POST_CREATE:
            case POST_UPVOTE:
            case COMMENT_CREATE:
            case COMMENT_UPVOTE:
            case REACTION_RECEIVE:
                reputation.setCommunityScore(reputation.getCommunityScore() + delta);
                break;
            default:
                break;
        }
    }

    private void checkBadges(String userId, long totalScore) {
        List<UserBadge> currentBadges = userBadgeRepository.findByUserId(userId);

        Arrays.stream(BadgeType.values())
                .filter(badgeType -> totalScore >= badgeType.getThreshold())
                .filter(badgeType -> currentBadges.stream().noneMatch(b -> b.getBadgeType() == badgeType))
                .forEach(badgeType -> {
                    UserBadge badge = UserBadge.builder()
                            .userId(userId)
                            .badgeType(badgeType)
                            .awardedAt(LocalDateTime.now())
                            .build();
                    userBadgeRepository.save(badge);
                });
    }

    public List<com.talentboozt.s_backend.domains.reputation.dto.LeaderboardEntry> getLeaderboard(int limit) {
        var typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(LEADERBOARD_KEY, 0, limit - 1);

        if (typedTuples == null || typedTuples.isEmpty())
            return java.util.Collections.emptyList();

        List<String> userIds = typedTuples.stream()
                .map(org.springframework.data.redis.core.ZSetOperations.TypedTuple::getValue)
                .toList();

        // Batch fetch users and reputations
        List<EmployeeModel> employees = employeeRepository.findAllById(userIds);
        List<UserReputation> reputations = userReputationRepository.findByUserIdIn(userIds);

        // Create lookup maps
        java.util.Map<String, EmployeeModel> employeeMap = employees.stream()
                .collect(java.util.stream.Collectors.toMap(EmployeeModel::getId, e -> e));
        java.util.Map<String, UserReputation> reputationMap = reputations.stream()
                .collect(java.util.stream.Collectors.toMap(UserReputation::getUserId, r -> r));

        java.util.concurrent.atomic.AtomicInteger rankCounter = new java.util.concurrent.atomic.AtomicInteger(1);
        return typedTuples.stream()
                .map(tuple -> {
                    String userId = tuple.getValue();
                    long totalScore = tuple.getScore() != null ? tuple.getScore().longValue() : 0;
                    int rank = rankCounter.getAndIncrement();

                    EmployeeModel employee = employeeMap.get(userId);
                    UserReputation reputation = reputationMap.get(userId);

                    String name = "Unknown User";
                    String avatar = null;
                    if (employee != null) {
                        name = employee.getFirstname() + " " + employee.getLastname();
                        avatar = employee.getImage();
                    }

                    long articleScore = (reputation != null) ? reputation.getArticleScore() : 0;
                    long communityScore = (reputation != null) ? reputation.getCommunityScore() : 0;

                    return com.talentboozt.s_backend.domains.reputation.dto.LeaderboardEntry.builder()
                            .userId(userId)
                            .totalScore(totalScore)
                            .articleScore(articleScore)
                            .communityScore(communityScore)
                            .rank(rank)
                            .name(name)
                            .avatar(avatar)
                            .build();
                })
                .toList();
    }

    public UserReputation getUserReputation(String userId) {
        return userReputationRepository.findByUserId(userId).orElse(null);
    }
}
