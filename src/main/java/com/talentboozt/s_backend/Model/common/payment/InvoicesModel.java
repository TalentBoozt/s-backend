package com.talentboozt.s_backend.Model.common.payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@ToString

@Document(collection = "portal_invoices")
public class InvoicesModel {
    @Id
    private String id;
    private String companyId;
    private String invoiceId;
    private String subscriptionId;
    private String amountDue;
    private Date billingDate;
    private Date dueDate;
    private Date periodStart;
    private Date periodEnd;
    private String status;
}
