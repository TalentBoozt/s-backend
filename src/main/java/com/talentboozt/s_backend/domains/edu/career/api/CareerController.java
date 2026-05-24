package com.talentboozt.s_backend.domains.edu.career.api;

import com.talentboozt.s_backend.domains.edu.career.application.AppResumeBuilderService;
import com.talentboozt.s_backend.domains.edu.career.application.AppJobMatchingEngine;
import com.talentboozt.s_backend.domains.edu.career.application.AppPortfolioGeneratorService;
import com.talentboozt.s_backend.domains.edu.career.application.AppCareerCoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/edu/career")
public class CareerController {

    @Autowired
    private AppResumeBuilderService resumeBuilderService;

    @Autowired
    private AppJobMatchingEngine jobMatchingEngine;

    @Autowired
    private AppPortfolioGeneratorService portfolioGeneratorService;

    @Autowired
    private AppCareerCoachService careerCoachService;

    @PostMapping("/resume/ats")
    public Map<String, Object> compileAts(
            @RequestParam String name, 
            @RequestParam String role, 
            @RequestParam List<String> skills) {
        return resumeBuilderService.compileAtsResume(name, role, skills, List.of("Freelance experience"));
    }

    @PostMapping("/jobs/match")
    public List<Map<String, Object>> matchJobs(@RequestParam List<String> skills) {
        return jobMatchingEngine.matchJobsToSkills(skills);
    }

    @PostMapping("/portfolio/generate")
    public Map<String, Object> generatePortfolio(
            @RequestParam String name, 
            @RequestParam List<String> projects) {
        return portfolioGeneratorService.compilePortfolio(name, projects);
    }

    @PostMapping("/coach/advise")
    public String advise(
            @RequestParam String focus, 
            @RequestParam String track) {
        return careerCoachService.formulateAdvisorAdvice(focus, track);
    }
}
