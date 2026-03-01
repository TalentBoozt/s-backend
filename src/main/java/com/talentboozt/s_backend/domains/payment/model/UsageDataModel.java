package com.talentboozt.s_backend.domains.payment.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "portal_usage_data")
public class UsageDataModel {
    @Id
    private String id;
    private String companyId;
    private String job_posts_allowed;
    private String job_posts_used;
    private String applicant_views_allowed;
    private String applicant_views_used;
    private String overage_charges; //Any extra charges for exceeding usage limits
    private Integer users;
    private Integer storage;
    private Integer bandwidth;
}
