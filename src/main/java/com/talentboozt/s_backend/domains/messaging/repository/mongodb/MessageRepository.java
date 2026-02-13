package com.talentboozt.s_backend.domains.messaging.repository.mongodb;

import com.talentboozt.s_backend.domains.messaging.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    Page<Message> findByRoomId(String roomId, Pageable pageable);

    @org.springframework.data.mongodb.repository.Query("{ 'roomId': ?0, 'senderId': { '$ne': ?1 }, 'readByUsers.?2': { '$exists': false } }")
    long countUnreadMessages(String roomId, String senderId, String userId);

    java.util.Optional<Message> findFirstByRoomIdOrderByCreatedAtDesc(String roomId);
}
