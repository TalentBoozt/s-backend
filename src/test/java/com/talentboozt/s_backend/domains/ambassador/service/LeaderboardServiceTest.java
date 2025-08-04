package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorLeaderboardModel;
import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorLeaderboardRepository;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.plat_courses.model.AmbassadorTaskProgressModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.TaskProgressRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private AmbassadorProfileRepository ambassadorRepo;

    @Mock
    private TaskProgressRepository progressRepo;

    @Mock
    private AmbassadorLeaderboardRepository leaderboardRepo;

    @InjectMocks
    private LeaderboardService leaderboardService;

    @Test
    void generateLeaderboards_createsLeaderboardsForAllTypes() {
        doNothing().when(leaderboardRepo).deleteAll(anyListOf());
        when(progressRepo.findByTaskType(anyString())).thenReturn(List.of());
//        when(ambassadorRepo.findById(anyString())).thenReturn(Optional.empty());

        leaderboardService.generateLeaderboards();

        verify(progressRepo, times(3)).findByTaskType(anyString());
        verify(leaderboardRepo, times(3)).deleteAll(anyListOf());
        verify(leaderboardRepo, times(3)).saveAll(anyListOf());
    }

    @Test
    void generateLeaderboardForType_savesLeaderboardWithCorrectRankings() {
        String type = "REFERRAL";
        List<AmbassadorTaskProgressModel> progresses = List.of(
                new AmbassadorTaskProgressModel("1", "amb1", "task1", type, 50, false, null, null, null, "NOT_ISSUED", false, null),
                new AmbassadorTaskProgressModel("2", "amb2", "task2", type, 30, false, null, null, null, "NOT_ISSUED", false, null),
                new AmbassadorTaskProgressModel("3", "amb3", "task3", type, 70, false, null, null, null, "NOT_ISSUED", false, null)
        );

        when(progressRepo.findByTaskType(type)).thenReturn(progresses);
        when(ambassadorRepo.findById("amb1")).thenReturn(Optional.of(new AmbassadorProfileModel("1", "John", "john@example.com", null, null, false, null, null, "amb1", "BRONZE", 50, 10, 2, 1, true, null, null, null, null, null, null, null, null, 250, null, 150, 100, null)));
        when(ambassadorRepo.findById("amb2")).thenReturn(Optional.of(new AmbassadorProfileModel("2", "Jane", "jane@example.com", null, null, false, null, null, "amb2", "SILVER", 30, 10, 2, 1, true, null, null, null, null, null, null, null, null, 250, null, 150, 100, null)));
        when(ambassadorRepo.findById("amb3")).thenReturn(Optional.of(new AmbassadorProfileModel("3", "Bob", "bob@example.com", null, null, false, null, null, "amb3", "GOLD", 70, 10, 2, 1, true, null, null, null, null, null, null, null, null, 250, null, 150, 100, null)));
//        doNothing().when(leaderboardRepo).deleteAll(anyListOf());
        when(leaderboardRepo.saveAll(anyListOf())).thenAnswer(invocation -> invocation.getArgument(0));

        leaderboardService.generateLeaderboards();

        verify(progressRepo).findByTaskType(type);
//        verify(leaderboardRepo).deleteAll(anyListOf());
        verify(leaderboardRepo).saveAll(argThat(leaderboard -> {
            @SuppressWarnings("unchecked")
            List<AmbassadorLeaderboardModel> lb = (List<AmbassadorLeaderboardModel>) leaderboard;
            return lb.size() == 3 &&
                    lb.get(0).getRank() == 1 && lb.get(0).getScore() == 70 &&
                    lb.get(1).getRank() == 2 && lb.get(1).getScore() == 50 &&
                    lb.get(2).getRank() == 3 && lb.get(2).getScore() == 30;
        }));
    }

//    @Test
//    void generateLeaderboardForType_skipsAmbassadorsNotFound() {
//        String type = "SESSION_HOSTING";
//        List<AmbassadorTaskProgressModel> progresses = List.of(
//                new AmbassadorTaskProgressModel("1", "amb1", "task1", type, 50, true, Instant.now(), Instant.now().plus(Duration.ofHours(1)), null, "NOT_ISSUED", true, Instant.now().plus(Duration.ofDays(1))),
//                new AmbassadorTaskProgressModel("2", "amb2", "task2", type, 30, true, Instant.now(), Instant.now().plus(Duration.ofHours(1)), null, "NOT_ISSUED", true, Instant.now().plus(Duration.ofDays(1)))
//        );
//
//        // Mock the task progress repo for each type used in generateLeaderboards
//        lenient().when(progressRepo.findByTaskType("SESSION_HOSTING")).thenReturn(progresses);
//        lenient().when(progressRepo.findByTaskType("REFERRAL")).thenReturn(Collections.emptyList());
//        lenient().when(progressRepo.findByTaskType("TRAINING_ATTENDANCE")).thenReturn(Collections.emptyList());
//
//        // Mock ambassadorRepo for valid and invalid ambassadors
//        when(ambassadorRepo.findById("amb1")).thenReturn(Optional.of(new AmbassadorProfileModel("1", "John", "john@example.com", null, null, false, null, null, "amb1", "BRONZE", 50, 10, 2, 1, true, null, null, null, null, null, null, null, null, 250, null, 150, 100, null)));
//        when(ambassadorRepo.findById("amb2")).thenReturn(Optional.empty()); // Ambassador not found for amb2
//
//        // Mock the leaderboardRepo to return empty list for "SESSION_HOSTING" (no previous leaderboard)
//        lenient().when(leaderboardRepo.findByTypeOrderByRankAsc("SESSION_HOSTING")).thenReturn(Collections.emptyList());
//        lenient().when(leaderboardRepo.findByTypeOrderByRankAsc("REFERRAL")).thenReturn(Collections.emptyList());
//        lenient().when(leaderboardRepo.findByTypeOrderByRankAsc("TRAINING_ATTENDANCE")).thenReturn(Collections.emptyList());
//
//        // Mock saveAll and deleteAll for leaderboardRepo
//        doNothing().when(leaderboardRepo).deleteAll(anyListOf());
//        when(leaderboardRepo.saveAll(anyListOf())).thenAnswer(invocation -> invocation.getArgument(0));
//
//        leaderboardService.generateLeaderboards();
//
//        // Verify interactions with progressRepo for each type
//        verify(progressRepo).findByTaskType("SESSION_HOSTING");
//        verify(progressRepo).findByTaskType("REFERRAL");
//        verify(progressRepo).findByTaskType("TRAINING_ATTENDANCE");
//
//        // Verify deleteAll was called with the correct type
//        verify(leaderboardRepo).deleteAll(anyListOf());
//
//        // Verify saveAll is called with the correct leaderboard
//        verify(leaderboardRepo).saveAll(argThat(leaderboard -> {
//            @SuppressWarnings("unchecked")
//            List<AmbassadorLeaderboardModel> lb = (List<AmbassadorLeaderboardModel>) leaderboard;
//            return lb.size() == 1 && lb.get(0).getAmbassadorId().equals("amb1");
//        }));
//    }

    private Iterable<? extends AmbassadorLeaderboardModel> anyListOf() {
        return anyList();
    }
}
