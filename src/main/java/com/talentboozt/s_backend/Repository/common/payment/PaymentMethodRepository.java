package com.talentboozt.s_backend.Repository.common.payment;

import com.talentboozt.s_backend.Model.common.payment.PaymentMethodsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentMethodRepository extends MongoRepository<PaymentMethodsModel, String> {
    List<PaymentMethodsModel> findByCompanyId(String companyId);
}
