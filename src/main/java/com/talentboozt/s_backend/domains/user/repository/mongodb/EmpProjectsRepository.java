package com.talentboozt.s_backend.domains.user.repository.mongodb;

import com.talentboozt.s_backend.domains.user.model.EmpProjectsModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpProjectsRepository extends MongoRepository<EmpProjectsModel, String> {
    List<EmpProjectsModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
