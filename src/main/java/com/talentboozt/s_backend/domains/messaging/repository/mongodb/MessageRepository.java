package com.talentboozt.s_backend.domains.messaging.repository.mongodb;

import com.talentboozt.s_backend.domains.messaging.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    @org.springframework.data.mongodb.repository.Query("{ 'roomId': ?0, 'deletedForUsers': { '$ne': ?1 } }")
    Page<Message> findByRoomIdAndDeletedForUsersNotContaining(String roomId, String userId, Pageable pageable);

    @org.springframework.data.mongodb.repository.Query(value = "{ 'roomId': ?0, 'senderId': { '$ne': ?1 }, 'readByUsers.?2': { '$exists': false }, 'deletedForUsers': { '$ne': ?2 } }", count = true)
    Long countUnreadMessages(String roomId, String senderId, String userId);

    @org.springframework.data.mongodb.repository.Query(value = "{ 'roomId': ?0, 'deletedForUsers': { '$ne': ?1 } }", sort = "{ 'createdAt': -1 }")
    java.util.Optional<Message> findLatestActiveMessage(String roomId, String userId);
}
