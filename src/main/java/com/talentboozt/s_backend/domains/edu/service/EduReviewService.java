package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.engagement.ReviewRequest;
import com.talentboozt.s_backend.domains.edu.exception.EduAccessDeniedException;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.model.EReviews;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EReviewsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EduReviewService {

    private final EReviewsRepository reviewsRepository;
    private final ECoursesRepository coursesRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final EduAnalyticsEventService analyticsEventService;
    private final EduTrustScoreService trustScoreService;

    public EduReviewService(EReviewsRepository reviewsRepository,
            ECoursesRepository coursesRepository,
            EEnrollmentsRepository enrollmentsRepository,
            EduAnalyticsEventService analyticsEventService,
            EduTrustScoreService trustScoreService) {
        this.reviewsRepository = reviewsRepository;
        this.coursesRepository = coursesRepository;
        this.enrollmentsRepository = enrollmentsRepository;
        this.analyticsEventService = analyticsEventService;
        this.trustScoreService = trustScoreService;
    }

    public EReviews addReview(String courseId, String userId, ReviewRequest request) {
        // Enforce only enrolled learners can post reviews
        boolean isEnrolled = enrollmentsRepository.findByUserIdAndCourseId(userId, courseId).isPresent();
        if (!isEnrolled) {
            throw new EduAccessDeniedException("Must be enrolled in the course to leave a review.");
        }

        // Ensure user hasn't already reviewed
        if (reviewsRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new EduBadRequestException("You have already reviewed this course.");
        }

        if (request.getRating() < 1.0 || request.getRating() > 5.0) {
            throw new EduBadRequestException("Rating must be between 1.0 and 5.0.");
        }

        EReviews review = EReviews.builder()
                .courseId(courseId)
                .userId(userId)
                .rating(request.getRating() != null ? request.getRating() : 0.0)
                .content(request.getContent() != null ? request.getContent() : "")
                .helpfulVotes(0)
                .createdAt(Instant.now())
                .build();

        EReviews saved = reviewsRepository.save(review);
        updateCourseAverageRating(courseId);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("rating", review.getRating());
        metadata.put("courseId", courseId);

        // Track analytics
        analyticsEventService.trackEvent(userId, com.talentboozt.s_backend.domains.edu.enums.EAnalyticsEvent.REVIEW,
                metadata);

        return saved;
    }

    public List<EReviews> getCourseReviews(String courseId) {
        return reviewsRepository.findByCourseId(courseId);
    }

    public EReviews updateReview(String reviewId, String userId, boolean isAdmin, ReviewRequest request) {
        EReviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new EduResourceNotFoundException("Review not found with id: " + reviewId));

        if (!isAdmin && !review.getUserId().equals(userId)) {
            throw new EduAccessDeniedException("You can only edit your own reviews.");
        }

        if (request.getRating() != null) {
            if (request.getRating() < 1.0 || request.getRating() > 5.0) {
                throw new EduBadRequestException("Rating must be between 1.0 and 5.0.");
            }
            review.setRating(request.getRating());
        }
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }

        review.setUpdatedAt(Instant.now());
        EReviews saved = reviewsRepository.save(review);
        updateCourseAverageRating(review.getCourseId());

        return saved;
    }

    public void deleteReview(String reviewId, String userId, boolean isAdmin) {
        EReviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new EduResourceNotFoundException("Review not found"));

        if (!isAdmin && !review.getUserId().equals(userId)) {
            throw new EduAccessDeniedException("You can only delete your own reviews.");
        }

        reviewsRepository.deleteById(reviewId);
        updateCourseAverageRating(review.getCourseId());
    }

    public EReviews incrementHelpful(String reviewId) {
        EReviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new EduResourceNotFoundException("Review not found with id: " + reviewId));
        int votes = review.getHelpfulVotes() != null ? review.getHelpfulVotes() : 0;
        review.setHelpfulVotes(votes + 1);
        review.setUpdatedAt(Instant.now());
        return reviewsRepository.save(review);
    }

    public void reportReview(String reviewId) {
        EReviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new EduResourceNotFoundException("Review not found"));
        review.setIsReported(true);
        reviewsRepository.save(review);
    }

    public void toggleVisibility(String reviewId, boolean isVisible) {
        EReviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new EduResourceNotFoundException("Review not found"));
        review.setIsVisible(isVisible);
        reviewsRepository.save(review);
        updateCourseAverageRating(review.getCourseId());
    }

    private void updateCourseAverageRating(String courseId) {
        List<EReviews> allReviews = reviewsRepository.findByCourseId(courseId);
        ECourses course = coursesRepository.findById(courseId).orElse(null);

        if (course != null) {
            if (allReviews.isEmpty()) {
                course.setRating(0.0);
                course.setTotalReviews(0);
            } else {
                double avg = allReviews.stream().mapToDouble(EReviews::getRating).average().orElse(0.0);
                course.setRating(avg);
                course.setTotalReviews(allReviews.size());
            }
            coursesRepository.save(course);
            
            // Trigger Trust Score update
            if (course.getCreatorId() != null) {
                trustScoreService.updateScore(course.getCreatorId());
            }
        }
    }
}
