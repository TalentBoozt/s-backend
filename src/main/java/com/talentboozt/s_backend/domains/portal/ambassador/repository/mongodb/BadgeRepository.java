package com.talentboozt.s_backend.domains.portal.ambassador.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.ambassador.model.BadgeModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BadgeRepository extends MongoRepository<BadgeModel, String> {
    boolean existsByAmbassadorIdAndBadgeId(String ambassadorId, String badgeId);
}
