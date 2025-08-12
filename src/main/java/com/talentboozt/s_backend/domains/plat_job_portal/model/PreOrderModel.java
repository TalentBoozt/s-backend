package com.talentboozt.s_backend.domains.plat_job_portal.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "portal_preorder")
public class PreOrderModel {
    @Id
    private String id;
    private String name;
    private String email;
    private String product;
    private String date;
}
