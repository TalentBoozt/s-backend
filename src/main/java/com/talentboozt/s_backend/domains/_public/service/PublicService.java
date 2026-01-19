package com.talentboozt.s_backend.domains._public.service;

import com.talentboozt.s_backend.domains.com_courses.dto.CourseMissedNotify;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.com_courses.repository.CourseRepository;
import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class PublicService {

    private final CourseRepository courseRepository;

    public PublicService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public ResponseEntity<ApiResponse> addNewBatchNotification(String courseId, CourseMissedNotify courseMissedNotify) {
        Optional<CourseModel> course = courseRepository.findById(Objects.requireNonNull(courseId));
        if (course.isPresent()) {
            CourseModel courseModel = course.get();
            if (courseModel.getNotifiers() == null) {
                courseModel.setNotifiers(new ArrayList<>());
            }
            for (CourseMissedNotify n : courseModel.getNotifiers()) {
                if (n.getEmail().equals(courseMissedNotify.getEmail())) {
                    return ResponseEntity.badRequest().body(new ApiResponse("Notification already exists"));
                }
            }
            courseModel.getNotifiers().add(courseMissedNotify);
            courseRepository.save(courseModel);
            return ResponseEntity.ok().body(new ApiResponse("Notification added successfully."));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("Course not found"));
        }
    }
}
