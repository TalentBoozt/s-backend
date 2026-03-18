package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.ELessons;

@Repository
public interface ELessonsRepository extends MongoRepository<ELessons, String> {
}
