package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.EmpContactModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpContactRepository extends MongoRepository<EmpContactModel, String> {
    List<EmpContactModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
