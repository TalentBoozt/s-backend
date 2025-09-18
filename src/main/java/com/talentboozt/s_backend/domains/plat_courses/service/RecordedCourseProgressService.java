package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.com_courses.dto.RecLectureDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.RecModuleDTO;
import com.talentboozt.s_backend.domains.com_courses.model.RecordedCourseModel;
import com.talentboozt.s_backend.domains.plat_courses.dto.*;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.EmpCoursesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecordedCourseProgressService {

    private final EmpCoursesRepository empCoursesRepository;

    public RecordedCourseProgressService(EmpCoursesRepository empCoursesRepository) {
        this.empCoursesRepository = empCoursesRepository;
    }

    public EmpCoursesModel markLectureWatched(String employeeId, String courseId, String moduleId, String lectureId) {
        List<EmpCoursesModel> users = empCoursesRepository.findAllByEmployeeId(employeeId);
        if (users.isEmpty()) throw new RuntimeException("Employee not found");

        EmpCoursesModel user = users.get(0);
        List<RecordedCourseEnrollment> courses = user.getRecordedCourses();

        for (RecordedCourseEnrollment course : courses) {
            if (course.getCourseId().equals(courseId)) {

                boolean updated = false;

                for (ModuleProgressDTO module : course.getModuleProgress()) {
                    if (module.getModuleId().equals(moduleId)) {
                        for (LectureProgressDTO lecture : module.getLectures()) {
                            if (lecture.getLectureId().equals(lectureId) && !lecture.isWatched()) {
                                lecture.setWatched(true);
                                lecture.setLastWatchedAt(LocalDateTime.now().toString());
                                updated = true;
                                break;
                            }
                        }
                    }
                }

                if (updated) {
                    updateCourseProgress(course);
                    return empCoursesRepository.save(user);
                }
            }
        }

        throw new RuntimeException("Course/module/lecture not found");
    }

    public void updateCourseProgress(RecordedCourseEnrollment course) {
        int total = 0;
        int watched = 0;

        for (ModuleProgressDTO module : course.getModuleProgress()) {
            for (LectureProgressDTO lecture : module.getLectures()) {
                total++;
                if (lecture.isWatched()) watched++;
            }
        }

        if (total == 0) {
            course.setOverallProgress(0);
        } else {
            int progress = (int) Math.round((watched * 100.0) / total);
            course.setOverallProgress(progress);
        }

        // Update status if fully watched
        if (course.getOverallProgress() == 100) {
            course.setStatus("completed");
        } else if (course.getOverallProgress() > 0) {
            course.setStatus("in-progress");
        }
    }

    public RecordedCourseEnrollment initRecordedCourseEnrollment(RecordedCourseModel course) {
        List<ModuleProgressDTO> moduleProgress = new ArrayList<>();

        for (RecModuleDTO module : course.getModules()) {
            List<LectureProgressDTO> lectureProgress = new ArrayList<>();
            for (RecLectureDTO lecture : module.getLectures()) {
                lectureProgress.add(new LectureProgressDTO(
                        lecture.getId(),
                        lecture.getTitle(),
                        false,
                        0,
                        0,
                        null
                ));
            }

            moduleProgress.add(new ModuleProgressDTO(
                    module.getId(),
                    module.getTitle(),
                    0,
                    0,
                    lectureProgress,
                    false
            ));
        }

        return new RecordedCourseEnrollment(
                course.getId(),
                course.getTitle(),
                "purchased",
                course.getLecturer(),
                LocalDateTime.now().toString(),
                0,
                new CourseProgressDTO(),
                moduleProgress,
                new ReviewDTO(),
                course.getImage(),
                course.getDescription(),
                new ArrayList<>()
        );
    }

    public RecordedCourseEnrollment getRecordedCoursesProgress(String userId) {
        List<EmpCoursesModel> users = empCoursesRepository.findAllByEmployeeId(userId);
        if (users.isEmpty()) throw new RuntimeException("Employee not found");

        EmpCoursesModel user = users.get(0);
        List<RecordedCourseEnrollment> courses = user.getRecordedCourses();

        if (courses.isEmpty()) {
            return initRecordedCourseEnrollment(new RecordedCourseModel());
        } else {
            return courses.get(0);
        }
    }

    public RecordedCourseEnrollment getRecordedCourseProgress(String userId, String courseId) {
        List<EmpCoursesModel> users = empCoursesRepository.findAllByEmployeeId(userId);
        if (users.isEmpty()) throw new RuntimeException("Employee not found");

        EmpCoursesModel user = users.get(0);
        List<RecordedCourseEnrollment> courses = user.getRecordedCourses();

        for (RecordedCourseEnrollment course : courses) {
            if (course.getCourseId().equals(courseId)) {
                return course;
            }
        }

        return initRecordedCourseEnrollment(new RecordedCourseModel());
    }
}
