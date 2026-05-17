package com.talentboozt.s_backend.domains.recommendations.service;

import com.talentboozt.s_backend.domains.jobs.model.JobPostModel;
import com.talentboozt.s_backend.domains.jobs.repository.mongodb.JobPostRepository;
import com.talentboozt.s_backend.domains.resume.model.ResumeModel;
import com.talentboozt.s_backend.domains.resume.repository.mongodb.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final JobPostRepository jobRepository;
    private final ResumeRepository resumeRepository;

    public List<JobPostModel> getRecommendedJobs(String employeeId) {
        // 1. Get active resume
        ResumeModel resume = resumeRepository.findByEmployeeIdAndDeletedFalseOrderByUpdatedAtDesc(employeeId)
                .stream().findFirst().orElse(null);

        // 2. Fetch active jobs
        List<JobPostModel> activeJobs = jobRepository.findByStatus("OPEN");

        if (resume == null) {
            return activeJobs.stream().limit(10).collect(Collectors.toList());
        }

        // 3. Simple matching logic (Placeholder for AI/Embedding similarity)
        // In production, this would use vector search or a complex scoring algorithm
        return activeJobs.stream()
                .map(job -> {
                    job.setMatchScore(calculateMatchScore(resume, job));
                    return job;
                })
                .sorted((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(20)
                .collect(Collectors.toList());
    }

    private int calculateMatchScore(ResumeModel resume, JobPostModel job) {
        // Dummy matching logic for now
        int score = 70;
        if (resume.getSkills() != null && job.getSkills() != null) {
            long matches = resume.getSkills().stream()
                    .filter(s -> job.getSkills().contains(s.getName()))
                    .count();
            score += matches * 5;
        }
        return Math.min(score, 100);
    }
}
