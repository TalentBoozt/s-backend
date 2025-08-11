package com.talentboozt.s_backend.domains.ai_tool.controller;

import com.talentboozt.s_backend.domains.ai_tool.dto.*;
import com.talentboozt.s_backend.domains.ai_tool.service.AiService;
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
    public CareerPathResponse careerPaths(@RequestBody CareerPathRequest req) {
        return aiService.getCareerPaths(req);
    }

    @PostMapping("/roadmap")
    public RoadmapResponse roadmap(@RequestBody RoadmapRequest req) {
        return aiService.getRoadmap(req);
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest req) {
        return aiService.chat(req);
    }
}
