package com.talentboozt.s_backend.domains.software_marketplace.repository.mongodb;

import com.talentboozt.s_backend.domains.software_marketplace.model.SoftwareAppModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoftwareAppRepository extends MongoRepository<SoftwareAppModel, String> {
    List<SoftwareAppModel> findByCompanyId(String companyId);

    List<SoftwareAppModel> findByIsGlobalTrue();
}
