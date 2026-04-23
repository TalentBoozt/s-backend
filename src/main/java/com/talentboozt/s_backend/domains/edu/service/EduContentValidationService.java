package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.response.EduValidationResponseDTO;
import com.talentboozt.s_backend.domains.edu.model.ECourseSections;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.ELessons;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECourseSectionsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ELessonsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EduContentValidationService {

    private final ELessonsRepository lessonsRepository;
    private final ECourseSectionsRepository sectionsRepository;

    public EduValidationResponseDTO validateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return EduValidationResponseDTO.builder()
                    .plagiarismScore(0.0)
                    .aiScore(0.0)
                    .qualityScore(0.0)
                    .findings(List.of("Empty content provided."))
                    .status("REJECTED")
                    .build();
        }

        double plagiarismScore = calculatePlagiarismScore(content);
        double aiScore = detectAIContent(content);
        double qualityScore = calculateQualityScore(content, plagiarismScore, aiScore);

        List<String> findings = new ArrayList<>();
        String status = "PASSED";

        if (plagiarismScore > 30) {
            findings.add(String.format("High similarity detected (%.1f%%). Potential plagiarism.", plagiarismScore));
            status = "WARNING";
        }
        if (aiScore > 80) {
            findings.add(String.format("Content seems heavily AI-generated (%.1f%%).", aiScore));
            if (!"WARNING".equals(status))
                status = "WARNING";
        }
        if (plagiarismScore > 70) {
            status = "REJECTED";
        }

        return EduValidationResponseDTO.builder()
                .plagiarismScore(plagiarismScore)
                .aiScore(aiScore)
                .qualityScore(qualityScore)
                .findings(findings)
                .status(status)
                .build();
    }

    private double calculatePlagiarismScore(String content) {
        List<ELessons> existingLessons = lessonsRepository.findAll(); // Optimization: Use search/index in production
        double maxSimilarity = 0.0;

        String sourceText = content.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "");
        Set<String> sourceWords = new HashSet<>(List.of(sourceText.split("\\s+")));

        for (ELessons lesson : existingLessons) {
            String target = lesson.getMarkdownContent() != null ? lesson.getMarkdownContent() : lesson.getTextContent();
            if (target == null || target.trim().isEmpty())
                continue;

            String targetText = target.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", "");
            Set<String> targetWords = new HashSet<>(List.of(targetText.split("\\s+")));

            double similarity = calculateJaccardSimilarity(sourceWords, targetWords);
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
            }
        }

        return maxSimilarity * 100;
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);

        if (union.isEmpty())
            return 0.0;
        return (double) intersection.size() / union.size();
    }

    private double detectAIContent(String content) {
        // Simple logic-base detection (placeholders for advanced LLM analysis)
        // Check for common AI patterns: repetitive structures, specific transitional
        // phrases, lack of typos
        double score = 0.0;

        String[] words = content.split("\\s+");
        if (words.length < 50)
            return 10.0; // Too short to accurately detect

        // Mock: In a real scenario, this would call an external API like GPTZero or
        // Originality.ai
        // For now, we use a logic based on length and structure
        if (content.contains("In conclusion") || content.contains("Overall, it is important to"))
            score += 10;
        if (content.length() > 500 && words.length > 50)
            score += 15;

        // Random variance for simulation
        score += Math.random() * 20;

        return Math.min(100.0, score);
    }

    private double calculateQualityScore(String content, double plagiarism, double ai) {
        double base = 100.0;
        base -= plagiarism;
        base -= (ai / 2.0); // AI content is acceptable but penalizes pure "quality" ranking

        // Length bonus/penalty
        if (content.length() < 100)
            base -= 20;
        else if (content.length() > 1000)
            base += 5;

        return Math.max(0.0, Math.min(100.0, base));
    }

    public EduValidationResponseDTO validateCourse(ECourses course) {
        List<String> findings = new ArrayList<>();
        double score = 100.0;

        // 1. Basic Metadata Depth
        if (course.getTitle() == null || course.getTitle().length() < 10) {
            score -= 20;
            findings.add("Title is too short (min 10 chars).");
        }
        if (course.getShortDescription() == null || course.getShortDescription().length() < 50) {
            score -= 10;
            findings.add("Short description is too brief (min 50 chars).");
        }
        if (course.getDescription() == null || course.getDescription().length() < 200) {
            score -= 15;
            findings.add("Full description is below recommended depth (min 200 chars).");
        }

        // 2. Media Presence
        if (course.getThumbnail() == null || course.getThumbnail().isEmpty()) {
            score -= 15;
            findings.add("Course thumbnail is missing.");
        }
        if (course.getPreviewVideoUrl() == null || course.getPreviewVideoUrl().isEmpty()) {
            score -= 10;
            findings.add("Preview video is missing (affects enrollment conversion).");
        }

        // 3. Structural Integrity
        List<ECourseSections> sections = sectionsRepository.findByCourseId(course.getId());
        List<ELessons> lessons = lessonsRepository.findByCourseId(course.getId());

        if (sections.isEmpty()) {
            score -= 40;
            findings.add("Course has no modules/sections.");
        } else if (lessons.isEmpty()) {
            score -= 30;
            findings.add("Course has sections but no lessons.");
        } else {
            if (sections.size() < 3)
                findings.add("Recommended: Add at least 3 modules for better structure.");
            if (lessons.size() < 5)
                findings.add("Recommended: Course should have at least 5 lessons for production readiness.");
        }

        // 4. Content Integrity (Avg of lessons)
        if (!lessons.isEmpty()) {
            double avgPlagiarism = lessons.stream()
                    .filter(l -> l.getPlagiarismScore() != null)
                    .mapToDouble(ELessons::getPlagiarismScore)
                    .average().orElse(0.0);

            if (avgPlagiarism > 20) {
                score -= (avgPlagiarism * 2);
                findings.add(String.format("High average plagiarism across lessons (%.1f%%).", avgPlagiarism));
            }
        }

        course.setValidationFindings(String.join("|", findings));
        String status = score > 70 ? "PASSED" : (score > 40 ? "WARNING" : "REJECTED");

        return EduValidationResponseDTO.builder()
                .plagiarismScore(0.0) // Aggregated logic could be more complex
                .aiScore(course.getAiScore() != null ? course.getAiScore() : 0.0)
                .qualityScore(Math.max(0, score))
                .findings(findings)
                .status(status)
                .build();
    }
}
