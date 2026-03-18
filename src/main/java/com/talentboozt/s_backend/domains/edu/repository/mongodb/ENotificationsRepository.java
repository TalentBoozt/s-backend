package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.ENotifications;
import java.util.List;

@Repository
public interface ENotificationsRepository extends MongoRepository<ENotifications, String> {
    List<ENotifications> findByUserIdOrderByCreatedAtDesc(String userId);
    List<ENotifications> findByUserIdAndIsReadFalse(String userId);
}
