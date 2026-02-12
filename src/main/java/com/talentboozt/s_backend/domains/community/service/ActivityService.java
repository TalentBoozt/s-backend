package com.talentboozt.s_backend.domains.community.service;

import com.talentboozt.s_backend.domains.community.model.Activity;
import com.talentboozt.s_backend.domains.community.repository.mongodb.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;

    public void logActivity(String userId, String action, String targetId) {
        Activity activity = Activity.builder()
                .userId(userId)
                .action(action)
                .targetId(targetId)
                .timestamp(LocalDateTime.now())
                .build();
        activityRepository.save(activity);
    }

    public List<Activity> getUserActivities(String userId) {
        return activityRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
