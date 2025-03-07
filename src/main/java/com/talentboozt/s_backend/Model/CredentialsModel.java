package com.talentboozt.s_backend.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "portal_credentials")
public class CredentialsModel {
    @Id
    private String id;
    private String employeeId;
    private String companyId;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String role;
    private String userLevel; // Free, Pro
}
