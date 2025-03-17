package com.talentboozt.s_backend.Repository.EndUser;

import com.talentboozt.s_backend.Model.EndUser.EmpProjectsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpProjectsRepository extends MongoRepository<EmpProjectsModel, String> {
    List<EmpProjectsModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
