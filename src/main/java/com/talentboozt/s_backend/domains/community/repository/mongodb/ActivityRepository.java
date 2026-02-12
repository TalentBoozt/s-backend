package com.talentboozt.s_backend.domains.community.repository.mongodb;

import com.talentboozt.s_backend.domains.community.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ActivityRepository extends MongoRepository<Activity, String> {
    List<Activity> findByUserIdOrderByTimestampDesc(String userId);
}
