package com.talentboozt.s_backend.domains.plat_courses.controller;

import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.service.EmpCoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v2/emp-courses")
public class EmpCoursesController {

    @Autowired
    private EmpCoursesService empCoursesService;

    @GetMapping("/getByEmployeeId/{employeeId}")
    public List<EmpCoursesModel> getAllEmpCoursesByEmployeeId(@PathVariable String employeeId) {
        return empCoursesService.getEmpCoursesByEmployeeId(employeeId);
    }

    @PostMapping("/add")
    public EmpCoursesModel addEmpCourses(@RequestBody EmpCoursesModel empCourses) {
        return empCoursesService.addEmpCourses(empCourses);
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

    @PutMapping("/update-enrollment-status/{employeeId}/{courseId}/{status}")
    public EmpCoursesModel updateEnrollmentStatus(@PathVariable String employeeId, @PathVariable String courseId, @PathVariable String status) {
        return empCoursesService.updateEnrollmentStatus(employeeId, courseId, status);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEmpCourses(@PathVariable String id) {
        empCoursesService.deleteEmpCourses(id);
    }

    @DeleteMapping("/delete-single/{employeeId}/{courseId}")
    public EmpCoursesModel deleteEmpCourse(@PathVariable String employeeId, @PathVariable String courseId) {
        return empCoursesService.deleteEmpCourse(employeeId, courseId);
    }
}
