package com.talentboozt.s_backend.domains.user.repository;

import com.talentboozt.s_backend.domains.user.model.EmpFollowersModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpFollowersRepository extends MongoRepository<EmpFollowersModel, String> {
    List<EmpFollowersModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
