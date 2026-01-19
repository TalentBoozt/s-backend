package com.talentboozt.s_backend.shared.security.cfg;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.talentboozt.s_backend.shared.tenant.TenantContext;
import com.talentboozt.s_backend.shared.utils.ConfigUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced MongoDB database factory with:
 * - Connection pooling optimization
 * - Tenant-aware database selection
 * - Connection reuse and monitoring
 */
@Component
public class DynamicMongoDatabaseFactory {

    private static final Map<String, MongoClient> mongoClients = new ConcurrentHashMap<>();
    private static final int MAX_POOL_SIZE = 100;
    private static final int MIN_POOL_SIZE = 10;
    private static final int MAX_IDLE_TIME_MS = 30000;
    private static final int CONNECTION_TIMEOUT_MS = 10000;
    private static final int SOCKET_TIMEOUT_MS = 30000;

    private final ConfigUtility configUtility;

    @Autowired
    public DynamicMongoDatabaseFactory(ConfigUtility configUtility) {
        this.configUtility = configUtility;
    }

    public MongoDatabaseFactory getMongoDatabaseFactory() {
        String dbUri = configUtility.getProperty("DATABASE_URI");
        String dbName = configUtility.getProperty("DATABASE");

        // Validate configuration - "World" is a placeholder that indicates missing config
        if (dbUri == null || dbUri.equals("World") || dbUri.isEmpty()) {
            throw new IllegalStateException(
                "DATABASE_URI environment variable is not set. Please configure MongoDB connection URI.");
        }
        if (dbName == null || dbName.equals("World") || dbName.isEmpty()) {
            throw new IllegalStateException(
                "DATABASE environment variable is not set. Please configure MongoDB database name.");
        }

        // Tenant-aware database selection
        TenantContext tenantContext = TenantContext.getCurrent();
        if (tenantContext != null && tenantContext.getDatabaseName() != null) {
            dbName = tenantContext.getDatabaseName();
        }

        // Check for demo mode header (if request context is available)
        String demoModeHeader = getDemoModeHeader();
        if ("true".equalsIgnoreCase(demoModeHeader)) {
            String demoUri = configUtility.getProperty("MONGODB_DEMO_URI");
            String demoName = configUtility.getProperty("MONGODB_DEMO_NAME");
            if (demoUri != null && !demoUri.equals("World") && !demoUri.isEmpty()) {
                dbUri = demoUri;
            }
            if (demoName != null && !demoName.equals("World") && !demoName.isEmpty()) {
                dbName = demoName;
            }
        }

        // Create or reuse MongoClient with optimized connection pooling
        MongoClient mongoClient = mongoClients.computeIfAbsent(dbUri, uri -> {
            try {
                ConnectionString connectionString = new ConnectionString(uri);
                
                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .applyToConnectionPoolSettings(builder -> {
                            builder.maxSize(MAX_POOL_SIZE)
                                   .minSize(MIN_POOL_SIZE)
                                   .maxConnectionIdleTime(MAX_IDLE_TIME_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
                        })
                        .applyToSocketSettings(builder -> {
                            builder.connectTimeout(CONNECTION_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS)
                                   .readTimeout(SOCKET_TIMEOUT_MS, java.util.concurrent.TimeUnit.MILLISECONDS);
                        })
                        .applyToServerSettings(builder -> {
                            builder.heartbeatFrequency(10000, java.util.concurrent.TimeUnit.MILLISECONDS);
                        })
                        .build();
                
                return MongoClients.create(settings);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to create MongoDB client for URI: " + uri, e);
            }
        });
        
        return new SimpleMongoClientDatabaseFactory(Objects.requireNonNull(mongoClient), Objects.requireNonNull(dbName));
    }
    
    private String getDemoModeHeader() {
        // Try to get from tenant context if available
        TenantContext context = TenantContext.getCurrent();
        // Note: For request-based access, consider using RequestContextHolder
        return null; // Default implementation
    }
    
    /**
     * Cleanup method to close MongoClient connections
     * Should be called during application shutdown
     */
    public void shutdown() {
        mongoClients.values().forEach(MongoClient::close);
        mongoClients.clear();
    }
}