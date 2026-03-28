package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EduMarketplaceService {

    private final ECoursesRepository courseRepository;

    public EduMarketplaceService(ECoursesRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<ECourses> getFeaturedCourses() {
        return courseRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getPublished())
                        && !Boolean.TRUE.equals(c.getIsPrivate())
                        && Boolean.TRUE.equals(c.getIsFeatured()))
                .collect(Collectors.toList());
    }

    public List<ECourses> searchCourses(String keyword, String category, String level, Double priceMax) {
        return courseRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getPublished()) && !Boolean.TRUE.equals(c.getIsPrivate()))
                .filter(c -> {
                    boolean matchesKeyword = keyword == null || keyword.isEmpty()
                            || (c.getTitle() != null && c.getTitle().toLowerCase().contains(keyword.toLowerCase()));
                    boolean matchesCategory = category == null || category.isEmpty()
                            || (c.getCategories() != null && java.util.Arrays.asList(c.getCategories()).contains(category));
                    boolean matchesLevel = level == null || level.isEmpty()
                            || (c.getLevel() != null && c.getLevel().name().equalsIgnoreCase(level));
                    boolean matchesPrice = priceMax == null 
                            || (c.getPrice() != null && c.getPrice() <= priceMax);
                    return matchesKeyword && matchesCategory && matchesLevel && matchesPrice;
                })
                .collect(Collectors.toList());
    }

    public ECourses getCourseDetails(String courseId) {
        ECourses course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!Boolean.TRUE.equals(course.getPublished()) || Boolean.TRUE.equals(course.getIsPrivate())) {
            throw new RuntimeException("Course is not available in the marketplace");
        }
        return course;
    }
}
