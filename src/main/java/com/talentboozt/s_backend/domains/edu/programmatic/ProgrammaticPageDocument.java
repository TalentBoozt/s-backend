package com.talentboozt.s_backend.domains.edu.programmatic;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

/**
 * Programmatic Landing Page Entity.
 * Models scale-generated educational landing pages (e.g., tuition centres by district,
 * subject-specific hubs) inside the MongoDB collection "programmatic_pages".
 */
@Document(collection = "programmatic_pages")
public class ProgrammaticPageDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    private String title;
    private String seoTitle;
    private String seoDescription;
    private List<String> contentBlocks;
    private List<String> semanticKeywords;
    private Boolean indexable = true;
    private String lang = "en-LK";
    private Date generatedAt = new Date();
    private Double freshnessScore = 1.0;

    public ProgrammaticPageDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSeoTitle() { return seoTitle; }
    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }

    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }

    public List<String> getContentBlocks() { return contentBlocks; }
    public void setContentBlocks(List<String> contentBlocks) { this.contentBlocks = contentBlocks; }

    public List<String> getSemanticKeywords() { return semanticKeywords; }
    public void setSemanticKeywords(List<String> semanticKeywords) { this.semanticKeywords = semanticKeywords; }

    public Boolean getIndexable() { return indexable; }
    public void setIndexable(Boolean indexable) { this.indexable = indexable; }

    public String getLang() { return lang; }
    public void setLang(String lang) { this.lang = lang; }

    public Date getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Date generatedAt) { this.generatedAt = generatedAt; }

    public Double getFreshnessScore() { return freshnessScore; }
    public void setFreshnessScore(Double freshnessScore) { this.freshnessScore = freshnessScore; }
}
