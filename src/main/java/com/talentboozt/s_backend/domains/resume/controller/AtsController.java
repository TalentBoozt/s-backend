package com.talentboozt.s_backend.domains.resume.controller;

import com.talentboozt.s_backend.domains.resume.service.AtsService;
import com.talentboozt.s_backend.shared.utils.DocumentExtractionService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v2/ats")
@RequiredArgsConstructor
public class AtsController {

    private final AtsService atsService;
    private final DocumentExtractionService documentExtractionService;

    @PostMapping("/analyze")
    @RateLimiter(name = "postLimiter")
    public ResponseEntity<AtsAnalysisResponse> analyzeResume(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "jobDescription", required = false) String jobDescription) {
        log.info("Analyzing resume: {} with JD: {}", file.getOriginalFilename(), jobDescription != null);

        try {
            // 1. Extract text
            String text = documentExtractionService.extractText(file);

            // 2. Real AI analysis
            AtsService.AtsAnalysisResult result = atsService.analyze(text, jobDescription);

            // 3. Map to response
            AtsAnalysisResponse response = new AtsAnalysisResponse();
            response.setScore(result.getScore());
            response.setFilename(file.getOriginalFilename());
            response.setImprovements(result.getImprovements());

            List<KeywordMatch> keywordMatches = result.getKeywords().stream()
                    .map(km -> new KeywordMatch(km.getKeyword(), km.isFound()))
                    .toList();
            response.setKeywords(keywordMatches);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Analysis failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Data
    public static class AtsAnalysisResponse {
        private int score;
        private String filename;
        private List<String> improvements;
        private List<KeywordMatch> keywords;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KeywordMatch {
        private String keyword;
        private boolean found;
    }
}
