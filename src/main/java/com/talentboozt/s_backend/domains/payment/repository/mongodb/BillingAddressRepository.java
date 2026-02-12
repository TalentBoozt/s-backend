package com.talentboozt.s_backend.domains.payment.repository.mongodb;

import com.talentboozt.s_backend.domains.payment.model.BillingAddressModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BillingAddressRepository extends MongoRepository<BillingAddressModel, String> {
    List<BillingAddressModel> findByCompanyId(String companyId);
}
