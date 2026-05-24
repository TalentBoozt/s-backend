package com.talentboozt.s_backend.domains.edu.seo.analytics;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

/**
 * Organic Search Keyword Metric Entity.
 * Maps search impressions, clicks, Click-Through Rates (CTR), and SERP ranking positions
 * in the MongoDB collection "seo_keyword_metrics".
 */
@Document(collection = "seo_keyword_metrics")
public class SeoKeywordMetricsDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String keyword;

    private Integer clicks;
    private Integer impressions;
    private Double ctr;
    private Double position;
    private Date updatedAt = new Date();

    public SeoKeywordMetricsDocument() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Integer getClicks() { return clicks; }
    public void setClicks(Integer clicks) { this.clicks = clicks; }

    public Integer getImpressions() { return impressions; }
    public void setImpressions(Integer impressions) { this.impressions = impressions; }

    public Double getCtr() { return ctr; }
    public void setCtr(Double ctr) { this.ctr = ctr; }

    public Double getPosition() { return position; }
    public void setPosition(Double position) { this.position = position; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
