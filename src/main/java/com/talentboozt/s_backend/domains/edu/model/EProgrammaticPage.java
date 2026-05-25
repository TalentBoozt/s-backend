package com.talentboozt.s_backend.domains.edu.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

/**
 * Standardized Programmatic Landing Page Entity.
 * Models scale-generated educational landing pages inside the MongoDB collection "edu_programmatic_pages".
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_programmatic_pages")
public class EProgrammaticPage {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    private String title;
    private String seoTitle;
    private String seoDescription;
    private List<String> contentBlocks;
    private List<String> semanticKeywords;
    
    @Builder.Default
    private Boolean indexable = true;
    
    @Builder.Default
    private String lang = "en-LK";
    
    @Builder.Default
    private Date generatedAt = new Date();
    
    @Builder.Default
    private Double freshnessScore = 1.0;
}
