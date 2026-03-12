package com.talentboozt.s_backend.domains.lifeplanner.journal.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.WeeklyMoodSummary;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface WeeklyMoodSummaryRepository extends MongoRepository<WeeklyMoodSummary, String> {
    Optional<WeeklyMoodSummary> findByUserIdAndWeekStartDate(String userId, LocalDate weekStartDate);
    List<WeeklyMoodSummary> findByUserIdOrderByWeekStartDateDesc(String userId);
}
