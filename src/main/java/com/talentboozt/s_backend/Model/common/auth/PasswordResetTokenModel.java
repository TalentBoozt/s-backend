package com.talentboozt.s_backend.Model.common.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter

@Document(collection = "portal_password_reset_tokens")
public class PasswordResetTokenModel {
    @Id
    private String id;
    private String token;
    private String userId; // User's MongoDB ID
    private LocalDateTime expirationDate;

    public PasswordResetTokenModel(String token, String userId, LocalDateTime expirationDate) {
        this.token = token;
        this.userId = userId;
        this.expirationDate = expirationDate;
    }

    public boolean isExpired() {
        return expirationDate.isBefore(LocalDateTime.now());
    }
}
