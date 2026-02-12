package com.talentboozt.s_backend.domains.reputation.service;

import com.talentboozt.s_backend.domains.reputation.model.ReputationSourceType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultReputationScoringStrategy implements ReputationScoringStrategy {

    private final Map<ReputationSourceType, Integer> weights = Map.of(
            ReputationSourceType.ARTICLE_PUBLISH, 20,
            ReputationSourceType.ARTICLE_LIKE, 5,
            ReputationSourceType.ARTICLE_BOOKMARK, 3,
            ReputationSourceType.POST_CREATE, 2,
            ReputationSourceType.POST_UPVOTE, 2,
            ReputationSourceType.COMMENT_CREATE, 3,
            ReputationSourceType.COMMENT_UPVOTE, 3,
            ReputationSourceType.REACTION_RECEIVE, 1,
            ReputationSourceType.SPAM_PENALTY, -10);

    @Override
    public int getScoreFor(ReputationSourceType type) {
        return weights.getOrDefault(type, 0);
    }
}
