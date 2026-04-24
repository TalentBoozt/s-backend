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
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository profilesRepository;
    private final com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository userRepository;

    public EduMarketplaceService(ECoursesRepository courseRepository, 
            EduTrustScoreService trustScoreService,
            com.talentboozt.s_backend.domains.edu.repository.mongodb.EProfilesRepository profilesRepository,
            com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.trustScoreService = trustScoreService;
        this.profilesRepository = profilesRepository;
        this.userRepository = userRepository;
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

            // Populate instructor name
            profilesRepository.findByUserId(c.getCreatorId()).ifPresent(p -> {
                String name = "";
                if (p.getFirstName() != null) name += p.getFirstName();
                if (p.getLastName() != null) name += (name.isEmpty() ? "" : " ") + p.getLastName();
                c.setInstructorName(name.isEmpty() ? "Talnova Creator" : name);
            });
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
                .sorted((c1, c2) -> Double.compare(calculateMarketplaceScore(c2), calculateMarketplaceScore(c1))) // Descending
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

    private double calculateMarketplaceScore(ECourses c) {
        double score = 0.0;

        // 1. Validation Bonus (Primary Weight)
        if (c.getValidationStatus() == ECourseValidationStatus.VALIDATED) score += 1000;
        else if (c.getValidationStatus() == ECourseValidationStatus.AI_APPROVED) score += 400;

        // 2. Performance Factor (Rating * Enrollments Scale)
        double rating = c.getRating() != null ? c.getRating() : 0.0;
        int students = c.getTotalEnrollments() != null ? c.getTotalEnrollments() : 0;
        score += (rating * 100); 
        score += Math.min(500, students * 2); // Cap student count impact at 500 points

        // 3. Featured Boost
        if (Boolean.TRUE.equals(c.getIsFeatured())) score += 3000; // Strong manual boost

        // 4. Integrity Factors (Negative weight for plagiarism)
        double plagiarism = c.getPlagiarismScore() != null ? c.getPlagiarismScore() : 0.0;
        double quality = c.getOverallQualityScore() != null ? c.getOverallQualityScore() : 70.0;
        
        score -= (plagiarism * 50); // Severe penalty for plagiarism
        score += (quality * 5);    // Reward for high quality overall score

        // 5. Recency (Recency boost for new nodes)
        if (c.getCreatedAt() != null) {
            long daysOld = java.time.Duration.between(c.getCreatedAt(), java.time.Instant.now()).toDays();
            if (daysOld < 30) score += 200;
        }

        return score;
    }

    public List<ECourses> getCoursesByCreator(String creatorId) {
        return courseRepository.findByCreatorId(creatorId).stream()
                .filter(this::isPublicCatalogCourse)
                .map(this::enrichTrustData)
                .collect(Collectors.toList());
    }

    public List<com.talentboozt.s_backend.domains.edu.model.EProfiles> getTopInstructors() {
        // Fetch all profiles and filter those who are instructors. 
        // In a real app, you might want to join with EUser to check roles or have an isInstructor flag.
        // For now, we'll fetch profiles that have courses published.
        java.util.Set<String> creatorIds = courseRepository.findAll().stream()
                .filter(this::isPublicCatalogCourse)
                .map(ECourses::getCreatorId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());

        return profilesRepository.findAll().stream()
                .filter(p -> creatorIds.contains(p.getUserId()))
                .limit(20) // Limit to top 20 for marketplace
                .collect(Collectors.toList());
    }

    private int getValidationWeight(ECourses c) {
        if (c.getValidationStatus() == null) return 0;
        if (c.getValidationStatus() == ECourseValidationStatus.VALIDATED) return 100;
        if (c.getValidationStatus() == ECourseValidationStatus.AI_APPROVED) return 50;
        return 0;
    }
}
