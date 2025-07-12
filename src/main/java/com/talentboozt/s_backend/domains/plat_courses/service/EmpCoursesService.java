package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.com_courses.dto.InstallmentDTO;
import com.talentboozt.s_backend.domains.com_courses.dto.ModuleDTO;
import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.EmpCoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class EmpCoursesService {

    @Autowired
    private EmpCoursesRepository empCoursesRepository;

    public List<EmpCoursesModel> getEmpCoursesByEmployeeId(String employeeId) { return empCoursesRepository.findByEmployeeId(employeeId); }

    public EmpCoursesModel addEmpCourses(EmpCoursesModel empCourses) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(empCourses.getEmployeeId());
        EmpCoursesModel empCoursesModel;
        if (!empCoursesList.isEmpty()) {
            empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses == null) {
                courses = new java.util.ArrayList<>(); // Initialize the courses list if it's null
            }
            courses.addAll(empCourses.getCourses());
            empCoursesModel.setCourses(courses);
        } else {
            empCoursesModel = empCoursesRepository.save(empCourses);
        }

        empCoursesRepository.save(empCoursesModel);

        return empCoursesModel;
    }

    public EmpCoursesModel updateEmpCourses(String id, EmpCoursesModel empCourses) {
        EmpCoursesModel empCoursesModel = empCoursesRepository.findById(id).orElse(null);
        if (empCoursesModel != null) {
            empCoursesModel.setEmployeeId(empCourses.getEmployeeId());
            empCoursesModel.setCourses(empCourses.getCourses());
            return empCoursesRepository.save(empCoursesModel);
        }
        return null;
    }

    public void deleteEmpCourses(String id) { empCoursesRepository.deleteById(id); }

    public EmpCoursesModel deleteEmpCourse(String employeeId, String courseId) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                courses.removeIf(course -> course.getCourseId().equals(courseId));
                empCoursesModel.setCourses(courses);
                return empCoursesRepository.save(empCoursesModel);
            }
            return empCoursesModel;
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public EmpCoursesModel editEmpCourse(String employeeId, CourseModel course) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment c : courses) {
                    if (c.getCourseId().equals(course.getId())) {
                        c.setCourseName(course.getName());
                        c.setOverview(course.getOverview());
                        c.setOrganizer(course.getOrganizer());
                        c.setModules(course.getModules());
                        c.setInstallment(course.getInstallment());
                        c.setImage(course.getImage());
                        c.setEnrollmentDate(LocalDateTime.now().toString());
                        c.setStatus("Enrolled");
                        break;
                    }
                }
            }
            return empCoursesRepository.save(empCoursesModel);
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public EmpCoursesModel updateModulePayment(String employeeId, String courseId, String moduleId, String status) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment c : courses) {
                    if (c.getCourseId().equals(courseId)) {
                        for (ModuleDTO m : c.getModules()) {
                            if (m.getId().equals(moduleId)) {
                                m.setPaid(status);
                                return empCoursesRepository.save(empCoursesModel);
                            }
                        }
                    }
                }
            }
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public EmpCoursesModel updateInstallmentPayment(String employeeId, String courseId, String installmentId, String status){
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment c : courses) {
                    if (c.getCourseId().equals(courseId)) {
                        for (InstallmentDTO i : c.getInstallment()) {
                            if (i.getId().equals(installmentId)) {
                                i.setPaid(status);
                                if (status.equals("pending")) i.setRequestDate(LocalDateTime.now().toString());
                                if (status.equals("paid") || status.equals("cancelled")) i.setPaymentDate(LocalDateTime.now().toString());
                            }
                        }
                        for (ModuleDTO m : c.getModules()) {
                            if (m.getInstallmentId().equals(installmentId)) {
                                m.setPaid("true");
                            }
                        }
                        return empCoursesRepository.save(empCoursesModel);
                    }
                }
            }
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public void updateFullCoursePayment(String userId, String courseId, String installmentId, String paid) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(userId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment c : courses) {
                    if (c.getCourseId().equals(courseId)) {
                        for (InstallmentDTO i : c.getInstallment()) {
                            if (i.getId().equals(installmentId)) {
                                i.setPaid(paid);
                                if (paid.equals("pending")) i.setRequestDate(LocalDateTime.now().toString());
                                if (paid.equals("paid") || paid.equals("cancelled")) i.setPaymentDate(LocalDateTime.now().toString());
                            }
                        }
                        for (ModuleDTO m : c.getModules()) {
                            m.setPaid("true");
                        }
                        empCoursesRepository.save(empCoursesModel);
                    }
                }
            }
        }
    }

    public EmpCoursesModel updateEnrollmentStatus(String employeeId, String courseId, String status) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseEnrollment> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseEnrollment c : courses) {
                    if (c.getCourseId().equals(courseId)) {
                        c.setStatus(status);
                        return empCoursesRepository.save(empCoursesModel);
                    }
                }
            }
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    @Async
    public CompletableFuture<List<EmpCoursesModel>> getEmpCoursesByEmployeeIdAsync(String employeeId) {
        List<EmpCoursesModel> empCourses = getEmpCoursesByEmployeeId(employeeId);
        return CompletableFuture.completedFuture(empCourses);
    }
}
