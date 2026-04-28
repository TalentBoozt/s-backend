package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent;
import com.talentboozt.s_backend.domains.edu.enums.EPaymentStatus;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.model.EReviews;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EduAnalyticsAggregationService {

    private final EAnalyticsEventsRepository eventsRepository;
    private final ETransactionsRepository transactionsRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final ECoursesRepository coursesRepository;
    private final EReviewsRepository reviewsRepository;

    /**
     * Course specific performance metrics.
     */
    public Map<String, Object> getCoursePerformance(String courseId) {
        ECourses course = coursesRepository.findById(courseId).orElse(null);
        if (course == null) return Collections.emptyMap();

        long views = eventsRepository.findByCourseId(courseId).stream()
                .filter(e -> e.getType() == EAnalyticsEvent.VIEW).count();
        
        List<ETransactions> txs = transactionsRepository.findByCourseId(courseId).stream()
                .filter(t -> t.getPaymentStatus() == EPaymentStatus.SUCCESS).collect(Collectors.toList());
        int purchases = txs.size();

        List<EEnrollments> enrolls = enrollmentsRepository.findByCourseId(courseId);
        double avgCompletion = enrolls.isEmpty() ? 0.0 : 
                enrolls.stream().mapToDouble(e -> e.getProgress() != null ? e.getProgress() : 0.0).average().orElse(0.0);

        List<EReviews> reviews = reviewsRepository.findByCourseId(courseId);
        double avgRating = reviews.isEmpty() ? 0.0 :
                reviews.stream().mapToDouble(EReviews::getRating).average().orElse(0.0);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("courseId", courseId);
        metrics.put("title", course.getTitle());
        metrics.put("views", views);
        metrics.put("purchases", purchases);
        metrics.put("completionRate", Math.round(avgCompletion * 100.0) / 100.0);
        metrics.put("averageRating", Math.round(avgRating * 100.0) / 100.0);
        metrics.put("conversionRate", views == 0 ? 0.0 : Math.round(((double) purchases / views) * 10000.0) / 100.0);

        return metrics;
    }

    /**
     * Global creator metrics across all their courses.
     */
    public Map<String, Object> getCreatorMetrics(String creatorId) {
        List<ECourses> myCourses = coursesRepository.findByCreatorId(creatorId);
        List<String> courseIds = myCourses.stream().map(ECourses::getId).collect(Collectors.toList());

        List<ETransactions> myTxs = transactionsRepository.findBySellerId(creatorId).stream()
                .filter(t -> t.getPaymentStatus() == EPaymentStatus.SUCCESS).collect(Collectors.toList());
        
        double totalRevenue = myTxs.stream()
                .mapToDouble(t -> t.getCreatorEarning() != null ? t.getCreatorEarning() : 0.0).sum();

        long totalViews = 0;
        for (String cId : courseIds) {
            totalViews += eventsRepository.findByCourseId(cId).stream()
                    .filter(e -> e.getType() == EAnalyticsEvent.VIEW).count();
        }

        double avgRating = myCourses.stream()
                .filter(c -> c.getRating() != null).mapToDouble(ECourses::getRating).average().orElse(0.0);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("creatorId", creatorId);
        metrics.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);
        metrics.put("totalCourses", myCourses.size());
        metrics.put("totalPurchases", myTxs.size());
        metrics.put("conversionRate", totalViews == 0 ? 0.0 : Math.round(((double) myTxs.size() / totalViews) * 10000.0) / 100.0);
        metrics.put("averageRating", Math.round(avgRating * 100.0) / 100.0);

        return metrics;
    }

    /**
     * Platform-wide metrics for admins.
     */
    public Map<String, Object> getPlatformMetrics() {
        List<ETransactions> allTxs = transactionsRepository.findAll().stream()
                .filter(t -> t.getPaymentStatus() == EPaymentStatus.SUCCESS).collect(Collectors.toList());
        
        double totalPlatformRevenue = allTxs.stream()
                .mapToDouble(t -> t.getAmount() != null ? t.getAmount() : 0.0).sum();
        
        double platformCut = allTxs.stream()
                .mapToDouble(t -> t.getPlatformFee() != null ? t.getPlatformFee() : 0.0).sum();

        long activeUsers = enrollmentsRepository.findAll().stream()
                .map(EEnrollments::getUserId).distinct().count();

        List<ECourses> topCourses = coursesRepository.findAll().stream()
                .sorted(Comparator.comparing(ECourses::getTotalEnrollments, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .collect(Collectors.toList());

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalGrossRevenue", Math.round(totalPlatformRevenue * 100.0) / 100.0);
        metrics.put("platformEarnings", Math.round(platformCut * 100.0) / 100.0);
        metrics.put("activeLearners", activeUsers);
        metrics.put("topCourses", topCourses.stream().map(c -> Map.of("id", c.getId(), "title", c.getTitle(), "enrollments", c.getTotalEnrollments())).collect(Collectors.toList()));

        return metrics;
    }
}
