package com.talentboozt.s_backend.domains.plat_courses.controller;

import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.plat_courses.dto.*;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.service.EmpCoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v2/emp-courses")
public class EmpCoursesController {

    @Autowired
    private EmpCoursesService empCoursesService;

    @GetMapping("/get-all")
    public List<EmpCoursesModel> getAllEmpCourses() {
        return empCoursesService.getAllEmpCourses();
    }

    @GetMapping("/getByEmployeeId/{employeeId}")
    public List<EmpCoursesModel> getAllEmpCoursesByEmployeeId(@PathVariable String employeeId) {
        return empCoursesService.getEmpCoursesByEmployeeId(employeeId);
    }

    @GetMapping("/get-course/{employeeId}/{courseId}")
    public EmpCoursesModel getEmpCourseByEmployeeIdAndCourseId(@PathVariable String employeeId, @PathVariable String courseId) {
        return empCoursesService.getEmpCourseByEmployeeIdAndCourseId(employeeId, courseId);
    }

    @PostMapping("/add")
    public EmpCoursesModel addEmpCourses(@RequestBody EmpCoursesModel empCourses,
                                         @RequestParam(required = false) String type) {
        String courseType; //live, recorded
        courseType = Objects.requireNonNullElse(type, "live");
        return empCoursesService.addEmpCourses(empCourses, courseType);
    }

    @PutMapping("/update/{id}")
    public EmpCoursesModel updateEmpCourses(@PathVariable String id, @RequestBody EmpCoursesModel empCourses) {
        return empCoursesService.updateEmpCourses(id, empCourses);
    }

    @PutMapping("/edit-single/{employeeId}")
    public EmpCoursesModel editEmpCourse(@PathVariable String employeeId, @RequestBody CourseModel course) {
        return empCoursesService.editEmpCourse(employeeId, course);
    }

    @PutMapping("/update-module-payment/{employeeId}/{courseId}/{moduleId}/{status}")
    public EmpCoursesModel updateModulePayment(@PathVariable String employeeId, @PathVariable String courseId, @PathVariable String moduleId, @PathVariable String status) {
        return empCoursesService.updateModulePayment(employeeId, courseId, moduleId, status);
    }

    @PutMapping("/update-installment-payment/{employeeId}/{courseId}/{installmentId}/{status}")
    public EmpCoursesModel updateInstallmentPayment(@PathVariable String employeeId, @PathVariable String courseId, @PathVariable String installmentId, @PathVariable String status) {
        return empCoursesService.updateInstallmentPayment(employeeId, courseId, installmentId, status);
    }

    @PutMapping("/update-full-payment/{employeeId}/{courseId}/{installmentId}/{status}")
    public EmpCoursesModel updateFullPayment(@PathVariable String employeeId, @PathVariable String courseId, @PathVariable String installmentId, @PathVariable String status) {
        return empCoursesService.updateFullCoursePayment(employeeId, courseId, installmentId, status);
    }

    @PutMapping("/update-enrollment-status/{employeeId}/{courseId}/{status}")
    public EmpCoursesModel updateEnrollmentStatus(@PathVariable String employeeId, @PathVariable String courseId, @PathVariable String status) {
        return empCoursesService.updateEnrollmentStatus(employeeId, courseId, status);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEmpCourses(@PathVariable String id) {
        empCoursesService.deleteEmpCourses(id);
    }

    @DeleteMapping("/delete-single/{employeeId}/{courseId}")
    public EmpCoursesModel deleteEmpCourse(@PathVariable String employeeId,
                                           @RequestParam(required = false) String type,
                                           @PathVariable String courseId) {
        String courseType; //live, recorded
        courseType = Objects.requireNonNullElse(type, "live");
        return empCoursesService.deleteEmpCourse(employeeId, courseId, courseType);
    }

    @GetMapping("/get/all-payments")
    public List<PaymentDetailsDTO> getAllPayments() {
        return empCoursesService.getAllPaymentDetails();
    }

    @GetMapping("/get/all-certificates")
    public List<CertificateDetailsDTO> getAllCertificates() throws IOException {
        return empCoursesService.getAllCertificateDetails();
    }

    @GetMapping("/get/rec/{employeeId}/{courseId}")
    public RecordedCourseEnrollment getRecordedCourseByEmployeeIdAndCourseId(
            @PathVariable String employeeId,
            @PathVariable String courseId) {
        return empCoursesService.getRecordedCourseByEmployeeIdAndCourseId(employeeId, courseId);
    }

    @PutMapping("/update-rec/{employeeId}/{courseId}")
    public EmpCoursesModel updateRecordedCourse(
            @PathVariable String employeeId,
            @PathVariable String courseId,
            @RequestBody CourseUpdateDTO courseUpdate) {
        return empCoursesService.updateRecordedCourseProgress(employeeId, courseId, courseUpdate);
    }

    @PutMapping("/update-rec-review/{employeeId}/{courseId}")
    public EmpCoursesModel updateRecordedCourseReview(
            @PathVariable String employeeId,
            @PathVariable String courseId,
            @RequestBody ReviewDTO review) {
        return empCoursesService.updateRecordedCourseReview(employeeId, courseId, review);
    }

    @PutMapping("/complete-rec/{employeeId}/{courseId}")
    public EmpCoursesModel completeRecordedCourse(@PathVariable String employeeId, @PathVariable String courseId) {
        return empCoursesService.completeRecordedCourse(employeeId, courseId);
    }

    @PutMapping("/update-rec-payment/{employeeId}/{courseId}/{status}")
    public EmpCoursesModel updateRecordedCoursePayment(@PathVariable String employeeId, @PathVariable String courseId, @PathVariable String status) {
        return empCoursesService.updateRecordedCoursePayment(employeeId, courseId, status);
    }
}
