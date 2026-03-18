package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EUserPreferences;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserPreferencesRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Collections;

@Service
public class EduPersonalizationService {

    private final EUserPreferencesRepository preferencesRepository;
    private final ECoursesRepository coursesRepository;

    public EduPersonalizationService(EUserPreferencesRepository preferencesRepository, ECoursesRepository coursesRepository) {
        this.preferencesRepository = preferencesRepository;
        this.coursesRepository = coursesRepository;
    }

    public EUserPreferences updatePreferences(String userId, EUserPreferences update) {
        EUserPreferences pref = preferencesRepository.findByUserId(userId).orElseGet(() -> 
                EUserPreferences.builder().userId(userId).createdAt(Instant.now()).build());
                
        pref.setInterests(update.getInterests());
        pref.setPreferredLanguage(update.getPreferredLanguage());
        pref.setDailyLearningGoalMinutes(update.getDailyLearningGoalMinutes());
        pref.setIsNotificationsEnabled(update.getIsNotificationsEnabled());
        pref.setPreferredDifficulty(update.getPreferredDifficulty());
        pref.setUpdatedAt(Instant.now());
        
        return preferencesRepository.save(pref);
    }

    public EUserPreferences getPreferences(String userId) {
        return preferencesRepository.findByUserId(userId).orElseGet(() -> 
                EUserPreferences.builder().userId(userId).interests(Collections.emptyList()).build());
    }

    public List<ECourses> getRecommendations(String userId) {
        EUserPreferences pref = getPreferences(userId);
        
        // Simplified Logic: Fetch all published courses, filter by matching tags, and sort by trust score realistically
        // Note: For heavy production, this is heavily optimized in MongoDB Aggregation Pipelines natively!
        List<ECourses> allCourses = coursesRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getPublished()))
                .filter(c -> !Boolean.TRUE.equals(c.getIsPrivate()))
                .collect(Collectors.toList());

        if (pref.getInterests() == null || pref.getInterests().isEmpty()) {
            return allCourses.stream()
                             .sorted((c1, c2) -> Double.compare(c2.getRating() != null ? c2.getRating() : 0, 
                                                                c1.getRating() != null ? c1.getRating() : 0))
                             .limit(10)
                             .collect(Collectors.toList());
        }

        // Tag matching scoring model
        return allCourses.stream()
                .filter(c -> c.getTags() != null && Arrays.stream(c.getTags()).anyMatch(pref.getInterests()::contains))
                .sorted((c1, c2) -> Double.compare(c2.getRating() != null ? c2.getRating() : 0, 
                                                   c1.getRating() != null ? c1.getRating() : 0))
                .limit(10)
                .collect(Collectors.toList());
    }
}
