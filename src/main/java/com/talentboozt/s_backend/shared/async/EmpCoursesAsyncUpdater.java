package com.talentboozt.s_backend.shared.async;

import com.talentboozt.s_backend.domains.audit_logs.model.AsyncUpdateAuditLog;
import com.talentboozt.s_backend.domains.audit_logs.service.AsyncUpdateLogger;
import com.talentboozt.s_backend.domains.com_courses.dto.InstallmentDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.mongodb.EmpCoursesRepository;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpCoursesAsyncUpdater {

    private final EmpCoursesRepository empCoursesRepository;
    private final AsyncUpdateLogger asyncUpdateLogger;

    public EmpCoursesAsyncUpdater(EmpCoursesRepository empCoursesRepository, AsyncUpdateLogger asyncUpdateLogger) {
        this.empCoursesRepository = empCoursesRepository;
        this.asyncUpdateLogger = asyncUpdateLogger;
    }

    @Async("taskExecutor")
    public void updateEnrolledUsersOnCourseChange(
            String courseId,
            String batchId,
            CourseModel updatedCourse,
            CourseBatchModel updatedBatch
    ) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, batchId, "updateCourse");

        try {
            List<EmpCoursesModel> enrolledUsers = empCoursesRepository.findByCoursesCourseId(courseId);

            for (EmpCoursesModel user : enrolledUsers) {
                boolean updated = false;
                for (CourseEnrollment enrollment : user.getCourses()) {
                    if (enrollment.getCourseId().equals(courseId) &&
                            (batchId == null || batchId.equals(enrollment.getBatchId()))) {
                        enrollment.setCourseName(updatedCourse.getName());
                        enrollment.setOverview(updatedCourse.getOverview());
                        enrollment.setImage(updatedCourse.getImage());
                        enrollment.setOrganizer(updatedCourse.getOrganizer());

                        if (updatedBatch != null) {
                            enrollment.setModules(updatedBatch.getModules());
                            enrollment.setInstallment(updatedBatch.getInstallment());
                        }

                        updated = true;
                    }
                }

                if (updated) {
                    empCoursesRepository.save(user);
                }
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }

    @Async("taskExecutor")
    public void deleteCourseFromEmpCourses(String courseId) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, null, "deleteCourse");
        try{
            List<EmpCoursesModel> enrolledUsers = empCoursesRepository.findByCoursesCourseId(courseId);

            for (EmpCoursesModel user : enrolledUsers) {
                boolean changed = user.getCourses().removeIf(c -> c.getCourseId().equals(courseId));
                if (changed) {
                    empCoursesRepository.save(user);
                }
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }

    @Async("taskExecutor")
    public void updateModules(String courseId, String batchId, List<ModuleDTO> modules) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, batchId, "updateModules");

        try {
            List<EmpCoursesModel> users = empCoursesRepository.findByCoursesCourseId(courseId);
            for (EmpCoursesModel user : users) {
                boolean changed = false;
                for (CourseEnrollment enrollment : user.getCourses()) {
                    if (enrollment.getCourseId().equals(courseId) &&
                            (batchId == null || batchId.equals(enrollment.getBatchId()))) {
                        enrollment.setModules(modules);
                        changed = true;
                    }
                }
                if (changed) empCoursesRepository.save(user);
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }

    @Async("taskExecutor")
    public void updateInstallment(String courseId, String batchId, List<InstallmentDTO> installment) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, batchId, "updateInstallment");

        try {
            List<EmpCoursesModel> users = empCoursesRepository.findByCoursesCourseId(courseId);
            for (EmpCoursesModel user : users) {
                boolean changed = false;
                for (CourseEnrollment enrollment : user.getCourses()) {
                    if (enrollment.getCourseId().equals(courseId) &&
                            (batchId == null || batchId.equals(enrollment.getBatchId()))) {
                        enrollment.setInstallment(installment);
                        changed = true;
                    }
                }
                if (changed) empCoursesRepository.save(user);
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }

    @Async("taskExecutor")
    public void updateModulesAndInstallment(String courseId, String batchId, List<ModuleDTO> modules, List<InstallmentDTO> installment) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, batchId, "updateModulesAndInstallment");

        try {
            List<EmpCoursesModel> users = empCoursesRepository.findByCoursesCourseId(courseId);
            for (EmpCoursesModel user : users) {
                boolean changed = false;
                for (CourseEnrollment enrollment : user.getCourses()) {
                    if (enrollment.getCourseId().equals(courseId) &&
                            (batchId == null || batchId.equals(enrollment.getBatchId()))) {
                        enrollment.setModules(modules);
                        enrollment.setInstallment(installment);
                        changed = true;
                    }
                }
                if (changed) empCoursesRepository.save(user);
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }

    @Async("taskExecutor")
    public void updateSingleModule(String courseId, String batchId, ModuleDTO module) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, batchId, "updateSingleModule");

        try {
            List<EmpCoursesModel> users = empCoursesRepository.findByCoursesCourseId(courseId);
            for (EmpCoursesModel user : users) {
                boolean changed = false;
                for (CourseEnrollment enrollment : user.getCourses()) {
                    if (enrollment.getCourseId().equals(courseId) &&
                            (batchId == null || batchId.equals(enrollment.getBatchId()))) {
                        List<ModuleDTO> modules = enrollment.getModules();
                        for (int i = 0; i < modules.size(); i++) {
                            if (modules.get(i).getId().equals(module.getId())) {
                                modules.set(i, module);
                                changed = true;
                                break;
                            }
                        }
                    }
                }
                if (changed) empCoursesRepository.save(user);
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }

    @Async("taskExecutor")
    public void updateSingleInstallment(String courseId, String batchId, InstallmentDTO installment) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, batchId, "updateSingleInstallment");
        try {
            List<EmpCoursesModel> users = empCoursesRepository.findByCoursesCourseId(courseId);
            for (EmpCoursesModel user : users) {
                boolean changed = false;
                for (CourseEnrollment enrollment : user.getCourses()) {
                    if (enrollment.getCourseId().equals(courseId) &&
                            (batchId == null || batchId.equals(enrollment.getBatchId()))) {
                        List<InstallmentDTO> installments = enrollment.getInstallment();
                        for (int i = 0; i < installments.size(); i++) {
                            if (installments.get(i).getId().equals(installment.getId())) {
                                installments.set(i, installment);
                                changed = true;
                                break;
                            }
                        }
                    }
                }
                if (changed) empCoursesRepository.save(user);
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }

    @Async("taskExecutor")
    public void deleteSingleModule(String courseId, String batchId, String moduleId) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, batchId, "deleteSingleModule");

        try {
            List<EmpCoursesModel> users = empCoursesRepository.findByCoursesCourseId(courseId);
            for (EmpCoursesModel user : users) {
                boolean changed = false;
                for (CourseEnrollment enrollment : user.getCourses()) {
                    if (enrollment.getCourseId().equals(courseId) &&
                            (batchId == null || batchId.equals(enrollment.getBatchId()))) {
                        List<ModuleDTO> modules = enrollment.getModules();
                        changed = modules.removeIf(m -> m.getId().equals(moduleId));
                    }
                }
                if (changed) empCoursesRepository.save(user);
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }

    @Async("taskExecutor")
    public void deleteSingleInstallment(String courseId, String batchId, String installmentId) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, batchId, "deleteSingleInstallment");

        try {
            List<EmpCoursesModel> users = empCoursesRepository.findByCoursesCourseId(courseId);
            for (EmpCoursesModel user : users) {
                boolean changed = false;
                for (CourseEnrollment enrollment : user.getCourses()) {
                    if (enrollment.getCourseId().equals(courseId) &&
                            (batchId == null || batchId.equals(enrollment.getBatchId()))) {
                        List<InstallmentDTO> installments = enrollment.getInstallment();
                        changed = installments.removeIf(i -> i.getId().equals(installmentId));
                    }
                }
                if (changed) empCoursesRepository.save(user);
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }

    @Async("taskExecutor")
    public void addSingleModule(String courseId, String batchId, ModuleDTO module) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, batchId, "addSingleModule");

        try {
            List<EmpCoursesModel> users = empCoursesRepository.findByCoursesCourseId(courseId);
            for (EmpCoursesModel user : users) {
                boolean changed = false;
                for (CourseEnrollment enrollment : user.getCourses()) {
                    if (enrollment.getCourseId().equals(courseId) &&
                            (batchId == null || batchId.equals(enrollment.getBatchId()))) {
                        enrollment.getModules().add(module);
                        changed = true;
                    }
                }
                if (changed) empCoursesRepository.save(user);
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }

    @Async("taskExecutor")
    public void addSingleInstallment(String courseId, String batchId, InstallmentDTO installment) {
        AsyncUpdateAuditLog log = asyncUpdateLogger.createLog(courseId, batchId, "addSingleInstallment");

        try {
            List<EmpCoursesModel> users = empCoursesRepository.findByCoursesCourseId(courseId);
            for (EmpCoursesModel user : users) {
                boolean changed = false;
                for (CourseEnrollment enrollment : user.getCourses()) {
                    if (enrollment.getCourseId().equals(courseId) &&
                            (batchId == null || batchId.equals(enrollment.getBatchId()))) {
                        enrollment.getInstallment().add(installment);
                        changed = true;
                    }
                }
                if (changed) empCoursesRepository.save(user);
            }
            asyncUpdateLogger.markSuccess(log);
        } catch (Exception e) {
            asyncUpdateLogger.markFailure(log, e);
        }
    }
}

