package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.talentboozt.s_backend.domains.edu.model.ELessons;

@Repository
public interface ELessonsRepository extends MongoRepository<ELessons, String> {
    List<ELessons> findByCourseId(String courseId);
    List<ELessons> findBySectionId(String sectionId);
    Integer countByCourseId(String courseId);
}
