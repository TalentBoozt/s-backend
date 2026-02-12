package com.talentboozt.s_backend.domains.reputation.service;

import com.talentboozt.s_backend.domains.reputation.model.ReputationSourceType;

public interface ReputationScoringStrategy {
    int getScoreFor(ReputationSourceType type);
}
