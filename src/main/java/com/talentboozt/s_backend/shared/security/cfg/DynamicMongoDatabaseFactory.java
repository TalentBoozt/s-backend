package com.talentboozt.s_backend.shared.security.cfg;

import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DynamicMongoDatabaseFactory {

    private static final Map<String, MongoClient> mongoClients = new ConcurrentHashMap<>();

    private final HttpServletRequest request;
    private final ConfigUtility configUtility;

    @Autowired
    public DynamicMongoDatabaseFactory(HttpServletRequest request, ConfigUtility configUtility) {
        this.request = request;
        this.configUtility = configUtility;
    }

    public MongoDatabaseFactory getMongoDatabaseFactory() {
        String dbUri = configUtility.getProperty("DATABASE_URI");
        String dbName = configUtility.getProperty("DATABASE");

//        if (request != null) {
//            System.out.println(request);
//            String demoModeHeader = request.getHeader("X-Demo-Mode");
//            System.out.println("X-Demo-Mode header: " + demoModeHeader); // Debugging

//            if ("true".equalsIgnoreCase(demoModeHeader)) {
//                dbUri = configUtility.getProperty("MONGODB_DEMO_URI");
//                dbName = configUtility.getProperty("MONGODB_DEMO_NAME");
//            }
//        }

//        System.out.println("Using MongoDB URI: " + dbUri); // Debugging
//        System.out.println("Using MongoDB Name: " + dbName); // Debugging

        MongoClient mongoClient = mongoClients.computeIfAbsent(dbUri, MongoClients::create);
        return new SimpleMongoClientDatabaseFactory(mongoClient, dbName);
    }
}