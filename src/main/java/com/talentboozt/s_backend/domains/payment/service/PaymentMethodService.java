package com.talentboozt.s_backend.domains.payment.service;

import com.talentboozt.s_backend.domains.payment.model.PaymentMethodsModel;
import com.talentboozt.s_backend.domains.payment.repository.mongodb.PaymentMethodRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PaymentMethodService {

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public List<PaymentMethodsModel> getPaymentMethods(String companyId) {
        return paymentMethodRepository.findByCompanyId(companyId);
    }

    public PaymentMethodsModel addPaymentMethod(String companyId, PaymentMethodsModel paymentMethod) {
        paymentMethod.setCompanyId(companyId);
        return paymentMethodRepository.save(paymentMethod);
    }

    public PaymentMethodsModel save(PaymentMethodsModel paymentMethodModel) {
        return paymentMethodRepository.save(Objects.requireNonNull(paymentMethodModel));
    }

    public boolean existsBySessionId(String id) {
        return paymentMethodRepository.existsBySessionId(id);
    }
}

