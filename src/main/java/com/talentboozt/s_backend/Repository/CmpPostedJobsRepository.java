package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.CmpPostedJobsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CmpPostedJobsRepository extends MongoRepository<CmpPostedJobsModel, String> {

    List<CmpPostedJobsModel> findByCompanyId(String companyId);
}
