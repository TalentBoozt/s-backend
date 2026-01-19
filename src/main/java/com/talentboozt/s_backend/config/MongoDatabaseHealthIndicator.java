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
            mongoTemplate.getDb().runCommand(org.bson.Document.parse("{ ping: 1 }"));
            
            return Health.up()
                    .withDetail("database", "MongoDB")
                    .withDetail("status", "Connected")
                    .withDetail("databaseName", mongoTemplate.getDb().getName())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "MongoDB")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
