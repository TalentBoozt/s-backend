package com.talentboozt.s_backend.domains.user.repository.mongodb;

import com.talentboozt.s_backend.domains.user.model.EmpEducationModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpEducationRepository extends MongoRepository<EmpEducationModel, String> {

    List<EmpEducationModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
