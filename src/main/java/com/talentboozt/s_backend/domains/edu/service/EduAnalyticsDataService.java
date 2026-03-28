package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.analytics.CreatorAnalyticsDTO;
import com.talentboozt.s_backend.domains.edu.dto.analytics.LearnerAnalyticsDTO;
import com.talentboozt.s_backend.domains.edu.dto.finance.RevenueSummaryDTO;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAnalyticsEventsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;
import org.springframework.stereotype.Service;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
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
    private final ETransactionsRepository transactionsRepository;

    public EduAnalyticsDataService(EEnrollmentsRepository enrollmentsRepository,
            ECoursesRepository coursesRepository,
            EduFinanceService financeService,
            EAnalyticsEventsRepository eventsRepository,
            ETransactionsRepository transactionsRepository) {
        this.enrollmentsRepository = enrollmentsRepository;
        this.coursesRepository = coursesRepository;
        this.financeService = financeService;
        this.eventsRepository = eventsRepository;
        this.transactionsRepository = transactionsRepository;
    }

    public CreatorAnalyticsDTO getCreatorAnalytics(String creatorId) {
        List<ECourses> courses = coursesRepository.findByCreatorId(creatorId);
        List<String> courseIds = courses.stream().map(ECourses::getId).collect(Collectors.toList());

        // Summarize Enrollments directly scaling total stats natively
        int totalEnrolls = courses.stream().mapToInt(c -> c.getTotalEnrollments() != null ? c.getTotalEnrollments() : 0)
                .sum();

        // Financial Math
        RevenueSummaryDTO revenueSummary = financeService.getRevenueSummary(creatorId);

        Map<String, Integer> courseViewsMap = new HashMap<>();
        for (String cId : courseIds) {
            long views = eventsRepository.findByCourseId(cId).stream()
                    .filter(e -> e.getType() == EAnalyticsEvent.VIEW).count();
            courseViewsMap.put(cId, (int) views);
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Instant monthStart = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay(ZoneOffset.UTC)
                .toInstant();
        Instant monthEnd = now.with(TemporalAdjusters.firstDayOfNextMonth()).toLocalDate().atStartOfDay(ZoneOffset.UTC)
                .toInstant();
        Instant prevMonthStart = now.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        int currentMonthEnrollments = 0;
        int previousMonthEnrollments = 0;
        double progressSum = 0.0;
        int progressCount = 0;
        for (String cId : courseIds) {
            for (EEnrollments e : enrollmentsRepository.findByCourseId(cId)) {
                if (e.getEnrolledAt() != null) {
                    if (!e.getEnrolledAt().isBefore(monthStart) && e.getEnrolledAt().isBefore(monthEnd)) {
                        currentMonthEnrollments++;
                    } else if (!e.getEnrolledAt().isBefore(prevMonthStart) && e.getEnrolledAt().isBefore(monthStart)) {
                        previousMonthEnrollments++;
                    }
                }
                if (e.getProgress() != null) {
                    progressSum += e.getProgress();
                    progressCount++;
                }
            }
        }
        double averageCompletionRate = progressCount > 0 ? Math.round((progressSum / progressCount) * 10.0) / 10.0
                : 0.0;

        Map<String, Double> timeline = getRevenueTimeline(creatorId);
        String currentMonthKey = String.format("%04d-%02d", now.getYear(), now.getMonthValue());
        ZonedDateTime prevMatch = now.minusMonths(1);
        String previousMonthKey = String.format("%04d-%02d", prevMatch.getYear(), prevMatch.getMonthValue());

        double currentMonthRevenue = timeline.getOrDefault(currentMonthKey, 0.0);
        double previousMonthRevenue = timeline.getOrDefault(previousMonthKey, 0.0);

        double revenueTrend = previousMonthRevenue == 0 ? 0.0 : ((currentMonthRevenue - previousMonthRevenue) / previousMonthRevenue) * 100;
        double enrollmentTrend = previousMonthEnrollments == 0 ? 0.0 : ((currentMonthEnrollments - (double)previousMonthEnrollments) / previousMonthEnrollments) * 100;

        return CreatorAnalyticsDTO.builder()
                .creatorId(creatorId)
                .totalRevenue(revenueSummary.getTotalEarnings())
                .totalEnrollments(totalEnrolls)
                .currentMonthEnrollments(currentMonthEnrollments)
                .averageCompletionRate(averageCompletionRate)
                .courseViews(courseViewsMap)
                .monthlyRevenueTimeline(timeline)
                .revenueTrendPercent(Math.round(revenueTrend * 10.0) / 10.0)
                .enrollmentTrendPercent(Math.round(enrollmentTrend * 10.0) / 10.0)
                .build();
    }

    public Map<String, Double> getRevenueTimeline(String creatorId) {
        List<ETransactions> transactions = transactionsRepository.findBySellerId(creatorId);
        
        return transactions.stream()
                .filter(t -> t.getCreatedAt() != null && t.getCreatorEarning() != null)
                .collect(Collectors.groupingBy(
                        t -> {
                            ZonedDateTime dt = t.getCreatedAt().atZone(ZoneOffset.UTC);
                            return String.format("%04d-%02d", dt.getYear(), dt.getMonthValue());
                        },
                        java.util.TreeMap::new,
                        Collectors.summingDouble(ETransactions::getCreatorEarning)
                ));
    }

    public LearnerAnalyticsDTO getLearnerAnalytics(String learnerId) {
        List<EEnrollments> enrolls = enrollmentsRepository.findByUserId(learnerId);

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
