package com.talentboozt.s_backend.domains.edu.seo.analytics;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface CrawlVisitLogRepository extends MongoRepository<CrawlVisitLog, String> {
    List<CrawlVisitLog> findByBotName(String botName);
    List<CrawlVisitLog> findByTimestampAfter(Instant timestamp);
}
