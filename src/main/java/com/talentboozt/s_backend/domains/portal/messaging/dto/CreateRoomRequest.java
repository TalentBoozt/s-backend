package com.talentboozt.s_backend.domains.portal.messaging.dto;

import com.talentboozt.s_backend.domains.portal.messaging.model.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {
    private RoomType type;
    private String name;
    private List<String> participants;
    private String communityId;
}
