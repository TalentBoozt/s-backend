package com.talentboozt.s_backend.domains.edu.seo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

/**
 * MongoDB Courses Collection Entity.
 * Integrates comprehensive SEO configurations, dynamic translation grouping variables,
 * text search parameters, and generative AI retrieval components.
 */
@Document(collection = "courses")
public class CourseDocument {

    @Id
    private String id;

    @Indexed(name = "seoSlug", unique = true, sparse = true)
    private String seoSlug;

    @TextIndexed(weight = 3)
    private String seoTitle;

    @TextIndexed(weight = 2)
    private String seoDescription;

    private String seoKeywords;
    private String schemaJsonLd;
    private String canonicalUrl;

    @Indexed(name = "localizedLangGroupId", sparse = true)
    private String localizedLangGroupId;

    private Boolean indexable = true;
    private Boolean aiReady = true;
    private String aiSummary;

    @TextIndexed(weight = 1)
    private List<String> semanticKeywords;

    private String embeddingHints;

    @Indexed
    private Date updatedAt;

    @Indexed
    private Date publishedAt;

    // Default Constructor for Spring Data Instantiation
    public CourseDocument() {}

    // Getters and Setters for standard ORM integration
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSeoSlug() { return seoSlug; }
    public void setSeoSlug(String seoSlug) { this.seoSlug = seoSlug; }

    public String getSeoTitle() { return seoTitle; }
    public void setSeoTitle(String seoTitle) { this.seoTitle = seoTitle; }

    public String getSeoDescription() { return seoDescription; }
    public void setSeoDescription(String seoDescription) { this.seoDescription = seoDescription; }

    public String getSeoKeywords() { return seoKeywords; }
    public void setSeoKeywords(String seoKeywords) { this.seoKeywords = seoKeywords; }

    public String getSchemaJsonLd() { return schemaJsonLd; }
    public void setSchemaJsonLd(String schemaJsonLd) { this.schemaJsonLd = schemaJsonLd; }

    public String getCanonicalUrl() { return canonicalUrl; }
    public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }

    public String getLocalizedLangGroupId() { return localizedLangGroupId; }
    public void setLocalizedLangGroupId(String localizedLangGroupId) { this.localizedLangGroupId = localizedLangGroupId; }

    public Boolean getIndexable() { return indexable; }
    public void setIndexable(Boolean indexable) { this.indexable = indexable; }

    public Boolean getAiReady() { return aiReady; }
    public void setAiReady(Boolean aiReady) { this.aiReady = aiReady; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public List<String> getSemanticKeywords() { return semanticKeywords; }
    public void setSemanticKeywords(List<String> semanticKeywords) { this.semanticKeywords = semanticKeywords; }

    public String getEmbeddingHints() { return embeddingHints; }
    public void setEmbeddingHints(String embeddingHints) { this.embeddingHints = embeddingHints; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Date getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Date publishedAt) { this.publishedAt = publishedAt; }
}
