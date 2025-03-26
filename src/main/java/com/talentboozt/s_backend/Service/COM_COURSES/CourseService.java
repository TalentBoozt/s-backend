package com.talentboozt.s_backend.Service.COM_COURSES;

import com.talentboozt.s_backend.DTO.COM_COURSES.InstallmentDTO;
import com.talentboozt.s_backend.DTO.COM_COURSES.ModuleDTO;
import com.talentboozt.s_backend.Model.COM_COURSES.CourseModel;
import com.talentboozt.s_backend.Model.EndUser.EmployeeModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.EmpCoursesModel;
import com.talentboozt.s_backend.Repository.COM_COURSES.CourseRepository;
import com.talentboozt.s_backend.Repository.EndUser.EmployeeRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.EmpCoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EmpCoursesRepository empCoursesRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<CourseModel> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<CourseModel> getCoursesByCompanyId(String companyId) {
        return courseRepository.findByCompanyId(companyId);
    }

    public CourseModel getCourseById(String id) {
        return courseRepository.findById(id).orElse(null);
    }

    public CourseModel createCourse(CourseModel course) {
        return courseRepository.save(course);
    }

    public CourseModel updateCourse(String id, CourseModel course) {
        CourseModel existingCourse = courseRepository.findById(id).orElse(null);
        if (existingCourse != null) {
            existingCourse.setName(course.getName());
            existingCourse.setDescription(course.getDescription());
            existingCourse.setOverview(course.getOverview());
            existingCourse.setCategory(course.getCategory());
            existingCourse.setOrganizer(course.getOrganizer());
            existingCourse.setLevel(course.getLevel());
            existingCourse.setPrice(course.getPrice());
            existingCourse.setInstallment(course.getInstallment());
            existingCourse.setDuration(course.getDuration());
            existingCourse.setModules(course.getModules());
            existingCourse.setRating(course.getRating());
            existingCourse.setLanguage(course.getLanguage());
            existingCourse.setLecturer(course.getLecturer());
            existingCourse.setImage(course.getImage());
            existingCourse.setSkills(course.getSkills());
            existingCourse.setRequirements(course.getRequirements());
            existingCourse.setCertificate(course.isCertificate());
            existingCourse.setPlatform(course.getPlatform());
            existingCourse.setStartDate(course.getStartDate());
            existingCourse.setFromTime(course.getFromTime());
            existingCourse.setToTime(course.getToTime());
            return courseRepository.save(existingCourse);
        }
        return null;
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }

    public CourseModel updateModule(String courseId, ModuleDTO module) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<ModuleDTO> modules = course.getModules();
            for (int i = 0; i < modules.size(); i++) {
                if (modules.get(i).getId().equals(module.getId())) {
                    modules.set(i, module);
                    course.setModules(modules);
                    return courseRepository.save(course);
                }
            }
        }
        return null;
    }

    public void deleteModule(String courseId, String moduleId) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<ModuleDTO> modules = course.getModules();
            for (int i = 0; i < modules.size(); i++) {
                if (modules.get(i).getId().equals(moduleId)) {
                    modules.remove(i);
                    course.setModules(modules);
                    courseRepository.save(course);
                    return;
                }
            }
        }
    }

    public CourseModel addModule(String courseId, ModuleDTO module) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<ModuleDTO> modules = course.getModules();
            if (modules == null) {
                modules = new java.util.ArrayList<>();
            }
            modules.add(module);
            course.setModules(modules);
            return courseRepository.save(course);
        }
        return null;
    }

    public CourseModel updateInstallment(String courseId, InstallmentDTO installment) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<InstallmentDTO> installmentList = course.getInstallment();
            for (int i = 0; i < installmentList.size(); i++) {
                if (installmentList.get(i).getId().equals(installment.getId())) {
                    installmentList.set(i, installment);
                    course.setInstallment(installmentList);
                    return courseRepository.save(course);
                }
            }
        }
        return null;
    }

    public void deleteInstallment(String courseId, String installmentId) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<InstallmentDTO> installmentList = course.getInstallment();
            for (int i = 0; i < installmentList.size(); i++) {
                if (installmentList.get(i).getId().equals(installmentId)) {
                    installmentList.remove(i);
                    course.setInstallment(installmentList);
                    courseRepository.save(course);
                    return;
                }
            }
        }
    }

    public CourseModel addInstallment(String courseId, InstallmentDTO installment) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<InstallmentDTO> installmentList = course.getInstallment();
            if (installmentList == null) {
                installmentList = new java.util.ArrayList<>();
            }
            installmentList.add(installment);
            course.setInstallment(installmentList);
            return courseRepository.save(course);
        }
        return null;
    }

    public List<EmployeeModel> getUsersEnrolledInCourse(String courseId) {
        List<EmpCoursesModel> enrollments = empCoursesRepository.findByCoursesCourseId(courseId);

        List<String> employeeIds = enrollments.stream()
                .map(EmpCoursesModel::getEmployeeId)
                .collect(Collectors.toList());

        return employeeRepository.findAllById(employeeIds);
    }

    public List<EmpCoursesModel> getEnrolls(String courseId) {
        return empCoursesRepository.findByCoursesCourseId(courseId);
    }
}
