package com.talentboozt.s_backend.domains.support.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "support_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportRequestModel {
    @Id
    private String id;
    private String name;
    private String email;
    private String service;
    private String message;
    private String status; // PENDING, IN_PROGRESS, RESOLVED

    @CreatedDate
    private Instant createdAt;
}
