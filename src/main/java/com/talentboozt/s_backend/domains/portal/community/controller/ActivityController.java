package com.talentboozt.s_backend.domains.portal.community.controller;

import com.talentboozt.s_backend.domains.portal.community.model.Activity;
import com.talentboozt.s_backend.domains.portal.community.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v2/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping("/{userId}")
    public List<Activity> getUserActivities(@PathVariable String userId) {
        return activityService.getUserActivities(userId);
    }
}
