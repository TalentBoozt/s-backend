package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.EmpFollowersModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpFollowersRepository extends MongoRepository<EmpFollowersModel, String> {
    List<EmpFollowersModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
