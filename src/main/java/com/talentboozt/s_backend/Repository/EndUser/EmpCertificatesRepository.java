package com.talentboozt.s_backend.Repository.EndUser;

import com.talentboozt.s_backend.Model.EndUser.EmpCertificatesModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EmpCertificatesRepository extends MongoRepository<EmpCertificatesModel, String> {
    List<EmpCertificatesModel> findByEmployeeId(String employeeId);
    void deleteByEmployeeId(String employeeId);
}
