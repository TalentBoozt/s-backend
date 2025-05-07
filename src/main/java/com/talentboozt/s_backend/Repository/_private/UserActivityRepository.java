package com.talentboozt.s_backend.Repository._private;

import com.talentboozt.s_backend.Model._private.UserActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

public interface UserActivityRepository extends MongoRepository<UserActivity, String> {
    @Query(value = "{ 'lastActive': { $gte: ?0 } }", count = true)
    long countActiveUsers(LocalDateTime activeSince);

    @Query("{'encryptedIpAddress': ?0, 'endpointAccessed': ?1, 'timestamp': { $gte: ?2 }}")
    Optional<UserActivity> findRecentActivity(String encryptedIpAddress, String endpointAccessed, LocalDateTime fromTimestamp);

    @Query(value = "{ 'lastActive': { $gte: ?0 } }", count = true)
    long countByLastActiveAfter(Instant instant);
}

