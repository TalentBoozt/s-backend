package com.talentboozt.s_backend.domains.resume.service;

import com.talentboozt.s_backend.domains.resume.dto.ResumeSummaryDto;
import com.talentboozt.s_backend.domains.resume.model.ResumeModel;
import com.talentboozt.s_backend.domains.resume.repository.mongodb.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * Core CRUD service for the Resume domain.
 * All operations are scoped to the authenticated employeeId.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;

    // ─── READ ────────────────────────────────────────────────────────────────

    /** Returns summary cards for the Dashboard */
    public List<ResumeSummaryDto> listByEmployee(String employeeId) {
        return resumeRepository
                .findByEmployeeIdAndDeletedFalseOrderByUpdatedAtDesc(employeeId)
                .stream()
                .map(ResumeSummaryDto::from)
                .toList();
    }

    /** Full resume — throws 404 if it doesn't belong to the user */
    public ResumeModel getById(String id, String employeeId) {
        return resumeRepository
                .findByIdAndEmployeeIdAndDeletedFalse(id, employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found"));
    }

    // ─── WRITE ───────────────────────────────────────────────────────────────

    /** Create a blank resume shell for the user */
    public ResumeModel create(String employeeId, String title, String platform) {
        ResumeModel resume = new ResumeModel();
        resume.setEmployeeId(employeeId);
        resume.setTitle(title != null && !title.isBlank() ? title : "My Resume");
        resume.setPlatform(platform);
        resume.setTemplateId("modern");
        resume.setPersonalInfo(new ResumeModel.PersonalInfo());
        resume.setWorkExperience(new ArrayList<>());
        resume.setEducation(new ArrayList<>());
        resume.setSkills(new ArrayList<>());
        resume.setProjects(new ArrayList<>());
        resume.setCertificates(new ArrayList<>());
        resume.setCustomSections(new ArrayList<>());
        resume.setSectionOrder(List.of("personal", "experience", "education", "skills", "projects", "certificates"));
        resume.setSettings(defaultSettings());
        resume.setAiUsageCount(0);
        resume.setDeleted(false);
        resume.setCompletionScore(0);
        resume.setAtsScore(0);
        return resumeRepository.save(resume);
    }

    /** Full update — replaces the resume content with the payload */
    public ResumeModel update(String id, String employeeId, ResumeModel patch) {
        ResumeModel existing = getById(id, employeeId);
        patch.setId(existing.getId());
        patch.setEmployeeId(existing.getEmployeeId());
        patch.setAiUsageCount(existing.getAiUsageCount()); // never overwrite from client
        patch.setDeleted(false);
        patch.setCreatedAt(existing.getCreatedAt());
        patch.setCompletionScore(scoreCompletion(patch));
        patch.setAtsScore(scoreAts(patch));
        return resumeRepository.save(patch);
    }

    /** Rename only */
    public ResumeModel rename(String id, String employeeId, String newTitle) {
        ResumeModel existing = getById(id, employeeId);
        existing.setTitle(newTitle);
        return resumeRepository.save(existing);
    }

    /** Soft delete */
    public void delete(String id, String employeeId) {
        ResumeModel existing = getById(id, employeeId);
        existing.setDeleted(true);
        resumeRepository.save(existing);
    }

    /** Duplicate an existing resume for the same user */
    public ResumeModel duplicate(String id, String employeeId) {
        ResumeModel source = getById(id, employeeId);
        source.setId(null); // force new ID on save
        source.setTitle(source.getTitle() + " (Copy)");
        source.setAiUsageCount(0); // fresh counter
        source.setCreatedAt(null);
        source.setUpdatedAt(null);
        return resumeRepository.save(source);
    }

    // ─── AI USAGE ───────────────────────────────────────────────────────────

    /** Atomically increments the AI usage counter. Returns updated document. */
    public ResumeModel incrementAiUsage(String id, String employeeId) {
        ResumeModel resume = getById(id, employeeId);
        resume.setAiUsageCount(resume.getAiUsageCount() + 1);
        return resumeRepository.save(resume);
    }

    /** Check whether AI usage is still allowed for this resume */
    public boolean canUseAi(String id, String employeeId) {
        ResumeModel resume = getById(id, employeeId);
        return resume.getAiUsageCount() < ResumeSummaryDto.MAX_AI_USAGE;
    }

    // ─── SCORING ────────────────────────────────────────────────────────────

    /** Rough completion % based on how many sections are non-empty */
    private int scoreCompletion(ResumeModel r) {
        int score = 0;
        ResumeModel.PersonalInfo p = r.getPersonalInfo();
        if (p != null) {
            if (notBlank(p.getFullName()))
                score += 15;
            if (notBlank(p.getEmail()))
                score += 10;
            if (notBlank(p.getPhone()))
                score += 5;
            if (notBlank(p.getSummary()))
                score += 15;
        }
        if (r.getWorkExperience() != null && !r.getWorkExperience().isEmpty())
            score += 25;
        if (r.getEducation() != null && !r.getEducation().isEmpty())
            score += 15;
        if (r.getSkills() != null && !r.getSkills().isEmpty())
            score += 10;
        if (r.getProjects() != null && !r.getProjects().isEmpty())
            score += 5;
        return Math.min(score, 100);
    }

    /** Simple ATS score: penalizes missing keywords/contacts */
    private int scoreAts(ResumeModel r) {
        int score = 60;
        ResumeModel.PersonalInfo p = r.getPersonalInfo();
        if (p != null) {
            if (notBlank(p.getEmail()))
                score += 10;
            if (notBlank(p.getPhone()))
                score += 5;
            if (notBlank(p.getSummary()))
                score += 10;
        }
        if (r.getSkills() != null && r.getSkills().size() >= 5)
            score += 10;
        if (r.getWorkExperience() != null && !r.getWorkExperience().isEmpty())
            score += 5;
        return Math.min(score, 100);
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private ResumeModel.ResumeSettings defaultSettings() {
        ResumeModel.ResumeSettings s = new ResumeModel.ResumeSettings();
        s.setPrimaryColor("#6C5CE7");
        s.setFontFamily("inter");
        s.setFontSize("medium");
        s.setSpacing("normal");
        s.setShowIcons(true);
        s.setShowPhoto(false);
        s.setShowSkillLevels(true);
        return s;
    }
}
