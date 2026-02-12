package com.talentboozt.s_backend.domains.reputation.controller;

import com.talentboozt.s_backend.domains.reputation.dto.LeaderboardEntry;
import com.talentboozt.s_backend.domains.reputation.model.UserReputation;
import com.talentboozt.s_backend.domains.reputation.service.ReputationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/reputation")
@RequiredArgsConstructor
public class ReputationController {
    private final ReputationService reputationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserReputation> getUserReputation(@PathVariable String userId) {
        return ResponseEntity.ok(reputationService.getUserReputation(userId));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(reputationService.getLeaderboard(limit));
    }
}
