package com.talentboozt.s_backend.domains.activity.repository.mongodb;

import com.talentboozt.s_backend.domains.activity.model.JPActivityModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface JPActivityRepository extends MongoRepository<JPActivityModel, String> {
    Page<JPActivityModel> findByUserIdOrderByTimestampDesc(String userId, Pageable pageable);
    Page<JPActivityModel> findByOrganizationIdOrderByTimestampDesc(String organizationId, Pageable pageable);
    List<JPActivityModel> findTop10ByOrganizationIdOrderByTimestampDesc(String organizationId);
}
