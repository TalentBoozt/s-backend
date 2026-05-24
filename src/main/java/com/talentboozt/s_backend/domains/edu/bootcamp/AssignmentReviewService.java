package com.talentboozt.s_backend.domains.edu.bootcamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Assignment Submission and Grading Service.
 * Coordinates code uploads, tutor review metrics, and AI summary reviews,
 * persisting them inside the MongoDB collection "assignment_submissions".
 */
@Service
public class AssignmentReviewService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Saves user study uploads in MongoDB.
     */
    public Map<String, Object> submitAssignment(String userId, String assignmentId, String submissionUrl) {
        Map<String, Object> submissionMap = new HashMap<>();
        submissionMap.put("userId", userId);
        submissionMap.put("assignmentId", assignmentId);
        submissionMap.put("submissionUrl", submissionUrl);
        submissionMap.put("status", "SUBMITTED");
        submissionMap.put("submittedAt", new Date());

        mongoTemplate.save(submissionMap, "assignment_submissions");
        System.out.println("[Assignment] Logged submission for user: " + userId);
        return submissionMap;
    }

    /**
     * Records tutor gradings and AI summaries.
     */
    public Map<String, Object> gradeAssignment(String submissionId, String mentorFeedback, int grade) {
        Map<String, Object> gradeReport = new HashMap<>();
        gradeReport.put("submissionId", submissionId);
        gradeReport.put("mentorFeedback", mentorFeedback);
        gradeReport.put("grade", grade);
        gradeReport.put("aiSummaryReview", "Tutor remarks: The layout displays robust responsive breakpoints. Ensure variables follow dry patterns.");
        gradeReport.put("gradedAt", new Date());

        mongoTemplate.save(gradeReport, "assignment_grades");
        System.out.println("[Assignment] Graded submission: " + submissionId + " with score: " + grade);
        return gradeReport;
    }
}
