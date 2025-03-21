package com.talentboozt.s_backend.Service.PLAT_COURSES;

import com.talentboozt.s_backend.DTO.COM_COURSES.ModuleDTO;
import com.talentboozt.s_backend.Model.COM_COURSES.CourseModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.EmpCoursesModel;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.EmpCoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
            List<CourseModel> courses = empCoursesModel.getCourses();
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
            List<CourseModel> courses = empCoursesModel.getCourses();
            if (courses != null) {
                courses.removeIf(course -> course.getId().equals(courseId));
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
            List<CourseModel> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseModel c : courses) {
                    if (c.getId().equals(course.getId())) {
                        c.setName(course.getName());
                        c.setDescription(course.getDescription());
                        c.setOverview(course.getOverview());
                        c.setCategory(course.getCategory());
                        c.setOrganizer(course.getOrganizer());
                        c.setLevel(course.getLevel());
                        c.setPrice(course.getPrice());
                        c.setInstallment(course.getInstallment());
                        c.setDuration(course.getDuration());
                        c.setModules(course.getModules());
                        c.setRating(course.getRating());
                        c.setLanguage(course.getLanguage());
                        c.setLecturer(course.getLecturer());
                        c.setImage(course.getImage());
                        c.setSkills(course.getSkills());
                        c.setRequirements(course.getRequirements());
                        c.setPlatform(course.getPlatform());
                        c.setStartDate(course.getStartDate());
                        c.setFromTime(course.getFromTime());
                        c.setToTime(course.getToTime());
                        break;
                    }
                }
            }
            return empCoursesModel;
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public EmpCoursesModel updateModulePayment(String employeeId, String courseId, String moduleId, String status) {
        List<EmpCoursesModel> empCoursesList = getEmpCoursesByEmployeeId(employeeId);
        if (!empCoursesList.isEmpty()) {
            EmpCoursesModel empCoursesModel = empCoursesList.get(0);
            List<CourseModel> courses = empCoursesModel.getCourses();
            if (courses != null) {
                for (CourseModel c : courses) {
                    if (c.getId().equals(courseId)) {
                        for (ModuleDTO m : c.getModules()) {
                            if (m.getId().equals(moduleId)) {
                                m.setPaid(status);
                                return empCoursesModel;
                            }
                        }
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
