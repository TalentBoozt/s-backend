package com.talentboozt.s_backend.Controller.PLAT_COURSES;

import com.talentboozt.s_backend.Model.COM_COURSES.CourseModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.EmpCoursesModel;
import com.talentboozt.s_backend.Service.PLAT_COURSES.EmpCoursesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/delete/{id}")
    public void deleteEmpCourses(@PathVariable String id) {
        empCoursesService.deleteEmpCourses(id);
    }

    @DeleteMapping("/delete-single/{employeeId}/{courseId}")
    public EmpCoursesModel deleteEmpCourse(@PathVariable String employeeId, @PathVariable String courseId) {
        return empCoursesService.deleteEmpCourse(employeeId, courseId);
    }
}
