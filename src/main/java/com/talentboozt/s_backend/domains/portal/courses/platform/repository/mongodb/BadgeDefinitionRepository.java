package com.talentboozt.s_backend.domains.portal.courses.platform.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.courses.platform.model.BadgeDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BadgeDefinitionRepository extends MongoRepository<BadgeDefinition, String> {
}
