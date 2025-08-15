package com.talentboozt.s_backend.domains._public.service;

import com.talentboozt.s_backend.domains.com_courses.dto.CourseMissedNotify;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.com_courses.repository.CourseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class PublicService {

    private final CourseRepository courseRepository;

    public PublicService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public ResponseEntity<?> addNewBatchNotification(String courseId, CourseMissedNotify courseMissedNotify) {
        Optional<CourseModel> course = courseRepository.findById(courseId);
        if (course.isPresent()) {
            CourseModel courseModel = course.get();
            if (courseModel.getNotifiers() == null) {
                courseModel.setNotifiers(new ArrayList<>());
            }
            courseModel.getNotifiers().add(courseMissedNotify);
            courseRepository.save(courseModel);
            return ResponseEntity.ok("Notification added successfully.");
        } else {
            return ResponseEntity.badRequest().body("Course not found.");
        }
    }
}
