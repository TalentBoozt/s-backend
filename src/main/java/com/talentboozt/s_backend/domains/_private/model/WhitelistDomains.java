package com.talentboozt.s_backend.domains._private.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter

@Document(collection = "portal_whitelist_domains")
public class WhitelistDomains {
    @Id
    private String id;
    private String domain;
    private boolean active;
    private String requestBy;
}
