package com.talentboozt.s_backend.Repository.EndUser;

import com.talentboozt.s_backend.Model.EndUser.EmpContactModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpContactRepository extends MongoRepository<EmpContactModel, String> {
    List<EmpContactModel> findByEmployeeId(String employeeId);

    void deleteByEmployeeId(String employeeId);
}
