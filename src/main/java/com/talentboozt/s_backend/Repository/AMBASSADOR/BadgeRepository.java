package com.talentboozt.s_backend.Repository.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.BadgeModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BadgeRepository extends MongoRepository<BadgeModel, String> {
    boolean existsByAmbassadorIdAndBadgeId(String ambassadorId, String badgeId);
}
