package com.talentboozt.s_backend.domains.edu.seo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

/**
 * MongoDB Instructor Profiles Collection Entity.
 * Maps educator profiles to MongoDB, configuring distinct indexing fields, text indexes,
 * and AI summary attributes.
 */
@Document(collection = "instructor_profiles")
public class InstructorProfileDocument {

    @Id
    private String id;

    @Indexed(unique = true, sparse = true)
    private String seoSlug;

    private String canonicalUrl;
    private String schemaJsonLd;
    private String aiSummary;

    @TextIndexed
    private List<String> semanticKeywords;

    private Boolean indexable = true;

    public InstructorProfileDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSeoSlug() { return seoSlug; }
    public void setSeoSlug(String seoSlug) { this.seoSlug = seoSlug; }

    public String getCanonicalUrl() { return canonicalUrl; }
    public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }

    public String getSchemaJsonLd() { return schemaJsonLd; }
    public void setSchemaJsonLd(String schemaJsonLd) { this.schemaJsonLd = schemaJsonLd; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public List<String> getSemanticKeywords() { return semanticKeywords; }
    public void setSemanticKeywords(List<String> semanticKeywords) { this.semanticKeywords = semanticKeywords; }

    public Boolean getIndexable() { return indexable; }
    public void setIndexable(Boolean indexable) { this.indexable = indexable; }
}
