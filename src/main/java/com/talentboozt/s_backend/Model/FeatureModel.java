package com.talentboozt.s_backend.Model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "portal_feature-requests")
public class FeatureModel {
    @Id
    private String id;
    private String featureType;
    private String description;
    private String attachment;
}
