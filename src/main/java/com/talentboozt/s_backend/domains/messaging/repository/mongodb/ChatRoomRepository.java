package com.talentboozt.s_backend.domains.messaging.repository.mongodb;

import com.talentboozt.s_backend.domains.messaging.model.ChatRoom;
import com.talentboozt.s_backend.domains.messaging.model.RoomType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    List<ChatRoom> findByParticipantsContaining(String userId);

    List<ChatRoom> findByCommunityId(String communityId);

    List<ChatRoom> findByTypeAndParticipantsAllIgnoreCase(RoomType type, List<String> participants);
}
