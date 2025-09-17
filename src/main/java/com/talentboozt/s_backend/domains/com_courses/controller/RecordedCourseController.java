package com.talentboozt.s_backend.domains.com_courses.controller;

import com.talentboozt.s_backend.domains.com_courses.dto.RecordedCourseReviewDTO;
import com.talentboozt.s_backend.domains.com_courses.model.RecordedCourseModel;
import com.talentboozt.s_backend.domains.com_courses.service.RecordedCourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recorded-courses")
public class RecordedCourseController {
    private final RecordedCourseService recordedCourseService;

    public RecordedCourseController(RecordedCourseService recordedCourseService) {
        this.recordedCourseService = recordedCourseService;
    }

    @PostMapping("/add")
    public RecordedCourseModel addCourse(@RequestBody RecordedCourseModel course) {
        return recordedCourseService.createCourse(course);
    }

    @PutMapping("/update/{id}")
    public RecordedCourseModel updateCourse(@PathVariable String id, @RequestBody RecordedCourseModel course) {
        return recordedCourseService.updateCourse(id, course);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteCourse(@PathVariable String id) {
        return recordedCourseService.deleteCourse(id);
    }

    @GetMapping("/get/{id}")
    public RecordedCourseModel getCourse(@PathVariable String id) {
        return recordedCourseService.getCourseById(id);
    }

    @GetMapping("/get-all")
    public List<RecordedCourseModel> getAllCourses() {
        return recordedCourseService.getAllCourses();
    }

    @GetMapping("/get-published")
    public List<RecordedCourseModel> getPublishedCourses() {
        return recordedCourseService.getPublishedCourses();
    }

    @PostMapping("/add-review/{id}")
    public RecordedCourseModel addReview(@PathVariable String id, @RequestBody RecordedCourseReviewDTO review) {
        return recordedCourseService.addReview(id, review);
    }
}
