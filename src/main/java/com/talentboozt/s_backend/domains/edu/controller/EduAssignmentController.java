package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.dto.evaluation.AssignmentRequest;
import com.talentboozt.s_backend.domains.edu.dto.evaluation.GradeRequest;
import com.talentboozt.s_backend.domains.edu.dto.evaluation.SubmissionRequest;
import com.talentboozt.s_backend.domains.edu.model.EAssignmentSubmissions;
import com.talentboozt.s_backend.domains.edu.model.EAssignments;
import com.talentboozt.s_backend.domains.edu.service.EduAssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/assignments")
public class EduAssignmentController {

    private final EduAssignmentService assignmentService;

    public EduAssignmentController(EduAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/course/{courseId}/lesson/{lessonId}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<EAssignments> createAssignment(
            @PathVariable String courseId,
            @PathVariable String lessonId,
            @RequestParam String creatorId,
            @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.createAssignment(courseId, "legacy-section", lessonId, creatorId, request));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<EAssignments> getAssignmentByLessonId(@PathVariable String lessonId) {
        return ResponseEntity.ok(assignmentService.getAssignmentByLesson(lessonId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EAssignments> getAssignment(@PathVariable String id) {
        return ResponseEntity.ok(assignmentService.getAssignment(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<EAssignments> updateAssignment(@PathVariable String id, @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(assignmentService.updateAssignment(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{assignmentId}/submit")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<EAssignmentSubmissions> submitAssignment(
            @PathVariable String assignmentId,
            @RequestParam String userId,
            @RequestBody SubmissionRequest request) {
        return ResponseEntity.ok(assignmentService.submitAssignment(assignmentId, userId, request));
    }

    @GetMapping("/{assignmentId}/submission/{userId}")
    public ResponseEntity<EAssignmentSubmissions> getSubmissionStatus(
            @PathVariable String assignmentId,
            @PathVariable String userId) {
        return ResponseEntity.ok(assignmentService.getSubmissionStatus(assignmentId, userId));
    }

    @GetMapping("/submissions/course/{courseId}/user/{userId}")
    public ResponseEntity<java.util.List<EAssignmentSubmissions>> getLearnerSubmissions(
            @PathVariable String courseId,
            @PathVariable String userId) {
        return ResponseEntity.ok(assignmentService.getLearnerSubmissions(courseId, userId));
    }

    @PutMapping("/submissions/{submissionId}/grade")
    @PreAuthorize("hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<EAssignmentSubmissions> gradeSubmission(
            @PathVariable String submissionId,
            @RequestParam String graderId,
            @RequestBody GradeRequest request) {
        return ResponseEntity.ok(assignmentService.gradeSubmission(submissionId, graderId, request));
    }
}
