package com.talentboozt.s_backend.domains.subscription.model;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "feature_flags")
public class FeatureFlag {
    @Id
    private String id;

    @Indexed
    private ESubscriptionPlan plan;

    @Indexed
    private String featureKey;

    private boolean enabled;
}
