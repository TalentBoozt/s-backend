package com.talentboozt.s_backend.Service.payment;

import com.talentboozt.s_backend.Model.payment.PaymentMethodsModel;
import com.talentboozt.s_backend.Repository.payment.PaymentMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return paymentMethodRepository.save(paymentMethodModel);
    }
}

