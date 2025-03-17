package com.talentboozt.s_backend.Repository.common.payment;

import com.talentboozt.s_backend.Model.common.payment.BillingAddressModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BillingAddressRepository extends MongoRepository<BillingAddressModel, String> {
    List<BillingAddressModel> findByCompanyId(String companyId);
}
