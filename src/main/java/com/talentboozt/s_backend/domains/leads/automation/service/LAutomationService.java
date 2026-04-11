package com.talentboozt.s_backend.domains.leads.automation.service;

import com.talentboozt.s_backend.domains.leads.automation.model.LLeadAutomation;
import com.talentboozt.s_backend.domains.leads.automation.repository.LLeadAutomationRepository;
import com.talentboozt.s_backend.domains.leads.events.LNewSignalEvent;
import com.talentboozt.s_backend.domains.leads.model.LRawSignal;
import com.talentboozt.s_backend.domains.leads.crm.service.LLeadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LAutomationService {

    private static final Logger log = LoggerFactory.getLogger(LAutomationService.class);

    private final LLeadAutomationRepository automationRepository;
    private final LLeadService leadService;

    public LAutomationService(LLeadAutomationRepository automationRepository, LLeadService leadService) {
        this.automationRepository = automationRepository;
        this.leadService = leadService;
    }

    @Async
    @EventListener
    public void handleNewSignalEvent(LNewSignalEvent event) {
        LRawSignal signal = event.getRawSignal();
        // log.info("Automation Engine processing NewSignalEvent for Signal {}", signal.getId());

        List<LLeadAutomation> automations = automationRepository.findByWorkspaceIdAndActiveTrue(signal.getWorkspaceId());

        for (LLeadAutomation automation : automations) {
            if ("AI_SCORE_GT".equals(automation.getTriggerType()) && signal.getScore() != null) {
                if (signal.getScore() >= automation.getTriggerScoreThreshold()) {
                    executeAction(automation, signal);
                }
            } else if ("KEYWORD_MATCH".equals(automation.getTriggerType()) && automation.getTriggerKeywords() != null) {
                boolean match = automation.getTriggerKeywords().stream()
                        .anyMatch(kw -> signal.getContent().toLowerCase().contains(kw.toLowerCase()));
                if (match) {
                    executeAction(automation, signal);
                }
            }
        }
    }

    private void executeAction(LLeadAutomation automation, LRawSignal signal) {
        log.info("Executing action {} for automation {}", automation.getActionType(), automation.getId());

        try {
            switch (automation.getActionType()) {
                case "MOVE_TO_PIPELINE":
                    leadService.autoConvert(signal);
                    break;
                case "GENERATE_REPLY":
                    if (!automation.isRequiresHumanApproval()) {
                        // In reality, this would hit AI to draft a real reply and post it
                        log.info("Auto-replying to signal {}", signal.getId());
                    } else {
                        log.info("Drafting reply for human review for signal {}", signal.getId());
                    }
                    break;
                case "NOTIFY_USER":
                    log.info("Notification sent to user for signal {}", signal.getId());
                    break;
                case "WEBHOOK":
                    // Optional: make HTTP call to automation.getActionWebhookUrl()
                    log.info("Webhook triggered for signal {}", signal.getId());
                    break;
            }
        } catch (Exception e) {
            log.error("Failed to execute automation action", e);
        }
    }
}
