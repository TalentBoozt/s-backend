package com.talentboozt.s_backend.Repository.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.AmbassadorTaskProgressModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaskProgressRepository extends MongoRepository<AmbassadorTaskProgressModel, String> {
    Optional<AmbassadorTaskProgressModel> findByAmbassadorIdAndTaskId(String id, String id1);

    List<AmbassadorTaskProgressModel> findByTaskType(String type);
}
