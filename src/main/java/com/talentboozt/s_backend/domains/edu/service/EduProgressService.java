package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ENotificationType;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EduProgressService {

    private final EEnrollmentsRepository enrollmentsRepository;
    private final EduNotificationService notificationService;

    public EduProgressService(EEnrollmentsRepository enrollmentsRepository,
                              EduNotificationService notificationService) {
        this.enrollmentsRepository = enrollmentsRepository;
        this.notificationService = notificationService;
    }

    public EEnrollments resumeLearning(String userId, String courseId) {
        return enrollmentsRepository.findAll().stream()
                .filter(e -> userId.equals(e.getUserId()) && courseId.equals(e.getCourseId()))
                .findFirst()
                .orElseThrow(() -> new EduResourceNotFoundException("Enrollment not found for user: " + userId + " and course: " + courseId));
    }

    // Can be called periodically or manually when user signs in
    public void trackLearningStreak(String userId) {
        List<EEnrollments> enrollments = enrollmentsRepository.findAll().stream()
                .filter(e -> userId.equals(e.getUserId()))
                .collect(Collectors.toList());

        Instant now = Instant.now();
        Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);
        
        for (EEnrollments e : enrollments) {
            Instant lastStreakDate = e.getLastStreakDate() != null ? e.getLastStreakDate().truncatedTo(ChronoUnit.DAYS) : null;
            
            if (lastStreakDate == null) {
                e.setCurrentStreak(1);
                e.setLastStreakDate(now);
                e.setLongestStreak(1);
                
                // Trigger First Streak Milestone Push
                notificationService.triggerNotification(userId, "Streak Ignited! 🔥", "You've successfully hit your first learning benchmark.", ENotificationType.STREAK, e.getCourseId());
            } else {
                long daysBetween = ChronoUnit.DAYS.between(lastStreakDate, startOfDay);
                
                if (daysBetween == 1) {
                    // Consecutive day!
                    e.setCurrentStreak(e.getCurrentStreak() + 1);
                    e.setLastStreakDate(now);
                    
                    if (e.getCurrentStreak() > e.getLongestStreak()) {
                        e.setLongestStreak(e.getCurrentStreak());
                    }
                } else if (daysBetween > 1) {
                    // Streak broken :(
                    e.setCurrentStreak(1);
                    e.setLastStreakDate(now);
                }
            }
            
            enrollmentsRepository.save(e);
        }
    }
}
