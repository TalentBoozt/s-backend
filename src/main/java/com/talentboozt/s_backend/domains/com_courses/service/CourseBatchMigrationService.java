package com.talentboozt.s_backend.domains.com_courses.service;

import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.com_courses.repository.mongodb.CourseBatchRepository;
import com.talentboozt.s_backend.domains.com_courses.repository.mongodb.CourseRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseBatchMigrationService {

    private final CourseRepository courseRepository;
    private final CourseBatchRepository courseBatchRepository;
    private final CourseBatchService courseBatchService;

    public CourseBatchMigrationService(
            CourseRepository courseRepository,
            CourseBatchRepository courseBatchRepository,
            CourseBatchService courseBatchService) {
        this.courseRepository = courseRepository;
        this.courseBatchRepository = courseBatchRepository;
        this.courseBatchService = courseBatchService;
    }

    public void backfillMissingBatches() {
        List<CourseModel> allCourses = courseRepository.findAll();

        for (CourseModel course : allCourses) {
            boolean batchExists = courseBatchRepository.existsByCourseId(course.getId());
            if (batchExists) {
                continue;
            }

            CourseBatchModel batch = new CourseBatchModel();
            batch.setCourseId(course.getId());
            batch.setBatchName("Default Batch - Migrated");
            batch.setCurrency(course.getCurrency());
            batch.setPrice(course.getPrice());
            batch.setOnetimePayment(course.isOnetimePayment());
            batch.setInstallment(course.getInstallment());
            batch.setDuration(course.getDuration());
            batch.setModules(course.getModules());
            batch.setLanguage(course.getLanguage());
            batch.setLecturer(course.getLecturer());
            batch.setImage(course.getImage());
            batch.setPlatform(course.getPlatform());
            batch.setLocation(course.getLocation());
            batch.setStartDate(course.getStartDate());
            batch.setFromTime(course.getFromTime());
            batch.setToTime(course.getToTime());
            batch.setUtcStart(course.getUtcStart());
            batch.setUtcEnd(course.getUtcEnd());
            batch.setTrainerTimezone(course.getTrainerTimezone());
            batch.setCourseStatus(course.getCourseStatus());
            batch.setPaymentMethod(course.getPaymentMethod());
            batch.setPublicity(course.isPublicity());
            batch.setMaterials(course.getMaterials());
            batch.setQuizzes(course.getQuizzes());
            batch.setEnrolledUserIds(new ArrayList<>());

            courseBatchService.saveBatch(batch);
        }
    }
}
