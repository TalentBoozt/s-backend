package com.talentboozt.s_backend.domains.lifeplanner.journal.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.MoodEntry;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MoodEntryRepository extends MongoRepository<MoodEntry, String> {
    Optional<MoodEntry> findByUserIdAndDate(String userId, LocalDate date);
    List<MoodEntry> findByUserIdAndDateBetweenOrderByDateAsc(String userId, LocalDate start, LocalDate end);
}
