package com.talentboozt.s_backend.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Custom health indicators for production monitoring
 */
@Component
public class MongoDatabaseHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;

    public MongoDatabaseHealthIndicator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Health health() {
        try {
            // Perform a simple database operation to check connectivity
            @SuppressWarnings("null")
            org.bson.Document result = mongoTemplate.executeCommand(org.bson.Document.parse("{ ping: 1 }"));
            
            // Get database name safely
            String databaseName = mongoTemplate.getDb().getName();
            
            return Health.up()
                    .withDetail("database", "MongoDB")
                    .withDetail("status", "Connected")
                    .withDetail("databaseName", databaseName != null ? databaseName : "unknown")
                    .withDetail("ping", result != null ? "ok" : "unknown")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "MongoDB")
                    .withDetail("error", e.getMessage() != null ? e.getMessage() : "Unknown error")
                    .withDetail("exception", e.getClass().getSimpleName())
                    .build();
        }
    }
}
