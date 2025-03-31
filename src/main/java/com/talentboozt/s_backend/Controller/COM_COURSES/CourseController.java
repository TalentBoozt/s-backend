package com.talentboozt.s_backend.Controller.COM_COURSES;

import com.talentboozt.s_backend.DTO.COM_COURSES.InstallmentDTO;
import com.talentboozt.s_backend.DTO.COM_COURSES.ModuleDTO;
import com.talentboozt.s_backend.Model.COM_COURSES.CourseModel;
import com.talentboozt.s_backend.Model.EndUser.EmployeeModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.EmpCoursesModel;
import com.talentboozt.s_backend.Service.COM_COURSES.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/all")
    public List<CourseModel> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/company/{companyId}")
    public List<CourseModel> getCoursesByCompanyId(@PathVariable String companyId) {
        return courseService.getCoursesByCompanyId(companyId);
    }

    @GetMapping("/get/{id}")
    public CourseModel getCourseById(@PathVariable String id) {
        return courseService.getCourseById(id);
    }

    @PostMapping("/add")
    public CourseModel addCourse(@RequestBody CourseModel course) {
        return courseService.createCourse(course);
    }

    @PutMapping("/update/{id}")
    public CourseModel updateCourse(@PathVariable String id, @RequestBody CourseModel course) {
        return courseService.updateCourse(id, course);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
    }

    @PutMapping("/update-module/{courseId}")
    public CourseModel updateModule(@PathVariable String courseId, @RequestBody ModuleDTO module) {
        return courseService.updateModule(courseId, module);
    }

    @DeleteMapping("/delete-module/{courseId}/{moduleId}")
    public void deleteModule(@PathVariable String courseId, @PathVariable String moduleId) {
        courseService.deleteModule(courseId, moduleId);
    }

    @PostMapping("/add-module/{courseId}")
    public CourseModel addModule(@PathVariable String courseId, @RequestBody ModuleDTO module) {
        return courseService.addModule(courseId, module);
    }

    @PutMapping("/update-installment/{courseId}")
    public CourseModel updateInstallment(@PathVariable String courseId, @RequestBody InstallmentDTO installment) {
        return courseService.updateInstallment(courseId, installment);
    }

    @DeleteMapping("/delete-installment/{courseId}/{installmentId}")
    public void deleteInstallment(@PathVariable String courseId, @PathVariable String installmentId) {
        courseService.deleteInstallment(courseId, installmentId);
    }

    @PostMapping("/add-installment/{courseId}")
    public CourseModel addInstallment(@PathVariable String courseId, @RequestBody InstallmentDTO installment) {
        return courseService.addInstallment(courseId, installment);
    }

    @GetMapping("/get/{courseId}/users")
    public List<EmployeeModel> getUsersEnrolledInCourse(@PathVariable String courseId) {
        return courseService.getUsersEnrolledInCourse(courseId);
    }

    @GetMapping("/get/{courseId}/enrolls")
    public List<EmpCoursesModel> getEnrollesInCourse(@PathVariable String courseId) {
        return courseService.getEnrolls(courseId);
    }

    @GetMapping("/get/categories")
    public List<String> getCategories() {
        return courseService.getCategories();
    }

    @GetMapping("/overview/{companyId}")
    public ResponseEntity<Map<String, Integer>> getCoursesOverviewByCompanyId(@PathVariable String companyId) {
        Map<String, Integer> courseOverview = courseService.getCoursesOverviewByCompanyId(companyId);
        return ResponseEntity.ok(courseOverview);
    }
}
