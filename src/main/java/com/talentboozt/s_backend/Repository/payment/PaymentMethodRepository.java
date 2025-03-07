package com.talentboozt.s_backend.Repository.payment;

import com.talentboozt.s_backend.Model.payment.PaymentMethodsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PaymentMethodRepository extends MongoRepository<PaymentMethodsModel, String> {
    List<PaymentMethodsModel> findByCompanyId(String companyId);
}
