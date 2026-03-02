package com.talentboozt.s_backend.domains.article.service;

import com.talentboozt.s_backend.domains.article.dto.ArticleEvaluationDTO;
import com.talentboozt.s_backend.domains.article.model.Article;
import com.talentboozt.s_backend.domains.article.model.ArticleEvaluationLog;
import com.talentboozt.s_backend.domains.article.model.ArticleStatus;
import com.talentboozt.s_backend.domains.article.repository.mongodb.ArticleEvaluationLogRepository;
import com.talentboozt.s_backend.domains.article.repository.mongodb.ArticleRepository;
import com.talentboozt.s_backend.shared.ai.GeminiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ArticleValidationService {

    private final GeminiClient geminiClient;
    private final ArticleEvaluationLogRepository logRepository;
    private final ArticleRepository articleRepository;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    public void validateArticle(Article article) {
        String systemPrompt = """
                Role:
                You are an AI Article Quality Evaluation and Moderation Agent operating inside a publishing platform backend.

                Your task is to evaluate a submitted article for publication eligibility.
                You must perform structured validation, scoring, classification, and moderation flagging.

                You must respond strictly in structured JSON format.

                TARGET OBJECTIVES
                1. Evaluate content quality
                2. Detect policy violations or harmful intent
                3. Assess informational value
                4. Classify into a single best-fit category
                5. Score across defined dimensions
                6. Determine publish eligibility
                7. Flag high-value articles
                8. Flag low-quality articles for manual review
                9. Provide transparent reasoning
                10. Output machine-readable structured JSON only

                EVALUATION DIMENSIONS (0 to 100)
                1. Content Quality Score
                2. Informational Value Score
                3. Originality Score
                4. Engagement Potential Score
                5. Safety & Policy Compliance Score

                CATEGORY CLASSIFICATION
                Assign exactly one primary category from: Technology, Business, Finance, Health, Education, Science, Politics, Lifestyle, Entertainment, Sports, Opinion, General.

                PUBLISH DECISION LOGIC
                1. Auto-Publish: Content Quality >= 75 AND Informational Value >= 70 AND Safety Score >= 85 AND No critical policy violations. -> APPROVED, manualReviewRequired = false.
                2. High-Value: Informational Value >= 85 AND Content Quality >= 85. -> markAsHighValue = true, markAsInformative = true.
                3. Manual Review Required: Any score < 60 OR Safety score between 60-84 OR Potential policy concerns detected. -> PENDING_MANUAL_REVIEW, manualReviewRequired = true.
                4. Reject Criteria: Safety Score < 60 OR Clear policy violation OR Spam/Malicious. -> REJECTED, manualReviewRequired = false.

                If none match strictly, you can decide based on best judgement but adhere to the formatting.
                """;

        String userPrompt = "Title: " + article.getTitle() + "\n\nExcerpt: " + article.getExcerpt() + "\n\nContent:\n"
                + article.getContent();

        GeminiClient.AiResponse<ArticleEvaluationDTO> response = geminiClient.callStructuredApiWithRaw(
                systemPrompt,
                userPrompt,
                ArticleEvaluationDTO.class);

        ArticleEvaluationDTO result = response.parsed();

        // Save Audit Log
        ArticleEvaluationLog evaluationLog = ArticleEvaluationLog.builder()
                .articleId(article.getId())
                .validationVersion("1.0")
                .aiProvider("gemini")
                .promptVersion("1.0")
                .responseHash(String.valueOf(response.raw().hashCode()))
                .rawApiResponse(response.raw())
                .evaluationResult(result)
                .evaluatedAt(LocalDateTime.now())
                .build();

        logRepository.save(evaluationLog);

        // Update Article Based on Decision
        String publishStatus = result.getDecision().getPublishStatus();

        article.setMarkAsHighValue(result.getFlags().isMarkAsHighValue());
        article.setMarkAsInformative(result.getFlags().isMarkAsInformative());
        article.setManualReviewRequired(result.getFlags().isManualReviewRequired());

        if ("APPROVED".equals(publishStatus)) {
            article.setStatus(ArticleStatus.PUBLISHED);
            // Optionally set other flags like high value
            eventPublisher.publishEvent(
                    new com.talentboozt.s_backend.domains.article.event.ArticlePublishedEvent(this, article));
        } else if ("PENDING_MANUAL_REVIEW".equals(publishStatus)) {
            article.setStatus(ArticleStatus.PENDING_MANUAL_REVIEW);
        } else if ("REJECTED".equals(publishStatus)) {
            article.setStatus(ArticleStatus.REJECTED);
        }

        articleRepository.save(article);
    }
}
