package com.talentboozt.s_backend.domains.portal.reputation.service;

import com.talentboozt.s_backend.domains.portal.reputation.dto.LeaderboardEntry;
import com.talentboozt.s_backend.domains.portal.reputation.model.*;
import com.talentboozt.s_backend.domains.portal.reputation.repository.mongodb.*;
import com.talentboozt.s_backend.domains.portal.user_profile.model.EmployeeModel;
import com.talentboozt.s_backend.domains.portal.user_profile.repository.mongodb.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReputationServiceTest {

    private UserReputationRepository userReputationRepository;
    private ReputationEventRepository reputationEventRepository;
    private UserBadgeRepository userBadgeRepository;
    private ReputationScoringStrategy scoringStrategy;
    private RedisTemplate<String, String> redisTemplate;
    private ZSetOperations<String, String> zSetOperations;
    private EmployeeRepository employeeRepository;

    private ReputationService reputationService;

    @BeforeEach
    void setUp() {
        userReputationRepository = mock(UserReputationRepository.class);
        reputationEventRepository = mock(ReputationEventRepository.class);
        userBadgeRepository = mock(UserBadgeRepository.class);
        scoringStrategy = mock(ReputationScoringStrategy.class);
        redisTemplate = mock(RedisTemplate.class);
        zSetOperations = mock(ZSetOperations.class);
        employeeRepository = mock(EmployeeRepository.class);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        reputationService = new ReputationService(
                userReputationRepository,
                reputationEventRepository,
                userBadgeRepository,
                scoringStrategy,
                redisTemplate,
                employeeRepository
        );
    }

    @Test
    void applyEvent_shouldCreateNewUserReputationWhenNoneExists() {
        // Arrange
        String userId = "user-1";
        ReputationSourceType type = ReputationSourceType.ARTICLE_PUBLISH;
        String sourceId = "art-1";

        when(scoringStrategy.getScoreFor(type)).thenReturn(100);
        when(userReputationRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userReputationRepository.save(any(UserReputation.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        UserReputation result = reputationService.applyEvent(userId, type, sourceId);

        // Assert
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTotalScore()).isEqualTo(100);
        assertThat(result.getArticleScore()).isEqualTo(100);
        assertThat(result.getCommunityScore()).isEqualTo(0);
        verify(userReputationRepository, times(1)).save(any(UserReputation.class));
    }

    @Test
    void applyEvent_shouldUpdateExistingUserReputationScores() {
        // Arrange
        String userId = "user-1";
        ReputationSourceType type = ReputationSourceType.COMMENT_CREATE;
        String sourceId = "comm-1";

        UserReputation existing = UserReputation.builder()
                .userId(userId)
                .totalScore(250)
                .articleScore(100)
                .communityScore(150)
                .build();

        when(scoringStrategy.getScoreFor(type)).thenReturn(50);
        when(userReputationRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(userReputationRepository.save(any(UserReputation.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        UserReputation result = reputationService.applyEvent(userId, type, sourceId);

        // Assert
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getTotalScore()).isEqualTo(300);
        assertThat(result.getArticleScore()).isEqualTo(100);
        assertThat(result.getCommunityScore()).isEqualTo(200);
        verify(userReputationRepository, times(1)).save(existing);
    }

    @Test
    void applyEvent_shouldAwardBadgeWhenThresholdIsMet() {
        // Arrange
        String userId = "user-1";
        ReputationSourceType type = ReputationSourceType.ARTICLE_PUBLISH;
        String sourceId = "art-1";

        // Score 500 triggers CONTRIBUTOR badge
        when(scoringStrategy.getScoreFor(type)).thenReturn(500);
        when(userReputationRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userReputationRepository.save(any(UserReputation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userBadgeRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        reputationService.applyEvent(userId, type, sourceId);

        // Assert
        ArgumentCaptor<UserBadge> badgeCaptor = ArgumentCaptor.forClass(UserBadge.class);
        verify(userBadgeRepository, times(1)).save(badgeCaptor.capture());
        UserBadge awarded = badgeCaptor.getValue();
        assertThat(awarded.getUserId()).isEqualTo(userId);
        assertThat(awarded.getBadgeType()).isEqualTo(BadgeType.CONTRIBUTOR);
    }

    @Test
    void applyEvent_shouldNotAwardDuplicateBadges() {
        // Arrange
        String userId = "user-1";
        ReputationSourceType type = ReputationSourceType.ARTICLE_PUBLISH;
        String sourceId = "art-1";

        when(scoringStrategy.getScoreFor(type)).thenReturn(500);
        when(userReputationRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userReputationRepository.save(any(UserReputation.class))).thenAnswer(inv -> inv.getArgument(0));

        UserBadge existingBadge = UserBadge.builder()
                .userId(userId)
                .badgeType(BadgeType.CONTRIBUTOR)
                .build();
        when(userBadgeRepository.findByUserId(userId)).thenReturn(List.of(existingBadge));

        // Act
        reputationService.applyEvent(userId, type, sourceId);

        // Assert - no new badge saved
        verify(userBadgeRepository, never()).save(any(UserBadge.class));
    }

    @Test
    void applyEvent_shouldSaveReputationEvent() {
        // Arrange
        String userId = "user-1";
        ReputationSourceType type = ReputationSourceType.ARTICLE_PUBLISH;
        String sourceId = "art-1";

        when(scoringStrategy.getScoreFor(type)).thenReturn(100);
        when(userReputationRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userReputationRepository.save(any(UserReputation.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        reputationService.applyEvent(userId, type, sourceId);

        // Assert
        ArgumentCaptor<ReputationEvent> eventCaptor = ArgumentCaptor.forClass(ReputationEvent.class);
        verify(reputationEventRepository, times(1)).save(eventCaptor.capture());
        ReputationEvent event = eventCaptor.getValue();
        assertThat(event.getUserId()).isEqualTo(userId);
        assertThat(event.getSourceType()).isEqualTo(type);
        assertThat(event.getSourceId()).isEqualTo(sourceId);
        assertThat(event.getDelta()).isEqualTo(100);
    }

    @Test
    void applyEvent_shouldUpdateLeaderboardInRedis() {
        // Arrange
        String userId = "user-1";
        ReputationSourceType type = ReputationSourceType.ARTICLE_PUBLISH;
        String sourceId = "art-1";

        when(scoringStrategy.getScoreFor(type)).thenReturn(150);
        when(userReputationRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userReputationRepository.save(any(UserReputation.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        reputationService.applyEvent(userId, type, sourceId);

        // Assert
        verify(zSetOperations, times(1)).add("leaderboard:global", userId, 150.0);
    }

    @Test
    void getLeaderboard_shouldReturnLeaderboardEntriesSortedAndMapped() {
        // Arrange
        ZSetOperations.TypedTuple<String> tuple1 = mock(ZSetOperations.TypedTuple.class);
        when(tuple1.getValue()).thenReturn("user-1");
        when(tuple1.getScore()).thenReturn(1000.0);

        ZSetOperations.TypedTuple<String> tuple2 = mock(ZSetOperations.TypedTuple.class);
        when(tuple2.getValue()).thenReturn("user-2");
        when(tuple2.getScore()).thenReturn(800.0);

        Set<ZSetOperations.TypedTuple<String>> tuples = new LinkedHashSet<>(List.of(tuple1, tuple2));
        when(zSetOperations.reverseRangeWithScores("leaderboard:global", 0, 9)).thenReturn(tuples);

        EmployeeModel emp1 = new EmployeeModel("user-1");
        emp1.setFirstname("John");
        emp1.setLastname("Doe");
        emp1.setImage("avatar-1");

        EmployeeModel emp2 = new EmployeeModel("user-2");
        emp2.setFirstname("Jane");
        emp2.setLastname("Smith");
        emp2.setImage("avatar-2");

        when(employeeRepository.findAllById(anyList())).thenReturn(List.of(emp1, emp2));

        UserReputation rep1 = UserReputation.builder().userId("user-1").articleScore(400).communityScore(600).build();
        UserReputation rep2 = UserReputation.builder().userId("user-2").articleScore(300).communityScore(500).build();
        when(userReputationRepository.findByUserIdIn(anyList())).thenReturn(List.of(rep1, rep2));

        // Act
        List<LeaderboardEntry> leaderboard = reputationService.getLeaderboard(10);

        // Assert
        assertThat(leaderboard).hasSize(2);
        LeaderboardEntry entry1 = leaderboard.get(0);
        assertThat(entry1.getUserId()).isEqualTo("user-1");
        assertThat(entry1.getTotalScore()).isEqualTo(1000);
        assertThat(entry1.getRank()).isEqualTo(1);
        assertThat(entry1.getName()).isEqualTo("John Doe");
        assertThat(entry1.getAvatar()).isEqualTo("avatar-1");
        assertThat(entry1.getArticleScore()).isEqualTo(400);
        assertThat(entry1.getCommunityScore()).isEqualTo(600);

        LeaderboardEntry entry2 = leaderboard.get(1);
        assertThat(entry2.getUserId()).isEqualTo("user-2");
        assertThat(entry2.getTotalScore()).isEqualTo(800);
        assertThat(entry2.getRank()).isEqualTo(2);
        assertThat(entry2.getName()).isEqualTo("Jane Smith");
        assertThat(entry2.getAvatar()).isEqualTo("avatar-2");
    }

    @Test
    void getLeaderboard_shouldReturnEmptyListWhenNoScoresInRedis() {
        // Arrange
        when(zSetOperations.reverseRangeWithScores("leaderboard:global", 0, 9)).thenReturn(Collections.emptySet());

        // Act
        List<LeaderboardEntry> leaderboard = reputationService.getLeaderboard(10);

        // Assert
        assertThat(leaderboard).isEmpty();
        verify(employeeRepository, never()).findAllById(anyList());
    }

    @Test
    void getUserReputation_shouldReturnNullWhenUserReputationNotFound() {
        // Arrange
        when(userReputationRepository.findByUserId("user-1")).thenReturn(Optional.empty());

        // Act
        UserReputation result = reputationService.getUserReputation("user-1");

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void getUserReputation_shouldReturnUserReputationWhenFound() {
        // Arrange
        UserReputation rep = UserReputation.builder().userId("user-1").totalScore(500).build();
        when(userReputationRepository.findByUserId("user-1")).thenReturn(Optional.of(rep));

        // Act
        UserReputation result = reputationService.getUserReputation("user-1");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalScore()).isEqualTo(500);
    }
}
