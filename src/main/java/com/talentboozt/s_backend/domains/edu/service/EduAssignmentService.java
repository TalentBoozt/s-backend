package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.evaluation.AssignmentRequest;
import com.talentboozt.s_backend.domains.edu.dto.evaluation.GradeRequest;
import com.talentboozt.s_backend.domains.edu.dto.evaluation.SubmissionRequest;
import com.talentboozt.s_backend.domains.edu.enums.EGradingStatus;
import com.talentboozt.s_backend.domains.edu.model.EAssignmentSubmissions;
import com.talentboozt.s_backend.domains.edu.model.EAssignments;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAssignmentSubmissionsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAssignmentsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class EduAssignmentService {

    private final EAssignmentsRepository assignmentsRepository;
    private final EAssignmentSubmissionsRepository submissionsRepository;

    public EduAssignmentService(EAssignmentsRepository assignmentsRepository,
            EAssignmentSubmissionsRepository submissionsRepository) {
        this.assignmentsRepository = assignmentsRepository;
        this.submissionsRepository = submissionsRepository;
    }

    public EAssignments createAssignment(String courseId, String sectionId, String creatorId,
            AssignmentRequest request) {
        EAssignments assignment = EAssignments.builder()
                .courseId(courseId)
                .sectionId(sectionId)
                .title(request.getTitle())
                .description(request.getDescription())
                .instructions(request.getInstructions())
                .attachmentUrls(request.getAttachmentUrls())
                .maxScore(request.getMaxScore())
                .weightage(request.getWeightage())
                .dueDate(request.getDueDate())
                .isPublished(request.getIsPublished() != null ? request.getIsPublished() : false)
                .createdBy(creatorId)
                .createdAt(Instant.now())
                .build();
        return assignmentsRepository.save(assignment);
    }

    public EAssignments getAssignment(String id) {
        return assignmentsRepository.findById(id).orElseThrow(() -> new RuntimeException("Assignment not found"));
    }

    public EAssignments updateAssignment(String id, AssignmentRequest request) {
        EAssignments assignment = getAssignment(id);
        if (request.getTitle() != null)
            assignment.setTitle(request.getTitle());
        if (request.getDescription() != null)
            assignment.setDescription(request.getDescription());
        if (request.getInstructions() != null)
            assignment.setInstructions(request.getInstructions());
        if (request.getAttachmentUrls() != null)
            assignment.setAttachmentUrls(request.getAttachmentUrls());
        if (request.getMaxScore() != null)
            assignment.setMaxScore(request.getMaxScore());
        if (request.getWeightage() != null)
            assignment.setWeightage(request.getWeightage());
        if (request.getDueDate() != null)
            assignment.setDueDate(request.getDueDate());
        if (request.getIsPublished() != null)
            assignment.setIsPublished(request.getIsPublished());

        assignment.setUpdatedAt(Instant.now());
        return assignmentsRepository.save(assignment);
    }

    public void deleteAssignment(String id) {
        assignmentsRepository.deleteById(id);
    }

    public EAssignmentSubmissions submitAssignment(String assignmentId, String userId, SubmissionRequest request) {
        // verify assignment exists
        getAssignment(assignmentId);

        EAssignmentSubmissions submission = EAssignmentSubmissions.builder()
                .userId(userId)
                .assignmentId(assignmentId)
                .content(request.getContent())
                .attachmentUrls(request.getAttachmentUrls())
                .status(EGradingStatus.PENDING)
                .submittedAt(Instant.now())
                .build();
        return submissionsRepository.save(submission);
    }

    public EAssignmentSubmissions gradeSubmission(String submissionId, String graderId, GradeRequest request) {
        EAssignmentSubmissions submission = submissionsRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission not found"));

        submission.setScore(request.getScore());
        submission.setStatus(request.getStatus() != null ? request.getStatus() : EGradingStatus.GRADED);
        submission.setFeedback(request.getFeedback());
        submission.setGradedBy(graderId);
        submission.setGradedAt(Instant.now());

        return submissionsRepository.save(submission);
    }
}
