package com.talentboozt.s_backend.domains.community.event;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContentCreatedEvent {
    private final String targetId;
    private final String type; // "POST" or "COMMENT"
    private final String text;
    private final String authorId;
}
