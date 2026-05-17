package com.talentboozt.s_backend.domains.automation.service;

import com.talentboozt.s_backend.domains.automation.model.AutomationRule;
import com.talentboozt.s_backend.domains.automation.repository.mongodb.AutomationRuleRepository;
import com.talentboozt.s_backend.domains.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AutomationService {
    private final AutomationRuleRepository repository;
    private final NotificationService notificationService;

    public void triggerAutomation(String orgId, String triggerType, Map<String, Object> context) {
        List<AutomationRule> activeRules = repository.findByOrganizationIdAndTriggerTypeAndActiveTrue(orgId, triggerType);
        
        for (AutomationRule rule : activeRules) {
            if (evaluateConditions(rule.getConditions(), context)) {
                executeActions(rule.getActions(), context);
            }
        }
    }

    private boolean evaluateConditions(Map<String, Object> conditions, Map<String, Object> context) {
        // Simple condition evaluation logic (In production, use SpEL or a rule engine)
        return true; 
    }

    private void executeActions(List<AutomationRule.Action> actions, Map<String, Object> context) {
        for (AutomationRule.Action action : actions) {
            switch (action.getType()) {
                case "NOTIFY_RECRUITER":
                    notificationService.createNotification(
                        (String) action.getParameters().get("recruiterId"),
                        "Automation Triggered: " + action.getParameters().get("title"),
                        (String) action.getParameters().get("message"),
                        "AUTOMATION",
                        "/recruiter/pipeline"
                    );
                    break;
                // Implement other actions...
            }
        }
    }
}
