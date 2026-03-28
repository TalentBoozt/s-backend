package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.EBundles;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EBundlesRepository extends MongoRepository<EBundles, String> {
    List<EBundles> findByCreatorId(String creatorId);
    List<EBundles> findByCreatorIdAndStatus(String creatorId, String status);
}
