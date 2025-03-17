package com.talentboozt.s_backend.Repository.EndUser;

import com.talentboozt.s_backend.Model.EndUser.EmpFollowingModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpFollowingRepository extends MongoRepository<EmpFollowingModel, String> {
    List<EmpFollowingModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
