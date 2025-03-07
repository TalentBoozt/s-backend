package com.talentboozt.s_backend.Model.payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "portal_billing_history")
public class BillingHistoryModel {
    @Id
    private String id;
    private String companyId;
    private String amount;
    private String date;
    private String invoice_id;
    private String status;
}
