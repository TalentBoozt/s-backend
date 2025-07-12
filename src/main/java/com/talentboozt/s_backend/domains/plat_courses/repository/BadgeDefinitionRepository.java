package com.talentboozt.s_backend.domains.plat_courses.repository;

import com.talentboozt.s_backend.domains.plat_courses.model.BadgeDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BadgeDefinitionRepository extends MongoRepository<BadgeDefinition, String> {
}
