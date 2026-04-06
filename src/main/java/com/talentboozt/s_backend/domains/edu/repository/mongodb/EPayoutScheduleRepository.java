package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EPayoutSchedule;

@Repository
public interface EPayoutScheduleRepository extends MongoRepository<EPayoutSchedule, String> {
    Optional<EPayoutSchedule> findByCreatorId(String creatorId);
    
    /** Finds active schedules where nextScheduledAt is before now */
    List<EPayoutSchedule> findByActiveTrueAndNextScheduledAtBefore(Instant now);
}
