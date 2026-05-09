package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.EnterpriseInquiry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnterpriseInquiryRepository extends MongoRepository<EnterpriseInquiry, String> {
    List<EnterpriseInquiry> findByUserId(String userId);
    List<EnterpriseInquiry> findByStatus(String status);
}
