package com.talentboozt.s_backend.domains.com_courses.controller;

import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.service.CourseBatchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/course-batch")
public class CourseBatchController {
    private final CourseBatchService courseBatchService;

    public CourseBatchController(CourseBatchService courseBatchService) {
        this.courseBatchService = courseBatchService;
    }

    @GetMapping("/get-all")
    public List<CourseBatchModel> getAllCourseBatches() {
        return courseBatchService.getAllCourseBatches();
    }

    @GetMapping("/get/{courseId}")
    public List<CourseBatchModel> getCourseBatchesByCourseId(@PathVariable String courseId) {
        return courseBatchService.getCourseBatchesByCourseId(courseId);
    }
}
