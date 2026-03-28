package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.talentboozt.s_backend.domains.edu.model.ECourseSections;

@Repository
public interface ECourseSectionsRepository extends MongoRepository<ECourseSections, String> {
    List<ECourseSections> findByCourseId(String courseId);
}
