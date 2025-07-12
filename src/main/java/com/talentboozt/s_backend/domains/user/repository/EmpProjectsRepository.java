package com.talentboozt.s_backend.domains.user.repository;

import com.talentboozt.s_backend.domains.user.model.EmpProjectsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpProjectsRepository extends MongoRepository<EmpProjectsModel, String> {
    List<EmpProjectsModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
