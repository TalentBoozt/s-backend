package com.talentboozt.s_backend.domains.edu.dto;

import java.time.Instant;

import lombok.Data;

@Data
public class EWChannelsDTO {
    private String id;
    private String name;
    private String description;
    private String type;
    private String status;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
