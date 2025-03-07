package com.talentboozt.s_backend.Config;

import com.talentboozt.s_backend.Shared.DynamicMongoDatabaseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.index.Index;


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

@Configuration
public class MongoConfig {

    @Autowired
    private DynamicMongoDatabaseFactory dynamicMongoDatabaseFactory;

    @Bean
    public MongoTemplate mongoTemplate(MappingMongoConverter mappingMongoConverter) {
        MongoDatabaseFactory mongoDbFactory = dynamicMongoDatabaseFactory.getMongoDatabaseFactory();

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, mappingMongoConverter);

        mongoTemplate.indexOps("portal_employees").ensureIndex(new Index().on("email", Sort.Direction.ASC).unique());
        mongoTemplate.indexOps("portal_employees").ensureIndex(new Index().on("occupation", Sort.Direction.ASC));

        return mongoTemplate;
    }
}
