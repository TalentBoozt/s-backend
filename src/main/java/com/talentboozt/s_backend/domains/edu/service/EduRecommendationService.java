package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EEnrollments;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduRecommendationService {

    private final ECoursesRepository coursesRepository;
    private final EAnalyticsEventsRepository eventsRepository;
    private final ETransactionsRepository transactionsRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final EduPersonalizationService personalizationService;

    /**
     * Trending = Score based on views and purchases in the last X days.
     */
    public List<ECourses> getTrendingCourses() {
        List<ECourses> allCourses = coursesRepository.findByPublishedTrueAndIsPrivateFalseAndStatus(
                com.talentboozt.s_backend.domains.edu.enums.ECourseStatus.PUBLISHED);

        // Strategy: (Purchases * 5) + (Views * 1)
        return allCourses.stream()
                .sorted((c1, c2) -> {
                    long v1 = eventsRepository.findByCourseId(c1.getId()).stream()
                            .filter(e -> e.getType() == EAnalyticsEvent.VIEW).count();
                    long p1 = transactionsRepository.findByCourseId(c1.getId()).size();
                    double s1 = (p1 * 5.0) + v1;

                    long v2 = eventsRepository.findByCourseId(c2.getId()).stream()
                            .filter(e -> e.getType() == EAnalyticsEvent.VIEW).count();
                    long p2 = transactionsRepository.findByCourseId(c2.getId()).size();
                    double s2 = (p2 * 5.0) + v2;

                    return Double.compare(s2, s1);
                })
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Personalized = Recommendations based on previously purchased categories and
     * user favorites/interests.
     */
    public List<ECourses> getRecommendedCourses(String userId) {
        // 1. Get categories of courses the user already purchased
        List<EEnrollments> enrollments = enrollmentsRepository.findByUserId(userId);
        Set<String> interestedCategories = new HashSet<>();

        for (EEnrollments en : enrollments) {
            coursesRepository.findById(en.getCourseId()).ifPresent(c -> {
                if (c.getCategories() != null) {
                    interestedCategories.addAll(Arrays.asList(c.getCategories()));
                }
            });
        }

        // 2. Get interests from personalization profile
        interestedCategories.addAll(personalizationService.getPreferences(userId).getInterests());

        // 3. Find courses matching these categories/interests that unique from already
        // enrolled
        Set<String> enrolledCourseIds = enrollments.stream().map(EEnrollments::getCourseId).collect(Collectors.toSet());

        return coursesRepository
                .findByPublishedTrueAndIsPrivateFalseAndStatus(
                        com.talentboozt.s_backend.domains.edu.enums.ECourseStatus.PUBLISHED)
                .stream()
                .filter(c -> !enrolledCourseIds.contains(c.getId()))
                .filter(c -> (c.getCategories() != null && Arrays.stream(c.getCategories()).anyMatch(interestedCategories::contains)) ||
                        (c.getTags() != null && Arrays.stream(c.getTags()).anyMatch(interestedCategories::contains)))
                .sorted(Comparator.comparing(ECourses::getRating, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Similar = Courses in the same category or with matching tags.
     */
    public List<ECourses> getSimilarCourses(String courseId) {
        ECourses target = coursesRepository.findById(courseId).orElse(null);
        if (target == null)
            return Collections.emptyList();

        List<String> targetTags = target.getTags() != null ? Arrays.asList(target.getTags()) : Collections.emptyList();

        return coursesRepository
                .findByPublishedTrueAndIsPrivateFalseAndStatus(
                        com.talentboozt.s_backend.domains.edu.enums.ECourseStatus.PUBLISHED)
                .stream()
                .filter(c -> !c.getId().equals(courseId))
                .filter(c -> (target.getCategories() != null && c.getCategories() != null &&
                        Arrays.stream(target.getCategories()).anyMatch(cat -> Arrays.asList(c.getCategories()).contains(cat))) ||
                        (c.getTags() != null && Arrays.stream(c.getTags()).anyMatch(targetTags::contains)))
                .sorted(Comparator.comparing(ECourses::getRating, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .collect(Collectors.toList());
    }
}
