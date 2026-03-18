package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.analytics.CreatorAnalyticsDTO;
import com.talentboozt.s_backend.domains.edu.dto.analytics.LearnerAnalyticsDTO;
import com.talentboozt.s_backend.domains.edu.dto.finance.RevenueSummaryDTO;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAnalyticsEventsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EduAnalyticsDataService {

    private final EEnrollmentsRepository enrollmentsRepository;
    private final ECoursesRepository coursesRepository;
    private final EduFinanceService financeService;
    private final EAnalyticsEventsRepository eventsRepository;

    public EduAnalyticsDataService(EEnrollmentsRepository enrollmentsRepository,
            ECoursesRepository coursesRepository,
            EduFinanceService financeService,
            EAnalyticsEventsRepository eventsRepository) {
        this.enrollmentsRepository = enrollmentsRepository;
        this.coursesRepository = coursesRepository;
        this.financeService = financeService;
        this.eventsRepository = eventsRepository;
    }

    public CreatorAnalyticsDTO getCreatorAnalytics(String creatorId) {
        List<ECourses> courses = coursesRepository.findByCreatorId(creatorId);
        List<String> courseIds = courses.stream().map(ECourses::getId).collect(Collectors.toList());

        // Summarize Enrollments directly scaling total stats natively
        int totalEnrolls = courses.stream().mapToInt(c -> c.getTotalEnrollments() != null ? c.getTotalEnrollments() : 0)
                .sum();

        // Financial Math
        RevenueSummaryDTO revenueSummary = financeService.getRevenueSummary(creatorId);

        // Simulating metric maps from Analytics Collection
        Map<String, Integer> courseViewsMap = new HashMap<>();
        for (String cId : courseIds) {
            long views = eventsRepository.findByCourseId(cId).stream()
                    .filter(e -> e.getType() == EAnalyticsEvent.VIEW).count();
            courseViewsMap.put(cId, (int) views);
        }

        return CreatorAnalyticsDTO.builder()
                .creatorId(creatorId)
                .totalRevenue(revenueSummary.getTotalEarnings())
                .totalEnrollments(totalEnrolls)
                .currentMonthEnrollments(0) // Simplified metric for MVP
                .averageCompletionRate(75.5) // Simplified metric
                .courseViews(courseViewsMap)
                .build();
    }

    public LearnerAnalyticsDTO getLearnerAnalytics(String learnerId) {
        List<EEnrollments> enrolls = enrollmentsRepository.findAll().stream()
                .filter(e -> learnerId.equals(e.getUserId()))
                .collect(Collectors.toList());

        int totalCourses = enrolls.size();
        int completedCourses = (int) enrolls.stream().filter(e -> Boolean.TRUE.equals(e.getCompleted())).count();

        int highestStreak = enrolls.stream().mapToInt(e -> e.getLongestStreak() != null ? e.getLongestStreak() : 0)
                .max().orElse(0);
        int currentStreak = enrolls.stream().mapToInt(e -> e.getCurrentStreak() != null ? e.getCurrentStreak() : 0)
                .max().orElse(0);

        return LearnerAnalyticsDTO.builder()
                .learnerId(learnerId)
                .totalCoursesEnrolled(totalCourses)
                .completedCourses(completedCourses)
                .currentStreak(currentStreak)
                .longestStreak(highestStreak)
                .totalCertificates(completedCourses) // Maps natively assuming certification triggers properly
                .build();
    }
}
