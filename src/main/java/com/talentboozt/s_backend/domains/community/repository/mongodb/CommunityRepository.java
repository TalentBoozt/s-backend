package com.talentboozt.s_backend.domains.community.repository.mongodb;

import com.talentboozt.s_backend.domains.community.model.Community;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommunityRepository extends MongoRepository<Community, String> {
}
