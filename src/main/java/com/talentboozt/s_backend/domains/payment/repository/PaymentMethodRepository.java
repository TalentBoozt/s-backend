package com.talentboozt.s_backend.domains.payment.repository;

import com.talentboozt.s_backend.domains.payment.model.PaymentMethodsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentMethodRepository extends MongoRepository<PaymentMethodsModel, String> {
    List<PaymentMethodsModel> findByCompanyId(String companyId);
}
