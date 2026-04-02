package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ECourseStatus;
import com.talentboozt.s_backend.domains.edu.enums.ECourseValidationStatus;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EduMarketplaceService {

    private final ECoursesRepository courseRepository;
    private final EduTrustScoreService trustScoreService;

    public EduMarketplaceService(ECoursesRepository courseRepository, EduTrustScoreService trustScoreService) {
        this.courseRepository = courseRepository;
        this.trustScoreService = trustScoreService;
    }

    /** Legacy documents may have {@code published == true} with {@code status} unset. */
    private boolean isPublicCatalogCourse(ECourses c) {
        ECourseStatus st = c.getStatus();
        boolean approved = st == ECourseStatus.PUBLISHED
                || (st == null && Boolean.TRUE.equals(c.getPublished()));
        
        boolean notRejected = c.getValidationStatus() != ECourseValidationStatus.AI_REJECTED;

        return Boolean.TRUE.equals(c.getPublished())
                && !Boolean.TRUE.equals(c.getIsPrivate())
                && approved
                && notRejected;
    }

    private ECourses enrichTrustData(ECourses c) {
        if (c.getValidationStatus() == ECourseValidationStatus.VALIDATED) {
            c.setTrustDisclaimer("Verified by Talnova Experts.");
        } else if (c.getValidationStatus() == ECourseValidationStatus.AI_APPROVED) {
            c.setTrustDisclaimer("AI Reviewed. This content has not been manually verified by Talnova.");
        } else {
            c.setTrustDisclaimer("Unverified user generated content.");
        }

        if (c.getCreatorId() != null) {
            var trust = trustScoreService.getTrustScore(c.getCreatorId());
            c.setCreatorTier(trust.getCurrentTier());
            if ("BRONZE".equals(trust.getCurrentTier())) {
                c.setTrustWarning("Marketplace Warning: This creator has a low trust score. Exercise caution.");
            }
        }
        return c;
    }

    public List<ECourses> getFeaturedCourses() {
        return courseRepository.findAll().stream()
                .filter(this::isPublicCatalogCourse)
                .filter(c -> Boolean.TRUE.equals(c.getIsFeatured()))
                .map(this::enrichTrustData)
                .collect(Collectors.toList());
    }

    public org.springframework.data.domain.Page<ECourses> searchCourses(String keyword, String category, String level,
            Double priceMin, Double priceMax, org.springframework.data.domain.Pageable pageable) {
        List<ECourses> list = courseRepository.findAll().stream()
                .filter(this::isPublicCatalogCourse)
                .filter(c -> {
                    boolean matchesKeyword = keyword == null || keyword.isEmpty()
                            || (c.getTitle() != null && c.getTitle().toLowerCase().contains(keyword.toLowerCase()));
                    boolean matchesCategory = category == null || category.isEmpty()
                            || (c.getCategories() != null && java.util.Arrays.asList(c.getCategories()).contains(category));
                    boolean matchesLevel = level == null || level.isEmpty()
                            || (c.getLevel() != null && c.getLevel().name().equalsIgnoreCase(level));
                    double p = c.getPrice() != null ? c.getPrice() : 0.0;
                    boolean matchesPriceMin = priceMin == null || p >= priceMin;
                    boolean matchesPriceMax = priceMax == null || p <= priceMax;
                    return matchesKeyword && matchesCategory && matchesLevel && matchesPriceMin && matchesPriceMax;
                })
                .sorted((c1, c2) -> Integer.compare(getValidationWeight(c2), getValidationWeight(c1))) // Descending
                .map(this::enrichTrustData)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        List<ECourses> pageContent = start <= end ? list.subList(start, end) : java.util.Collections.emptyList();

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, list.size());
    }

    /** Distinct category labels from published public courses (for marketplace filters). */
    public List<String> getDistinctCategories() {
        return courseRepository.findAll().stream()
                .filter(this::isPublicCatalogCourse)
                .flatMap(c -> c.getCategories() == null ? java.util.stream.Stream.empty()
                        : java.util.Arrays.stream(c.getCategories()))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    public ECourses getCourseDetails(String courseId) {
        ECourses course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EduResourceNotFoundException("Course not found with id: " + courseId));

        if (!isPublicCatalogCourse(course)) {
            throw new EduBadRequestException("Course is not available in the marketplace");
        }
        return enrichTrustData(course);
    }

    private int getValidationWeight(ECourses c) {
        if (c.getValidationStatus() == null) return 0;
        if (c.getValidationStatus() == ECourseValidationStatus.VALIDATED) return 100;
        if (c.getValidationStatus() == ECourseValidationStatus.AI_APPROVED) return 50;
        return 0;
    }
}
