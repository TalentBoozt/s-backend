package com.talentboozt.s_backend.domains.ambassador.controller;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorLeaderboardModel;
import com.talentboozt.s_backend.domains.ambassador.repository.mongodb.AmbassadorLeaderboardRepository;
import com.talentboozt.s_backend.domains.ambassador.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/ambassador/leaderboard")
public class AmbassadorLeaderboardController {

    @Autowired
    private AmbassadorLeaderboardRepository leaderboardRepository;

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/{type}")
    public ResponseEntity<List<AmbassadorLeaderboardModel>> getLeaderboard(@PathVariable String type) {
        // type can be REFERRAL, SESSION_HOSTING, TRAINING_ATTENDANCE
        List<AmbassadorLeaderboardModel> leaderboard = leaderboardRepository
                .findByTypeOrderByRankAsc(type.toUpperCase());
        return ResponseEntity.ok(leaderboard);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshLeaderboards() {
        leaderboardService.generateLeaderboards();
        return ResponseEntity.ok("Leaderboards refreshed successfully");
    }
}
