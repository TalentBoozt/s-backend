package com.talentboozt.s_backend.domains.lifeplanner.journal.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.Instant;

@Data
@Document(collection = "lp_journal_entries")
@CompoundIndex(name = "user_date_idx", def = "{'userId': 1, 'date': 1}", unique = true)
public class JournalEntry {
    @Id
    private String id;
    @Indexed
    private String userId;
    private LocalDate date;
    private String reflection;
    private String aiInsight;
    private Instant createdAt;
}
