package com.talentboozt.s_backend.Repository.COM_JOB_PORTAL;

import com.talentboozt.s_backend.Model.COM_JOB_PORTAL.CmpPostedJobsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmpPostedJobsRepository extends MongoRepository<CmpPostedJobsModel, String> {

    List<CmpPostedJobsModel> findByCompanyId(String companyId);
}
