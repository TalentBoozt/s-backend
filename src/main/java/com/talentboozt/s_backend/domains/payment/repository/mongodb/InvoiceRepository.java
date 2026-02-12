package com.talentboozt.s_backend.domains.payment.repository.mongodb;

import com.talentboozt.s_backend.domains.payment.model.InvoicesModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InvoiceRepository extends MongoRepository<InvoicesModel, String> {
    List<InvoicesModel> findByCompanyId(String companyId);
    InvoicesModel findByInvoiceId(String invoiceId);

    boolean existsBySessionId(String sessionId);
}
