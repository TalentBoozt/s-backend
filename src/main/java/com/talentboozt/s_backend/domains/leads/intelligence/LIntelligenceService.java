package com.talentboozt.s_backend.domains.leads.intelligence;

import com.talentboozt.s_backend.domains.leads.events.LNewSignalEvent;
import com.talentboozt.s_backend.domains.leads.model.LRawSignal;
import com.talentboozt.s_backend.domains.leads.repository.LRawSignalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class LIntelligenceService {

    private static final Logger log = LoggerFactory.getLogger(LIntelligenceService.class);

    private final LIntentClassifier intentClassifier;
    private final LRawSignalRepository rawSignalRepository;
    private final com.talentboozt.s_backend.domains.leads.crm.service.LLeadService leadService;

    private static final double SCORE_THRESHOLD = 50.0;

    public LIntelligenceService(
            LIntentClassifier intentClassifier,
            LRawSignalRepository rawSignalRepository,
            com.talentboozt.s_backend.domains.leads.crm.service.LLeadService leadService) {
        this.intentClassifier = intentClassifier;
        this.rawSignalRepository = rawSignalRepository;
        this.leadService = leadService;
    }

    @EventListener
    public void handleNewSignalEvent(LNewSignalEvent event) {
        LRawSignal signal = event.getRawSignal();
        // log.info("Processing new signal for AI Intelligence (ID: {})", signal.getId());

        try {
            LAIAnalysisResult analysis = intentClassifier.analyzeContent(signal.getContent());

            double baseScore = (analysis.getIntentWeight() * 0.4)
                    + (analysis.getUrgency() * 0.3)
                    + (analysis.getEngagementWeight() * 0.2)
                    + (analysis.getRecencyWeight() * 0.1);

            // Apply Multipliers
            double score = baseScore;
            if ("BUYING".equalsIgnoreCase(analysis.getIntent())) {
                score *= 1.25; // 25% boost for explicit buying intent
            } else if ("NOISE".equalsIgnoreCase(analysis.getIntent())) {
                score *= 0.2; // 80% reduction for noise
            }

            // Sentiment adjustment: High pain/frustration (-0.5 or lower) often indicates higher qualifying potential
            if (analysis.getSentiment() <= -0.5) {
                score *= 1.15;
            }

            // Cap at 100
            score = Math.min(100.0, score);

            // Update Signal
            signal.setIntent(analysis.getIntent());
            signal.setScore(score);
            signal.setTags(analysis.getTags());
            signal.setStatus("PROCESSED");
            rawSignalRepository.save(signal);

            log.info("Signal {} classified and scored: {} | Intent: {}", signal.getId(), score, analysis.getIntent());

            // Create CRM Lead if meets threshold and intent is not NOISE
            if (!"NOISE".equalsIgnoreCase(analysis.getIntent()) && score >= SCORE_THRESHOLD) {
                com.talentboozt.s_backend.domains.leads.crm.model.LLead lead = leadService.autoConvert(signal);
                log.info("CRM Lead created automatically from Signal ({})", signal.getId());
            }

        } catch (Exception e) {
            log.error("Failed to process AI Intelligence for Signal ID: {}", signal.getId(), e);
        }
    }

    private String extractSummary(String content) {
        if (content == null)
            return "";
        if (content.length() <= 150)
            return content;
        return content.substring(0, 147) + "...";
    }
}
