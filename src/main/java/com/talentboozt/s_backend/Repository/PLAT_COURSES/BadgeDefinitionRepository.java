package com.talentboozt.s_backend.Repository.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.BadgeDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BadgeDefinitionRepository extends MongoRepository<BadgeDefinition, String> {
}
