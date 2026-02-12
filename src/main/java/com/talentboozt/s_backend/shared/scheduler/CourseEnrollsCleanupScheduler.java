package com.talentboozt.s_backend.shared.scheduler;

import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.mongodb.EmpCoursesRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CourseEnrollsCleanupScheduler {
    private final EmpCoursesRepository empCoursesRepository;

    public CourseEnrollsCleanupScheduler(EmpCoursesRepository empCoursesRepository) {
        this.empCoursesRepository = empCoursesRepository;
    }

    @Scheduled(cron = "0 0 2 * * ?") // Runs daily at 2 AM
    public void removeDuplicateCourses() {
        List<EmpCoursesModel> allEmployees = empCoursesRepository.findAll();

        for (EmpCoursesModel emp : allEmployees) {
            List<CourseEnrollment> originalCourses = emp.getCourses();
            if (originalCourses == null || originalCourses.isEmpty()) continue;

            // Use Set to track unique key combinations
            Set<String> seen = new HashSet<>();
            List<CourseEnrollment> deduplicated = new ArrayList<>();

            for (CourseEnrollment course : originalCourses) {
                String key = course.getCourseId() + "-" + course.getBatchId();
                if (seen.add(key)) {
                    deduplicated.add(course);  // Add only if not seen before
                }
            }

            if (deduplicated.size() < originalCourses.size()) {
                emp.setCourses(deduplicated);
                empCoursesRepository.save(emp);
                System.out.println("Updated employee " + emp.getEmployeeId() + " with deduplicated courses.");
            }
        }
    }
}
