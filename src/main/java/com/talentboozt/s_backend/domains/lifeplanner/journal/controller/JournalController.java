package com.talentboozt.s_backend.domains.lifeplanner.journal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.JournalEntry;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.WeeklyMoodSummary;
import com.talentboozt.s_backend.domains.lifeplanner.journal.model.MoodEntry;
import com.talentboozt.s_backend.domains.lifeplanner.journal.service.JournalService;
import com.talentboozt.s_backend.domains.lifeplanner.journal.service.MoodTrackingService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lifeplanner/journal")
@RequiredArgsConstructor
public class JournalController {

    private final JournalService journalService;
    private final MoodTrackingService moodTrackingService;

    @PostMapping("/mood")
    public ResponseEntity<MoodEntry> logMood(@RequestBody Map<String, Object> payload, @RequestHeader("x-user-id") String userId) {
        int score = (Integer) payload.get("score");
        String label = (String) payload.get("label");
        MoodEntry entry = moodTrackingService.logMood(userId, score, label);
        return ResponseEntity.ok(entry);
    }

    @GetMapping("/mood/weekly")
    public ResponseEntity<List<WeeklyMoodSummary>> getWeeklyMoodSummaries(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(moodTrackingService.getWeeklySummaries(userId));
    }

    @GetMapping("/mood/trends")
    public ResponseEntity<List<MoodEntry>> getMoodTrends(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(moodTrackingService.getMoodTrends(userId));
    }

    @PostMapping
    public ResponseEntity<JournalEntry> logJournal(@RequestBody Map<String, String> payload, @RequestHeader("x-user-id") String userId) {
        String reflection = payload.get("reflection");
        JournalEntry entry = journalService.saveEntry(userId, reflection);
        return ResponseEntity.ok(entry);
    }

    @GetMapping
    public ResponseEntity<List<JournalEntry>> getJournals(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(journalService.getAllEntries(userId));
    }

    @GetMapping("/prompt")
    public ResponseEntity<Map<String, String>> getPrompt(@RequestHeader("x-user-id") String userId) {
        return ResponseEntity.ok(Map.of("prompt", journalService.getPromptForToday(userId)));
    }

    @PutMapping("/{entryId}")
    public ResponseEntity<JournalEntry> updateJournal(@PathVariable String entryId, @RequestBody Map<String, String> payload, @RequestHeader("x-user-id") String userId) {
        String reflection = payload.get("reflection");
        return ResponseEntity.ok(journalService.updatePastEntry(userId, entryId, reflection));
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteJournal(@PathVariable String entryId, @RequestHeader("x-user-id") String userId) {
        journalService.deleteEntry(userId, entryId);
        return ResponseEntity.noContent().build();
    }
}
