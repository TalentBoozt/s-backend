package com.talentboozt.s_backend.config;

import com.talentboozt.s_backend.shared.security.cfg.DynamicMongoDatabaseFactory;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


//@Configuration
//public class MongoConfig {
//
////    private final DynamicMongoDatabaseFactory dynamicMongoDatabaseFactory;
////
////    public MongoConfig(DynamicMongoDatabaseFactory dynamicMongoDatabaseFactory) {
////        this.dynamicMongoDatabaseFactory = dynamicMongoDatabaseFactory;
////    }
//
//    @Bean
////    @RequestScope
//    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory,
//                                       MappingMongoConverter mappingMongoConverter) {
//        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, mappingMongoConverter);
////        MongoTemplate mongoTemplate = new MongoTemplate(dynamicMongoDatabaseFactory.getMongoDatabaseFactory(), mappingMongoConverter);
//
//        mongoTemplate.indexOps("portal_employees").ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
//        mongoTemplate.indexOps("portal_employees").ensureIndex(new Index().on("occupation", Sort.Direction.ASC));
//
//        return mongoTemplate;
//    }
//}

@EnableTransactionManagement
@Configuration
public class MongoConfig {

    private final DynamicMongoDatabaseFactory dynamicMongoDatabaseFactory;

    @Autowired
    public MongoConfig(DynamicMongoDatabaseFactory dynamicMongoDatabaseFactory) {
        this.dynamicMongoDatabaseFactory = dynamicMongoDatabaseFactory;
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        // Create default database factory for bean initialization
        // This will be used during startup and can be overridden per-request
        try {
            return dynamicMongoDatabaseFactory.getMongoDatabaseFactory();
        } catch (IllegalStateException e) {
            // If database configuration is missing, throw a more helpful error
            throw new IllegalStateException(
                "MongoDB configuration is required. Please set DATABASE_URI and DATABASE environment variables. " +
                "Original error: " + e.getMessage(), e);
        }
    }

    @Bean
    public PlatformTransactionManager transactionManager(@NonNull MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Bean
    public MongoTemplate mongoTemplate(@NonNull MappingMongoConverter mappingMongoConverter) {
        MongoDatabaseFactory mongoDbFactory;
        try {
            mongoDbFactory = dynamicMongoDatabaseFactory.getMongoDatabaseFactory();
        } catch (IllegalStateException e) {
            throw new IllegalStateException(
                "MongoDB configuration is required. Please set DATABASE_URI and DATABASE environment variables. " +
                "Original error: " + e.getMessage(), e);
        }

        MongoTemplate mongoTemplate = new MongoTemplate(Objects.requireNonNull(mongoDbFactory), mappingMongoConverter);

        // Note: Indexes are automatically created by Spring Data MongoDB based on @Indexed annotations
        // in the model classes (e.g., EmployeeModel). Manual index creation is removed to avoid conflicts
        // with existing indexes that may have different names (e.g., "occupation_1" vs "occupation").
        // If you need to create indexes manually, use ensureIndex() with explicit names or check
        // if indexes exist first to avoid conflicts.

        return mongoTemplate;
    }
}
