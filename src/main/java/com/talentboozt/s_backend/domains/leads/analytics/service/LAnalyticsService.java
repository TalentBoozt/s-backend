package com.talentboozt.s_backend.domains.leads.analytics.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class LAnalyticsService {

    private final MongoTemplate mongoTemplate;

    public LAnalyticsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Cacheable(value = "leadAnalytics", key = "#workspaceId")
    public Map<String, Object> getWorkspaceAnalytics(String workspaceId) {
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);

        Map<String, Object> analytics = new HashMap<>();

        // 1. Total Leads (last 30 days)
        Aggregation totalLeadsAgg = newAggregation(
                match(Criteria.where("workspaceId").is(workspaceId)
                        .and("createdAt").gte(thirtyDaysAgo)),
                group().count().as("total")
        );
        AggregationResults<Map> totalLeadsResult = mongoTemplate.aggregate(totalLeadsAgg, "leads_leads", Map.class);
        long totalLeads = totalLeadsResult.getMappedResults().isEmpty() ? 0 : 
                ((Number) totalLeadsResult.getMappedResults().get(0).get("total")).longValue();
        analytics.put("totalLeads", totalLeads);

        // 2. Conversion Rate (Converted / Total)
        Aggregation convertedAgg = newAggregation(
                match(Criteria.where("workspaceId").is(workspaceId)
                        .and("status").is("CONVERTED")),
                group().count().as("total")
        );
        AggregationResults<Map> convertedResult = mongoTemplate.aggregate(convertedAgg, "leads_leads", Map.class);
        long convertedCount = convertedResult.getMappedResults().isEmpty() ? 0 : 
                ((Number) convertedResult.getMappedResults().get(0).get("total")).longValue();
        double conversionRate = totalLeads == 0 ? 0.0 : (double) convertedCount / totalLeads * 100.0;
        analytics.put("conversionRate", Math.round(conversionRate * 10.0) / 10.0);

        // 3. Platform Distribution
        Aggregation platformAgg = newAggregation(
                match(Criteria.where("workspaceId").is(workspaceId)),
                group("platform").count().as("value"),
                project("value").and("name").previousOperation()
        );
        AggregationResults<Map> platformDistResult = mongoTemplate.aggregate(platformAgg, "leads_leads", Map.class);
        analytics.put("platformDistribution", platformDistResult.getMappedResults());

        // 4. Status Distribution
        Aggregation statusAgg = newAggregation(
                match(Criteria.where("workspaceId").is(workspaceId)),
                group("status").count().as("count"),
                project("count").and("status").previousOperation()
        );
        AggregationResults<Map> statusDistResult = mongoTemplate.aggregate(statusAgg, "leads_leads", Map.class);
        analytics.put("statusDistribution", statusDistResult.getMappedResults());

        // 5. Keyword / Tags Performance (Unwind tags and count)
        Aggregation tagsAgg = newAggregation(
                match(Criteria.where("workspaceId").is(workspaceId)),
                unwind("tags"),
                group("tags").count().as("count"),
                project("count").and("tag").previousOperation(),
                sort(org.springframework.data.domain.Sort.Direction.DESC, "count"),
                limit(10)
        );
        AggregationResults<Map> tagsResult = mongoTemplate.aggregate(tagsAgg, "leads_leads", Map.class);
        analytics.put("topTags", tagsResult.getMappedResults());

        return analytics;
    }
}
