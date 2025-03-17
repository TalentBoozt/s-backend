package com.talentboozt.s_backend.Service.common.payment;

import com.talentboozt.s_backend.Model.common.payment.InvoicesModel;
import com.talentboozt.s_backend.Repository.common.payment.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoicesService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    public List<InvoicesModel> getAllInvoices() { return invoiceRepository.findAll(); }

    public List<InvoicesModel> getInvoicesByCompanyId(String companyId) { return invoiceRepository.findByCompanyId(companyId); }
}
