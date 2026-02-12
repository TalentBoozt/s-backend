package com.talentboozt.s_backend.domains.article.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleLikedEvent {
    private final String articleId;
    private final String userId; // User who liked
    private final String authorId; // Beneficiary
}
