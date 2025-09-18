package com.talentboozt.s_backend.domains.plat_courses.controller;

import com.talentboozt.s_backend.domains.plat_courses.dto.RecordedCourseEnrollment;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.service.RecordedCourseProgressService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recorded-course-progress")
public class RecordedCourseProgressController {

    private final RecordedCourseProgressService recordedCourseProgressService;

    public RecordedCourseProgressController(RecordedCourseProgressService recordedCourseProgressService) {
        this.recordedCourseProgressService = recordedCourseProgressService;
    }

    @GetMapping("/progress/{userId}")
    public RecordedCourseEnrollment getRecordedCoursesProgress(@PathVariable String userId) {
        return recordedCourseProgressService.getRecordedCoursesProgress(userId);
    }

    @GetMapping("/progress/{userId}/{courseId}")
    public RecordedCourseEnrollment getRecordedCourseProgress(@PathVariable String userId, @PathVariable String courseId) {
        return recordedCourseProgressService.getRecordedCourseProgress(userId, courseId);
    }

    @PostMapping("/progress/mark-watched")
    public EmpCoursesModel markLectureWatched(@RequestBody LectureWatchedPayload payload) {
        return recordedCourseProgressService.markLectureWatched(payload.getEmployeeId(), payload.getCourseId(), payload.getModuleId(), payload.getLectureId());
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class LectureWatchedPayload {
        String employeeId, courseId, moduleId, lectureId;
    }
}
