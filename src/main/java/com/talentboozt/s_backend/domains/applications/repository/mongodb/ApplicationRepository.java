package com.talentboozt.s_backend.domains.applications.repository.mongodb;

import com.talentboozt.s_backend.domains.applications.model.ApplicationModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ApplicationRepository extends MongoRepository<ApplicationModel, String> {
    List<ApplicationModel> findByJobId(String jobId);
    List<ApplicationModel> findByCompanyId(String companyId);
    List<ApplicationModel> findByEmployeeId(String employeeId);
}
