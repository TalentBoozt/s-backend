package com.talentboozt.s_backend.domains.plat_courses.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter

@Document("badges")
public class BadgeDefinition {
    @Id
    private String id;
    private String title;
    private String description;
    private String level;
    private String svgUrl;
    private boolean visible = true;
    private Instant createdAt;
    private List<String> tags;
}

//{
//  "_id": "top_referrer",
//  "title": "Top Referrer",
//  "description": "Awarded for referring the most users in a cycle.",
//  "level": "Gold", // Optional: Bronze, Silver, Gold etc.
//  "svgUrl": "https://cdn.talnova.io/badges/top_referrer.svg",
//  "visible": true,
//  "createdAt": ISODate("2025-07-01T00:00:00Z"),
//  "tags": ["referral", "top", "special"]
//}
