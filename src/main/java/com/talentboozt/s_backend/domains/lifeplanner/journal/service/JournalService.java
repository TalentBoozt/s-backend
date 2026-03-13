package com.talentboozt.s_backend.domains.lifeplanner.journal.service;

import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.JournalEntry;
import com.talentboozt.s_backend.domains.lifeplanner.journal.repository.mongodb.JournalEntryRepository;
import com.talentboozt.s_backend.domains.lifeplanner.ai.JournalGenerator;
import com.talentboozt.s_backend.domains.lifeplanner.user.service.UserService;
import com.talentboozt.s_backend.domains.lifeplanner.user.model.UserProfile;
import java.time.LocalDate;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final JournalGenerator journalGenerator;
    private final UserService userService;

    public JournalEntry saveEntry(String userId, String reflection) {
        LocalDate today = LocalDate.now();
        JournalEntry entry = journalEntryRepository.findByUserIdAndDate(userId, today)
                .orElse(new JournalEntry());

        entry.setUserId(userId);
        entry.setDate(today);
        entry.setReflection(reflection);
        if (entry.getId() == null || entry.getAiInsight() == null || entry.getAiInsight().startsWith("AI insights")) {
            entry.setCreatedAt(Instant.now());
            try {
                String insight = journalGenerator.generateInsight(userId, reflection);
                entry.setAiInsight(insight);
            } catch (Exception e) {
                entry.setAiInsight("Great reflection. Keep focusing on your goals and maintaining consistency.");
            }
        }

        return journalEntryRepository.save(entry);
    }

    public String getPromptForToday(String userId) {
        return userService.getProfileByUserId(userId)
                .map(journalGenerator::generateDailyPrompt)
                .orElse("How was your day?");
    }

    public List<JournalEntry> getAllEntries(String userId) {
        return journalEntryRepository.findByUserIdOrderByDateDesc(userId);
    }
}
