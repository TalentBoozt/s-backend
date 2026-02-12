package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorLeaderboardModel;
import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.AmbassadorLeaderboardRepository;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.plat_courses.model.AmbassadorTaskProgressModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.mongodb.TaskProgressRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaderboardService {

    @Autowired
    private AmbassadorProfileRepository ambassadorRepo;

    @Autowired
    private TaskProgressRepository progressRepo;

    @Autowired
    private AmbassadorLeaderboardRepository leaderboardRepo;

    public void generateLeaderboards() {
        generateLeaderboardForType("REFERRAL");
        generateLeaderboardForType("SESSION_HOSTING");
        generateLeaderboardForType("TRAINING_ATTENDANCE");
    }

    private void generateLeaderboardForType(String type) {
        List<AmbassadorTaskProgressModel> progresses = progressRepo.findByTaskType(type);

        Map<String, Integer> ambassadorScoreMap = progresses.stream()
                .collect(Collectors.groupingBy(
                        AmbassadorTaskProgressModel::getAmbassadorId,
                        Collectors.summingInt(AmbassadorTaskProgressModel::getProgressValue)
                ));

        List<Map.Entry<String, Integer>> sorted = ambassadorScoreMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();

        List<AmbassadorLeaderboardModel> leaderboard = new ArrayList<>();
        Instant now = Instant.now();
        Instant expireAt = now.plus(Duration.ofHours(12));

        int rank = 1;
        for (Map.Entry<String, Integer> entry : sorted) {
            Optional<AmbassadorProfileModel> optional = ambassadorRepo.findById(Objects.requireNonNull(entry.getKey()));
            if (optional.isEmpty()) continue;

            AmbassadorProfileModel ambassador = optional.get();

            AmbassadorLeaderboardModel lb = new AmbassadorLeaderboardModel();
            lb.setType(type);
            lb.setAmbassadorId(ambassador.getId());
            lb.setName(ambassador.getName());
            lb.setEmail(ambassador.getEmail());
            lb.setLevel(ambassador.getLevel());
            lb.setScore(entry.getValue());
            lb.setRank(rank++);
            lb.setGeneratedAt(now);
            lb.setExpireAt(expireAt);

            leaderboard.add(lb);
        }

        // Clear previous and save fresh
        leaderboardRepo.deleteAll(Objects.requireNonNull(leaderboardRepo.findByTypeOrderByRankAsc(type)));
        leaderboardRepo.saveAll(leaderboard);
    }
}
