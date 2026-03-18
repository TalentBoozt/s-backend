package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EduTrustService {

    private final ECoursesRepository coursesRepository;

    public EduTrustService(ECoursesRepository coursesRepository) {
        this.coursesRepository = coursesRepository;
    }

    // A Cron task simulating nightly ranking resets based on raw analytics
    // e.g., runs at 2:00 AM every day
    @Scheduled(cron = "0 0 2 * * ?")
    public void calculateFeaturedCourses() {
        List<ECourses> courses = coursesRepository.findAll();

        for (ECourses course : courses) {
            double rating = course.getRating() != null ? course.getRating() : 0.0;
            int totalEnrolls = course.getTotalEnrollments() != null ? course.getTotalEnrollments() : 0;
            int totalReviews = course.getTotalReviews() != null ? course.getTotalReviews() : 0;

            // Simple trust algorithm: needs high engagement and ratings above 4.5
            boolean isHighlyRated = rating >= 4.5;
            boolean hasHighEngagement = totalEnrolls > 50 && totalReviews > 10;
            boolean verifiedCreator = Boolean.TRUE.equals(course.getTalnovaVerified());

            if (isHighlyRated && hasHighEngagement && verifiedCreator) {
                course.setIsFeatured(true);
            } else {
                course.setIsFeatured(false);
            }
            
            // Search rank formula (basic)
            int score = (int) (rating * 10) + (totalEnrolls * 2) + (totalReviews * 5);
            course.setSearchRank(score);

            coursesRepository.save(course);
        }
    }
}
