package com.talentboozt.s_backend.Service.AUDIT_LOGS;

import com.talentboozt.s_backend.Model.AUDIT_LOGS.ClientActAuditLog;
import com.talentboozt.s_backend.Repository.AUDIT_LOGS.ClientActAuditLogRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
public class ClientActAuditLogService {

    @Autowired
    private ClientActAuditLogRepository auditLogRepository;

    private final BlockingQueue<ClientActAuditLog> auditQueue = new LinkedBlockingQueue<>(10_000); // max cap to prevent overload

    private long flushInterval = 5; // default fallback
    private int batchSize = 50;

    private ScheduledExecutorService scheduler;

    @Value("${audit.flush-interval-s:5}")
    public void setFlushInterval(long interval) {
        this.flushInterval = interval;
    }

    @Value("${audit.batch-size:50}")
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Value("${audit.expire-after-days:30}")
    private long expireAfterDays;

    @PostConstruct
    public void init() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::flushLogs, flushInterval, flushInterval, TimeUnit.SECONDS);
    }

    public void log(String userId, String ipAddress, String sessionId, String action, String source, Map<String, Object> details) {
        Instant expiresAt = Instant.now().plus(expireAfterDays, ChronoUnit.DAYS);
        boolean added = auditQueue.offer(new ClientActAuditLog(userId, ipAddress, sessionId, action, source, details, expiresAt));
        if (!added) {
            System.err.println("Audit queue full: Dropped audit log for action=" + action);
        }
    }

    private void flushLogs() {
        try {
            List<ClientActAuditLog> batch = new ArrayList<>(batchSize);
            auditQueue.drainTo(batch, batchSize);
            if (!batch.isEmpty()) {
                auditLogRepository.saveAll(batch);
            }
        } catch (Exception e) {
            System.err.println("Audit flush failed: " + e.getMessage());
        }
    }

    @PreDestroy
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }
}
