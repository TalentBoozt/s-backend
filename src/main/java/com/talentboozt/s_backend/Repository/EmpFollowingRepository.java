package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.EmpFollowingModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpFollowingRepository extends MongoRepository<EmpFollowingModel, String> {
    List<EmpFollowingModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
