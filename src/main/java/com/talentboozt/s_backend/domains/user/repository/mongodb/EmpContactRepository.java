package com.talentboozt.s_backend.domains.user.repository.mongodb;

import com.talentboozt.s_backend.domains.user.model.EmpContactModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpContactRepository extends MongoRepository<EmpContactModel, String> {
    List<EmpContactModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
