package com.talentboozt.s_backend.domains.interviews.service;

import com.talentboozt.s_backend.domains.interviews.model.InterviewModel;
import com.talentboozt.s_backend.domains.interviews.repository.mongodb.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewRepository interviewRepository;

    public InterviewModel scheduleInterview(InterviewModel interview) {
        interview.setStatus("SCHEDULED");
        interview.setCreatedAt(Instant.now());
        interview.setUpdatedAt(Instant.now());
        return interviewRepository.save(interview);
    }

    public List<InterviewModel> getInterviewsForRecruiter(String recruiterId) {
        return interviewRepository.findByInterviewerIdsContaining(recruiterId);
    }

    public InterviewModel submitFeedback(String interviewId, InterviewModel.InterviewFeedback feedback) {
        InterviewModel interview = interviewRepository.findById(interviewId).orElseThrow();
        interview.getFeedback().add(feedback);
        interview.setUpdatedAt(Instant.now());
        return interviewRepository.save(interview);
    }
}
