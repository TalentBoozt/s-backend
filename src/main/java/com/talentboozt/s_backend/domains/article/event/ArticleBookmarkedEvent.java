package com.talentboozt.s_backend.domains.article.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArticleBookmarkedEvent {
    private final String articleId;
    private final String userId;
    private final String authorId;
}
