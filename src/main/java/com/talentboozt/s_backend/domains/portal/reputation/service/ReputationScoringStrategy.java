package com.talentboozt.s_backend.domains.portal.reputation.service;

import com.talentboozt.s_backend.domains.portal.reputation.model.ReputationSourceType;

public interface ReputationScoringStrategy {
    int getScoreFor(ReputationSourceType type);
}
