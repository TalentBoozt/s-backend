package com.talentboozt.s_backend.domains.lifeplanner.journal.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.JournalEntry;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {
    Optional<JournalEntry> findByUserIdAndDate(String userId, LocalDate date);
    List<JournalEntry> findByUserIdOrderByDateDesc(String userId);
}
