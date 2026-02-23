package com.talentboozt.s_backend.domains.messaging.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_rooms")
public class ChatRoom {
    @Id
    private String id;
    private RoomType type;
    private String name; // For GROUP rooms
    private List<String> participants; // User IDs
    private String communityId; // For COMMUNITY type
    private LocalDateTime createdAt;

    private List<String> pinnedBy;
    private List<String> archivedBy;
    private List<String> favoritedBy;
}
