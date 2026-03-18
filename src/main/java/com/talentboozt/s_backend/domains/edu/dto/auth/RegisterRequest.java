package com.talentboozt.s_backend.domains.edu.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String role; // LEARNER, INSTRUCTOR, CREATOR
}
