package com.talentboozt.s_backend.Service.COM_COURSES;

import com.talentboozt.s_backend.DTO.COM_COURSES.InstallmentDTO;
import com.talentboozt.s_backend.DTO.COM_COURSES.MaterialsDTO;
import com.talentboozt.s_backend.DTO.COM_COURSES.ModuleDTO;
import com.talentboozt.s_backend.Model.COM_COURSES.CourseModel;
import com.talentboozt.s_backend.Model.EndUser.EmployeeModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.EmpCoursesModel;
import com.talentboozt.s_backend.Repository.COM_COURSES.CourseRepository;
import com.talentboozt.s_backend.Repository.EndUser.EmployeeRepository;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.EmpCoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public List<String> getCategories() {
        return courseRepository.findAll().stream()
                .map(CourseModel::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getCoursesOverviewByCompanyId(String companyId) {
        Map<String, Integer> overview = new HashMap<>();

        // Fetch courses by companyId
        List<CourseModel> courses = courseRepository.findByCompanyId(companyId);

        int totalTrainingHours = 0;
        int totalCompletedTrainings = 0;
        int totalInProgressTrainings = 0;
        int totalParticipants = 0;

        // Iterate over each course
        for (CourseModel course : courses) {
            // Add the duration in hours to totalTrainingHours
            totalTrainingHours += calculateTotalTrainingHours(course);

            // Get enrolled participants
            List<EmployeeModel> enrolledParticipants = getUsersEnrolledInCourse(course.getId());
            totalParticipants += enrolledParticipants.size();

            // Determine status of the course
            String courseStatus = course.getCourseStatus();
            if ("completed".equalsIgnoreCase(courseStatus)) {
                totalCompletedTrainings ++;
            } else if ("ongoing".equalsIgnoreCase(courseStatus)) {
                totalInProgressTrainings ++;
            }
        }

        // Add the results to the map
        overview.put("totalTrainingHours", totalTrainingHours);
        overview.put("totalCompletedTrainings", totalCompletedTrainings);
        overview.put("totalInProgressTrainings", totalInProgressTrainings);
        overview.put("totalParticipants", totalParticipants);

        return overview;
    }

    private int calculateTotalTrainingHours(CourseModel course) {
        int totalMinutes = 0;

        // Ensure that the course has modules
        if (course.getModules() != null && !course.getModules().isEmpty()) {
            for (ModuleDTO module : course.getModules()) {
                String startTime = module.getStart(); // "14:45"
                String endTime = module.getEnd(); // "16:09"

                try {
                    // Parse the times into LocalTime
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    LocalTime start = LocalTime.parse(startTime, formatter);
                    LocalTime end = LocalTime.parse(endTime, formatter);

                    // Calculate the difference in minutes
                    long minutes = java.time.Duration.between(start, end).toMinutes();
                    totalMinutes += minutes; // Add to total training duration in minutes
                } catch (Exception e) {
                    // Handle any invalid time format gracefully
                    System.err.println("Invalid time format for module: " + module.getName());
                }
            }
        }

        // Convert total minutes to hours
        return totalMinutes; // Return the total training hours
    }

    public CourseModel updatePublicity(String courseId) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.setPublicity(!course.isPublicity());
            return courseRepository.save(course);
        }
        return null;
    }

    public CourseModel addMaterial(String courseId, MaterialsDTO materials) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<MaterialsDTO> materialsList = course.getMaterials();
            if (materialsList == null) {
                materialsList = new java.util.ArrayList<>();
            }
            materialsList.add(materials);
            course.setMaterials(materialsList);
            return courseRepository.save(course);
        }
        return null;
    }

    public CourseModel updateMaterial(String courseId, String id, MaterialsDTO materials) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<MaterialsDTO> materialsList = course.getMaterials();
            for (int i = 0; i < materialsList.size(); i++) {
                if (materialsList.get(i).getId().equals(id)) {
                    materialsList.set(i, materials);
                    course.setMaterials(materialsList);
                    return courseRepository.save(course);
                }
            }
        }
        return null;
    }

    public void deleteMaterial(String courseId, String id) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<MaterialsDTO> materialsList = course.getMaterials();
            for (int i = 0; i < materialsList.size(); i++) {
                if (materialsList.get(i).getId().equals(id)) {
                    materialsList.remove(i);
                    course.setMaterials(materialsList);
                    courseRepository.save(course);
                    return;
                }
            }
        }
    }

    public List<MaterialsDTO> getMaterials(String courseId) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            return course.getMaterials();
        }
        return null;
    }

    public MaterialsDTO getMaterial(String courseId, String id) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<MaterialsDTO> materialsList = course.getMaterials();
            for (MaterialsDTO materialsDTO : materialsList) {
                if (materialsDTO.getId().equals(id)) {
                    return materialsDTO;
                }
            }
        }
        return null;
    }

    public MaterialsDTO incrementMaterialView(String courseId, String id) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<MaterialsDTO> materialsList = course.getMaterials();
            for (MaterialsDTO materialsDTO : materialsList) {
                if (materialsDTO.getId().equals(id)) {
                    materialsDTO.setViewCount(materialsDTO.getViewCount() + 1);
                    return materialsDTO;
                }
            }
        }
        return null;
    }

    public MaterialsDTO updateMaterialVisibility(String courseId, String id, String status) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<MaterialsDTO> materialsList = course.getMaterials();
            for (MaterialsDTO materialsDTO : materialsList) {
                if (materialsDTO.getId().equals(id)) {
                    materialsDTO.setVisibility(status);
                    return materialsDTO;
                }
            }
        }
        return null;
    }

    public CourseModel updateCourseStatus(String courseId, String status) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            course.setCourseStatus(status);
            return courseRepository.save(course);
        }
        return null;
    }
}
