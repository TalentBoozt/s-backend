package com.hris.HRIS_job_portal.Model.payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "portal_pre_payment_data")
public class PrePaymentModel {
    @Id
    private String id;
    private String companyId;
    private String firstname;
    private String lastname;
    private String country;
    private String address;
    private String phone;
    private String subscriptionId;
    private String paymentMethodId;
    private String billingAddressId;
    private String invoiceId;
    private String slipUrl;
    private String payType; // card or bank
    private String status; // pending or paid
}
