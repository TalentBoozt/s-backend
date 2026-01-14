package com.talentboozt.s_backend.domains.plat_courses.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter

@Document("swag_items")
public class SwagItem {
    @Id
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private int inventory;
    private List<String> sizeOptions;
    private boolean enabled;
    private Instant createdAt;
}

//{
//  "_id": "tshirt_v1",
//  "title": "Talentboozt T-Shirt",
//  "description": "Premium cotton shirt with logo",
//  "imageUrl": "https://cdn.talnova.io/swag/tshirt_v1.png",
//  "inventory": 250,
//  "sizeOptions": ["S", "M", "L", "XL"],
//  "enabled": true,
//  "createdAt": ISODate("2025-07-01T00:00:00Z")
//}
