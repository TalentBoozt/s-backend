package com.talentboozt.s_backend.domains.portal.reputation.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.reputation.model.UserBadge;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface UserBadgeRepository extends MongoRepository<UserBadge, String> {
    List<UserBadge> findByUserId(String userId);
}
