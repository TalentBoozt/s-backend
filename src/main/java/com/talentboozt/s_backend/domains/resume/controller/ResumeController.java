package com.talentboozt.s_backend.domains.resume.controller;

import com.talentboozt.s_backend.domains.resume.dto.AiResumeRequest;
import com.talentboozt.s_backend.domains.resume.dto.AiResumeResponse;
import com.talentboozt.s_backend.domains.resume.dto.ResumeSummaryDto;
import com.talentboozt.s_backend.domains.resume.model.ResumeModel;
import com.talentboozt.s_backend.domains.resume.service.ResumeAiService;
import com.talentboozt.s_backend.domains.resume.service.ResumeService;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/**
 * Resume CRUD + AI endpoints.
 * Base path: /api/v2/resumes
 *
 * Every write operation extracts the employeeId from the JWT (SSO token).
 * This ensures user isolation — no user can touch another user's resumes.
 */
@Slf4j
@RestController
@RequestMapping("/api/v2/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeAiService resumeAiService;
    private final JwtService jwtService;

    // ─── Helper ──────────────────────────────────────────────────────────────

    /**
     * Extracts the SSO employeeId from the JWT in the Authorization header or
     * TB_REFRESH cookie.
     */
    private String resolveEmployeeId(HttpServletRequest request) {
        String token = jwtService.extractTokenFromHeaderOrCookie(request);
        if (token == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        try {
            return jwtService.getUserFromToken(token).getEmployeeId();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    // ─── List ────────────────────────────────────────────────────────────────

    /**
     * GET /api/v2/resumes
     * Returns all resume summary cards for the authenticated user (for Dashboard).
     */
    @GetMapping
    public ResponseEntity<List<ResumeSummaryDto>> listResumes(HttpServletRequest request) {
        String employeeId = resolveEmployeeId(request);
        return ResponseEntity.ok(resumeService.listByEmployee(employeeId));
    }

    // ─── Get Single ──────────────────────────────────────────────────────────

    /**
     * GET /api/v2/resumes/{id}
     * Returns the full resume document (for the Builder page).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResumeModel> getResume(@PathVariable String id, HttpServletRequest request) {
        String employeeId = resolveEmployeeId(request);
        return ResponseEntity.ok(resumeService.getById(id, employeeId));
    }

    // ─── Create ──────────────────────────────────────────────────────────────

    /**
     * POST /api/v2/resumes
     * Body: { "title": "My Resume" }
     */
    @PostMapping
    public ResponseEntity<ResumeModel> createResume(
            @RequestBody(required = false) Map<String, String> body,
            HttpServletRequest request) {
        String employeeId = resolveEmployeeId(request);
        String title = body != null ? body.getOrDefault("title", "My Resume") : "My Resume";
        String platform = body != null ? body.getOrDefault("platform", "ResumeBuilder") : "ResumeBuilder";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resumeService.create(employeeId, title, platform));
    }

    // ─── Full Update (Auto-Save) ──────────────────────────────────────────────

    /**
     * PUT /api/v2/resumes/{id}
     * Full replacement of resume content. Used by the auto-save hook.
     * Server recalculates completion & ATS scores.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResumeModel> updateResume(
            @PathVariable String id,
            @RequestBody ResumeModel payload,
            HttpServletRequest request) {
        String employeeId = resolveEmployeeId(request);
        return ResponseEntity.ok(resumeService.update(id, employeeId, payload));
    }

    // ─── Rename ──────────────────────────────────────────────────────────────

    /**
     * PATCH /api/v2/resumes/{id}/rename
     * Body: { "title": "New Name" }
     */
    @PatchMapping("/{id}/rename")
    public ResponseEntity<ResumeModel> renameResume(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        String employeeId = resolveEmployeeId(request);
        String newTitle = body.getOrDefault("title", "My Resume");
        return ResponseEntity.ok(resumeService.rename(id, employeeId, newTitle));
    }

    // ─── Duplicate ───────────────────────────────────────────────────────────

    /**
     * POST /api/v2/resumes/{id}/duplicate
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<ResumeModel> duplicateResume(
            @PathVariable String id,
            HttpServletRequest request) {
        String employeeId = resolveEmployeeId(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resumeService.duplicate(id, employeeId));
    }

    // ─── Soft Delete ─────────────────────────────────────────────────────────

    /**
     * DELETE /api/v2/resumes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(
            @PathVariable String id,
            HttpServletRequest request) {
        String employeeId = resolveEmployeeId(request);
        resumeService.delete(id, employeeId);
        return ResponseEntity.noContent().build();
    }

    // ─── AI Generation ───────────────────────────────────────────────────────

    /**
     * POST /api/v2/resumes/ai/generate
     *
     * Requires valid JWT (authenticated user only).
     * Per-resume limit: 5 calls maximum.
     *
     * Body: AiResumeRequest { resumeId, type, context, jobDescription }
     */
    @PostMapping("/ai/generate")
    public ResponseEntity<AiResumeResponse> generateAiContent(
            @RequestBody AiResumeRequest req,
            HttpServletRequest request) {
        String employeeId = resolveEmployeeId(request);

        if (req.getResumeId() == null || req.getResumeId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "resumeId is required");
        }

        log.info("AI generation requested by employeeId={} for resumeId={} type={}",
                employeeId, req.getResumeId(), req.getType());

        AiResumeResponse response = resumeAiService.generate(employeeId, req);
        return ResponseEntity.ok(response);
    }

    // ─── AI Usage Status ─────────────────────────────────────────────────────

    /**
     * GET /api/v2/resumes/{id}/ai-usage
     * Returns remaining AI generations for a resume.
     */
    @GetMapping("/{id}/ai-usage")
    public ResponseEntity<Map<String, Object>> getAiUsage(
            @PathVariable String id,
            HttpServletRequest request) {
        String employeeId = resolveEmployeeId(request);
        ResumeModel resume = resumeService.getById(id, employeeId);
        int remaining = Math.max(0, ResumeSummaryDto.MAX_AI_USAGE - resume.getAiUsageCount());
        return ResponseEntity.ok(Map.of(
                "aiUsageCount", resume.getAiUsageCount(),
                "aiUsageRemaining", remaining,
                "maxUsage", ResumeSummaryDto.MAX_AI_USAGE));
    }
}
