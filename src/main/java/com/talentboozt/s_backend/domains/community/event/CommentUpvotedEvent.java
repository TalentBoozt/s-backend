package com.talentboozt.s_backend.domains.community.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentUpvotedEvent {
    private final String commentId;
    private final String userId; // User who upvoted
    private final String authorId; // Beneficiary
}
