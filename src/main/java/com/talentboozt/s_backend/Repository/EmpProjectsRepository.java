package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.EmpProjectsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpProjectsRepository extends MongoRepository<EmpProjectsModel, String> {
    List<EmpProjectsModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
