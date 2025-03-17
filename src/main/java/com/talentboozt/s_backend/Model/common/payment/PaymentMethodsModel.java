package com.talentboozt.s_backend.Model.common.payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "portal_payment_methods")
public class PaymentMethodsModel {
    @Id
    private String id;
    private String companyId;
    private String type; //paypal, stripe, credit card
    private String last_four; //last four digits of credit card for security purposes
    private String expiry_date;
    private String is_default;
}
