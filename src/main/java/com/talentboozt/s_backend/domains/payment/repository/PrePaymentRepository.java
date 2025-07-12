package com.talentboozt.s_backend.domains.payment.repository;

import com.talentboozt.s_backend.domains.payment.model.PrePaymentModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PrePaymentRepository extends MongoRepository<PrePaymentModel, String> {
    Optional<PrePaymentModel> findByCompanyId(String companyId);
    Optional<PrePaymentModel> findBySubscriptionId(String subscriptionId);
    Optional<PrePaymentModel> findByPaymentMethodId(String paymentMethodId);
    Optional<PrePaymentModel> findByInvoiceId(String invoiceId);
    List<PrePaymentModel> findAllByPayType(String invoiceId);
    List<PrePaymentModel> findAllByStatus(String status);
}
