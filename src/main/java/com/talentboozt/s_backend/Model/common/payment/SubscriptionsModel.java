package com.talentboozt.s_backend.Model.common.payment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "portal_subscriptions")
public class SubscriptionsModel {
    @Id
    private String id;
    private String companyId;
    private String subscriptionId;
    private String plan_name; //Free, Premium
    private String start_date;
    private String end_date;
    private String cost;
    private String billing_cycle; //Monthly, Yearly
    private boolean is_active;
}
