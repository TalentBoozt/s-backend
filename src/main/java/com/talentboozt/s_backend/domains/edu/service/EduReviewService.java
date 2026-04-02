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
import java.util.List;

@Service
public class EduReviewService {

    private final EReviewsRepository reviewsRepository;
    private final ECoursesRepository coursesRepository;
    private final EEnrollmentsRepository enrollmentsRepository;

    public EduReviewService(EReviewsRepository reviewsRepository,
            ECoursesRepository coursesRepository,
            EEnrollmentsRepository enrollmentsRepository) {
        this.reviewsRepository = reviewsRepository;
        this.coursesRepository = coursesRepository;
        this.enrollmentsRepository = enrollmentsRepository;
    }

    public EReviews addReview(String courseId, String userId, ReviewRequest request) {
        // Enforce only enrolled learners can post reviews
        boolean isEnrolled = enrollmentsRepository.findAll().stream()
                .anyMatch(e -> userId.equals(e.getUserId()) && courseId.equals(e.getCourseId()));

        if (!isEnrolled) {
            throw new EduAccessDeniedException("Must be enrolled in the course to leave a review.");
        }

        // Ensure user hasn't already reviewed
        boolean alreadyReviewed = reviewsRepository.findByCourseId(courseId).stream()
                .anyMatch(r -> userId.equals(r.getUserId()));

        if (alreadyReviewed) {
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

        return saved;
    }

    public List<EReviews> getCourseReviews(String courseId) {
        return reviewsRepository.findByCourseId(courseId);
    }

    public EReviews updateReview(String reviewId, ReviewRequest request) {
        EReviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new EduResourceNotFoundException("Review not found with id: " + reviewId));

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

    public void deleteReview(String reviewId) {
        EReviews review = reviewsRepository.findById(reviewId).orElse(null);
        if (review != null) {
            reviewsRepository.deleteById(reviewId);
            updateCourseAverageRating(review.getCourseId());
        }
    }

    public EReviews incrementHelpful(String reviewId) {
        EReviews review = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new EduResourceNotFoundException("Review not found with id: " + reviewId));
        int votes = review.getHelpfulVotes() != null ? review.getHelpfulVotes() : 0;
        review.setHelpfulVotes(votes + 1);
        review.setUpdatedAt(Instant.now());
        return reviewsRepository.save(review);
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
        }
    }
}
