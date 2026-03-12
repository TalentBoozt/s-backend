package com.talentboozt.s_backend.domains.lifeplanner.journal.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.MoodEntry;
import com.talentboozt.s_backend.domains.lifeplanner.journal.repository.mongodb.MoodEntryRepository;

import java.time.LocalDate;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MoodTrackingService {

    private final MoodEntryRepository moodEntryRepository;

    public MoodEntry logMood(String userId, int score, String label) {
        LocalDate today = LocalDate.now();
        MoodEntry entry = moodEntryRepository.findByUserIdAndDate(userId, today)
                .orElse(new MoodEntry());
        
        entry.setUserId(userId);
        entry.setDate(today);
        entry.setScore(score);
        entry.setLabel(label);
        if (entry.getId() == null) {
            entry.setCreatedAt(Instant.now());
        }
        
        return moodEntryRepository.save(entry);
    }

    public Optional<MoodEntry> getTodayMood(String userId) {
        return moodEntryRepository.findByUserIdAndDate(userId, LocalDate.now());
    }
}
