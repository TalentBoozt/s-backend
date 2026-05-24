package com.talentboozt.s_backend.domains.edu.learning.api;

import com.talentboozt.s_backend.domains.edu.learning.application.AppLessonProgressService;
import com.talentboozt.s_backend.domains.edu.learning.application.AppLearningSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/edu/learning")
public class LearningController {

    @Autowired
    private AppLessonProgressService lessonProgressService;

    @Autowired
    private AppLearningSessionService learningSessionService;

    @PostMapping("/progress")
    public Map<String, Object> updateProgress(
            @RequestParam String userId, 
            @RequestParam String lessonId, 
            @RequestParam double percentage, 
            @RequestParam double watchDuration) {
        return lessonProgressService.updateLessonProgress(userId, lessonId, percentage, watchDuration);
    }

    @PostMapping("/session")
    public Map<String, Object> logSession(
            @RequestParam String userId,
            @RequestParam double sessionMinutes,
            @RequestParam double engagementScore) {
        return learningSessionService.logLearningSession(userId, sessionMinutes, engagementScore);
    }
}
