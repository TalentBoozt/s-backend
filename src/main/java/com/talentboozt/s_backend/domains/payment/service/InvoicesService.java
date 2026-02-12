package com.talentboozt.s_backend.domains.payment.service;

import com.talentboozt.s_backend.domains.payment.model.InvoicesModel;
import com.talentboozt.s_backend.domains.payment.repository.mongodb.InvoiceRepository;

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
