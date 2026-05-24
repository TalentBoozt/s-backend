package com.talentboozt.s_backend.domains.edu.community.api;

import com.talentboozt.s_backend.domains.edu.community.application.AppPeerLearningService;
import com.talentboozt.s_backend.domains.edu.community.application.AppCommunityLeaderboardService;
import com.talentboozt.s_backend.domains.edu.community.application.AppReferralRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/edu/community")
public class EduCommunityController {

    @Autowired
    private AppPeerLearningService peerLearningService;

    @Autowired
    private AppCommunityLeaderboardService leaderboardService;

    @Autowired
    private AppReferralRewardService referralRewardService;

    @PostMapping("/circle/create")
    public Map<String, Object> createCircle(
            @RequestParam String name, 
            @RequestParam String subject, 
            @RequestParam List<String> members) {
        return peerLearningService.createStudyCircle(name, subject, members);
    }

    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getLeaderboard() {
        return leaderboardService.fetchGlobalLeaderboard();
    }

    @PostMapping("/referral/reward")
    public Map<String, Object> rewardReferral(
            @RequestParam String referrerId, 
            @RequestParam String refereeId) {
        return referralRewardService.rewardReferral(referrerId, refereeId);
    }
}
