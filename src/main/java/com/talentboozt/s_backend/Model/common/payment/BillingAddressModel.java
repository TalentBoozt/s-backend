package com.talentboozt.s_backend.Model.common.payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "portal_billing_address")
public class BillingAddressModel {
    @Id
    private String id;
    private String companyId;
    private String street;
    private String city;
    private String state;
    private String postal_code;
    private String country;
}
