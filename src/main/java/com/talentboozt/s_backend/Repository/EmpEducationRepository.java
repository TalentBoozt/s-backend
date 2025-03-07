package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.EmpEducationModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpEducationRepository extends MongoRepository<EmpEducationModel, String> {
    List<EmpEducationModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
