package com.talentboozt.s_backend.shared.notification.automation.repository.mongodb;

import com.talentboozt.s_backend.shared.notification.automation.model.AutomationRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface AutomationRuleRepository extends MongoRepository<AutomationRule, String> {
    List<AutomationRule> findByOrganizationIdAndTriggerTypeAndActiveTrue(String organizationId, String triggerType);
}
