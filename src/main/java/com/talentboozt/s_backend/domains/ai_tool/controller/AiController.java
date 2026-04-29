package com.talentboozt.s_backend.domains.ai_tool.controller;

import com.talentboozt.s_backend.domains.ai_tool.dto.*;
import com.talentboozt.s_backend.domains.ai_tool.service.AiService;
import com.talentboozt.s_backend.shared.security.annotations.AuthenticatedUser;
import com.talentboozt.s_backend.shared.security.model.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/career-paths")
    public CareerPathResponse careerPaths(@AuthenticatedUser String userId,
            @RequestBody CareerPathRequest req) {
        return aiService.getCareerPaths(userId, req);
    }

    @PostMapping("/roadmap")
    public RoadmapResponse roadmap(@AuthenticatedUser String userId, @RequestBody RoadmapRequest req) {
        return aiService.getRoadmap(userId, req);
    }

    @PostMapping("/chat")
    public ChatResponse chat(@AuthenticatedUser String userId, @RequestBody ChatRequest req) {
        return aiService.chat(userId, req);
    }
}
