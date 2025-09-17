package com.talentboozt.s_backend.domains.com_courses.service;

import com.talentboozt.s_backend.domains.com_courses.dto.RecLectureDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.RecordedCourseReviewDTO;
import com.talentboozt.s_backend.domains.com_courses.model.RecordedCourseModel;
import com.talentboozt.s_backend.domains.com_courses.repository.RecordedCourseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecordedCourseService {

    private final RecordedCourseRepository recordedCourseRepository;

    public RecordedCourseService(RecordedCourseRepository recordedCourseRepository) {
        this.recordedCourseRepository = recordedCourseRepository;
    }

    // CRUD Methods
    public RecordedCourseModel createCourse(RecordedCourseModel course) {
        course.setId(UUID.randomUUID().toString());
        course.setCreatedAt(LocalDateTime.now().toString());
        course.setUpdatedAt(LocalDateTime.now().toString());
        course.setCourseType("recorded");
        return recordedCourseRepository.save(course);
    }


    public RecordedCourseModel updateCourse(String courseId, RecordedCourseModel updatedCourse) {
        Optional<RecordedCourseModel> optionalCourse = recordedCourseRepository.findById(courseId);
        if (optionalCourse.isPresent()) {
            RecordedCourseModel existingCourse = optionalCourse.get();

            // Set updated fields
            existingCourse.setTitle(updatedCourse.getTitle());
            existingCourse.setSubtitle(updatedCourse.getSubtitle());
            existingCourse.setDescription(updatedCourse.getDescription());
            existingCourse.setModules(updatedCourse.getModules());
            existingCourse.setSkills(updatedCourse.getSkills());
            existingCourse.setRequirements(updatedCourse.getRequirements());
            existingCourse.setPrice(updatedCourse.getPrice());
            existingCourse.setCurrency(updatedCourse.getCurrency());
            existingCourse.setImage(updatedCourse.getImage());
            existingCourse.setLecturer(updatedCourse.getLecturer());
            existingCourse.setCategory(updatedCourse.getCategory());
            existingCourse.setLanguage(updatedCourse.getLanguage());
            existingCourse.setPublished(updatedCourse.isPublished());

            existingCourse.setUpdatedAt(LocalDateTime.now().toString());

            return recordedCourseRepository.save(existingCourse);
        }
        throw new RuntimeException("Course not found: " + courseId);
    }


    public boolean deleteCourse(String courseId) {
        if (recordedCourseRepository.existsById(courseId)) {
            recordedCourseRepository.deleteById(courseId);
            return true;
        }
        return false;
    }


    public List<RecordedCourseModel> getAllCourses() {
        return recordedCourseRepository.findAll();
    }

    public RecordedCourseModel getCourseById(String courseId) {
        return recordedCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
    }

    public List<RecordedCourseModel> getPublishedCourses() {
        return recordedCourseRepository.findByPublishedTrue();
    }


    // Rating / Review Handling
    public double calculateAverageRating(List<RecordedCourseReviewDTO> reviews) {
        if (reviews == null || reviews.isEmpty()) return 0.0;

        double sum = reviews.stream()
                .mapToDouble(RecordedCourseReviewDTO::getRating)
                .sum();
        return Math.round((sum / reviews.size()) * 10.0) / 10.0;
    }


    public RecordedCourseModel addReview(String courseId, RecordedCourseReviewDTO review) {
        RecordedCourseModel course = getCourseById(courseId);

        if (course.getReviews() == null) {
            course.setReviews(new ArrayList<>());
        }

        review.setReviewId(UUID.randomUUID().toString());
        review.setReviewDate(LocalDateTime.now().toString());

        course.getReviews().add(review);
        double updatedRating = calculateAverageRating(course.getReviews());

        course.setRating(updatedRating);
        course.setReviewCount(course.getReviews().size());

        return recordedCourseRepository.save(course);
    }

    // Utility
    public int getTotalLectureCount(RecordedCourseModel course) {
        if (course.getModules() == null) return 0;
        return course.getModules().stream()
                .mapToInt(m -> m.getLectures() != null ? m.getLectures().size() : 0)
                .sum();
    }

    public int getTotalDuration(RecordedCourseModel course) {
        if (course.getModules() == null) return 0;
        return course.getModules().stream()
                .flatMap(module -> module.getLectures().stream())
                .mapToInt(RecLectureDTO::getDuration)
                .sum();
    }
}

