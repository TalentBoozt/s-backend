package com.talentboozt.s_backend.domains._public.controller;

import com.talentboozt.s_backend.domains._public.service.PublicService;
import com.talentboozt.s_backend.domains.com_courses.dto.CourseMissedNotify;
import com.talentboozt.s_backend.domains.com_courses.model.RecordedCourseModel;
import com.talentboozt.s_backend.domains.com_courses.service.RecordedCourseService;
import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final PublicService publicService;
    private final RecordedCourseService recordedCourseService;

    public PublicController(PublicService publicService, RecordedCourseService recordedCourseService) {
        this.publicService = publicService;
        this.recordedCourseService = recordedCourseService;
    }

    @PostMapping("/add/batch-notification/{courseId}")
    public ResponseEntity<ApiResponse> addBatchNotification(@PathVariable String courseId, @RequestBody CourseMissedNotify courseMissedNotify) {
        return publicService.addNewBatchNotification(courseId, courseMissedNotify);
    }

    @PostMapping("/recorded-courses/add")
    public RecordedCourseModel addCourse(@RequestBody RecordedCourseModel course) {
        return recordedCourseService.createCourse(course);
    }

    @GetMapping("/recorded-courses/categories")
    public List<String> getCategories() {
        return recordedCourseService.getCategories();
    }
}
