package com.talentboozt.s_backend.Repository.common.payment;

import com.talentboozt.s_backend.Model.common.payment.InvoicesModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InvoiceRepository extends MongoRepository<InvoicesModel, String> {
    List<InvoicesModel> findByCompanyId(String companyId);
    InvoicesModel findByInvoiceId(String invoiceId);
}
