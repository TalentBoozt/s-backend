package com.talentboozt.s_backend.domains.community.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.community.model.Community;

import java.util.Optional;

@Repository
public interface CommunityRepository extends MongoRepository<Community, String> {
    Optional<Community> findByName(String name);
}
