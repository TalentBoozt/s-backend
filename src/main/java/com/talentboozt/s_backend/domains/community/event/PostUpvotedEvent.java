package com.talentboozt.s_backend.domains.community.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostUpvotedEvent {
    private final String postId;
    private final String userId; // User who upvoted
    private final String authorId; // Beneficiary
}
