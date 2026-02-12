package com.talentboozt.s_backend.domains.reputation.model;

public enum ReputationSourceType {
    ARTICLE_PUBLISH,
    ARTICLE_LIKE,
    ARTICLE_BOOKMARK,
    POST_CREATE,
    POST_UPVOTE,
    COMMENT_CREATE,
    COMMENT_UPVOTE,
    REACTION_RECEIVE,
    SPAM_PENALTY
}
