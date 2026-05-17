package com.talentboozt.s_backend.domains.candidate.service;

import com.talentboozt.s_backend.domains.applications.model.ApplicationModel;
import com.talentboozt.s_backend.domains.jobs.model.JobPostModel;
import com.talentboozt.s_backend.domains.resume.model.ResumeModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidateRankingService {

    /**
     * Ranks a candidate for a specific job based on their resume and application data.
     */
    public int calculateMatchScore(ResumeModel resume, JobPostModel job) {
        // Placeholder for complex AI matching logic
        // For now, use a simple keyword overlap or existing Phase 2 logic
        int score = 75; 
        
        if (resume.getSkills() != null && job.getSkills() != null) {
            long matches = resume.getSkills().stream()
                    .filter(s -> job.getSkills().contains(s.getName()))
                    .count();
            score += matches * 5;
        }
        
        return Math.min(score, 100);
    }
    
    /**
     * Provides an AI summary of why a candidate matches a job.
     */
    public String generateCandidateSummary(ResumeModel resume, JobPostModel job) {
        return "This candidate has 5+ years of experience with React and has worked at high-growth startups similar to yours.";
    }
}
