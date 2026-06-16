package com.talentboozt.s_backend.domains.portal.messaging.dto;

import com.talentboozt.s_backend.domains.portal.messaging.model.PresenceStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantDTO {
    private String userId;
    private String name;
    private String avatar;
    private PresenceStatus status;
}
