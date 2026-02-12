package com.talentboozt.s_backend.domains.payment.service;

import com.talentboozt.s_backend.domains.payment.model.PrePaymentModel;
import com.talentboozt.s_backend.domains.payment.repository.mongodb.PrePaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class PrePaymentService {

    @Autowired
    PrePaymentRepository prePaymentRepository;

    public PrePaymentModel save(PrePaymentModel prePaymentModel) {
        return prePaymentRepository.save(Objects.requireNonNull(prePaymentModel));
    }

    public void updateSubscriptionId(String companyId, String subscriptionId){
        Optional<PrePaymentModel> prePaymentModel = prePaymentRepository.findBySubscriptionId(companyId);
        if(prePaymentModel.isPresent()){
            PrePaymentModel prePayment = prePaymentModel.get();
            prePayment.setSubscriptionId(subscriptionId);
            prePaymentRepository.save(prePayment);
        }
    }

    public void updatePaymentMethodId(String companyId, String paymentMethodId){
        Optional<PrePaymentModel> prePaymentModel = prePaymentRepository.findBySubscriptionId(companyId);
        if(prePaymentModel.isPresent()){
            PrePaymentModel prePayment = prePaymentModel.get();
            prePayment.setPaymentMethodId(paymentMethodId);
            prePaymentRepository.save(prePayment);
        }
    }

    public PrePaymentModel updateBillingAddressId(String companyId, String billingAddressId){
        Optional<PrePaymentModel> prePaymentModel = prePaymentRepository.findBySubscriptionId(companyId);
        if(prePaymentModel.isPresent()){
            PrePaymentModel prePayment = prePaymentModel.get();
            prePayment.setBillingAddressId(billingAddressId);
            return prePaymentRepository.save(prePayment);
        }
        return null;
    }

    public void updateInvoiceId(String companyId, String invoiceId){
        Optional<PrePaymentModel> prePaymentModel = prePaymentRepository.findBySubscriptionId(companyId);
        if(prePaymentModel.isPresent()){
            PrePaymentModel prePayment = prePaymentModel.get();
            prePayment.setInvoiceId(invoiceId);
            prePaymentRepository.save(prePayment);
        }
    }

    public PrePaymentModel updatePayType(String companyId, String payType){
        Optional<PrePaymentModel> prePaymentModel = prePaymentRepository.findBySubscriptionId(companyId);
        if(prePaymentModel.isPresent()){
            PrePaymentModel prePayment = prePaymentModel.get();
            prePayment.setPayType(payType);
            return prePaymentRepository.save(prePayment);
        }
        return null;
    }

    public void updateStatus(String companyId, String status){
        Optional<PrePaymentModel> prePaymentModel = prePaymentRepository.findBySubscriptionId(companyId);
        if(prePaymentModel.isPresent()){
            PrePaymentModel prePayment = prePaymentModel.get();
            prePayment.setStatus(status);
            prePaymentRepository.save(prePayment);
        }
    }
}
