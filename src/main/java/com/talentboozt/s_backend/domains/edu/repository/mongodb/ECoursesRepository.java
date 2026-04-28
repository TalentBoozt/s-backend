package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.enums.ECourseStatus;
import com.talentboozt.s_backend.domains.edu.enums.ECourseValidationStatus;

import java.util.List;

@Repository
public interface ECoursesRepository extends MongoRepository<ECourses, String> {
    List<ECourses> findByCreatorId(String creatorId);
    List<ECourses> findByWorkspaceId(String workspaceId);

    List<ECourses> findByStatus(ECourseStatus status);
    List<ECourses> findByValidationStatusIn(List<ECourseValidationStatus> statuses);
    List<ECourses> findByPublishedTrueAndIsPrivateFalseAndStatus(ECourseStatus status);
}
