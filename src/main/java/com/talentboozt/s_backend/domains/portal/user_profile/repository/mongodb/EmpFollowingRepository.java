package com.talentboozt.s_backend.domains.portal.user_profile.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.user_profile.model.EmpFollowingModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpFollowingRepository extends MongoRepository<EmpFollowingModel, String> {
    List<EmpFollowingModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
