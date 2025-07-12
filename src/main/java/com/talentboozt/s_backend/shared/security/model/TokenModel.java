package com.talentboozt.s_backend.shared.security.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter

@Document(collection = "portal_tokens")
public class TokenModel {
    @Id
    private String id;
    private String token;
    private String username;
    private Date expiration;
    private boolean used;
}
