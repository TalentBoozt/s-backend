package com.talentboozt.s_backend.domains.edu.ai.api;

import com.talentboozt.s_backend.domains.edu.ai.tutoring.application.AppAiTutorService;
import com.talentboozt.s_backend.domains.edu.ai.recommendation.application.AppSkillRecommendationEngine;
import com.talentboozt.s_backend.domains.edu.ai.generation.application.AppAIAdaptiveQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/edu/ai")
public class EduAiController {

    @Autowired
    private AppAiTutorService aiTutorService;

    @Autowired
    private AppSkillRecommendationEngine skillRecommendationEngine;

    @Autowired
    private AppAIAdaptiveQuizService adaptiveQuizService;

    @PostMapping("/tutor")
    public String askTutor(@RequestParam String concept, @RequestParam String mode) {
        return aiTutorService.generateTutorResponse(concept, mode);
    }

    @GetMapping("/recommendations")
    public List<String> getRecommendations(@RequestParam String skill) {
        return skillRecommendationEngine.recommendNextSkills(skill);
    }

    @PostMapping("/quiz")
    public Map<String, Object> getQuiz(@RequestParam String skill, @RequestParam int score) {
        return adaptiveQuizService.compileAdaptiveQuiz(skill, score);
    }
}
