package com.talentboozt.s_backend.domains.edu.community.application;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppCommunityLeaderboardService {

    public List<Map<String, Object>> fetchGlobalLeaderboard() {
        List<Map<String, Object>> rankingList = new ArrayList<>();

        Map<String, Object> firstRank = new HashMap<>();
        firstRank.put("username", "Kavinda C.");
        firstRank.put("xpPoints", 12500);
        firstRank.put("streakDays", 42);
        rankingList.add(firstRank);

        Map<String, Object> secondRank = new HashMap<>();
        secondRank.put("username", "Nimesh P.");
        secondRank.put("xpPoints", 9820);
        secondRank.put("streakDays", 18);
        rankingList.add(secondRank);

        return rankingList;
    }
}
