package com.talentboozt.s_backend.domains.com_courses.service;

import com.stripe.exception.StripeException;
import com.talentboozt.s_backend.domains.com_courses.dto.*;
import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.shared.async.EmpCoursesAsyncUpdater;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.com_courses.repository.CourseRepository;
import com.talentboozt.s_backend.domains.user.repository.EmployeeRepository;
import com.talentboozt.s_backend.domains.plat_courses.repository.EmpCoursesRepository;
import com.talentboozt.s_backend.domains.payment.service.StripeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final EmpCoursesRepository empCoursesRepository;
    private final EmployeeRepository employeeRepository;
    private final StripeService stripeService;
    private final CourseBatchService courseBatchService;
    private final CourseMapperService courseMapper;
    private final EmpCoursesAsyncUpdater empCoursesAsyncUpdater;

    public CourseService(CourseRepository courseRepository, EmpCoursesRepository empCoursesRepository,
                         EmployeeRepository employeeRepository, StripeService stripeService,
                         CourseBatchService courseBatchService, CourseMapperService courseMapper,
                         EmpCoursesAsyncUpdater empCoursesAsyncUpdater) {
        this.courseRepository = courseRepository;
        this.empCoursesRepository = empCoursesRepository;
        this.employeeRepository = employeeRepository;
        this.stripeService = stripeService;
        this.courseBatchService = courseBatchService;
        this.courseMapper = courseMapper;
        this.empCoursesAsyncUpdater = empCoursesAsyncUpdater;
    }

    public List<CourseModel> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<CourseModel> getCoursesByCompanyId(String companyId) {
        return courseRepository.findByCompanyId(companyId);
    }

    public CourseModel getCourseById(String id) {
        return courseRepository.findById(id).orElse(null);
    }

    public CourseResponseDTO createCourse(CourseModel course) {
        CourseModel courseModel = courseRepository.save(course);
        CourseBatchModel latestBatch = setBatchDetails(new CourseBatchModel(), courseModel);
        latestBatch.setBatchName(generateBatchName(course.getName()));

        CourseBatchModel savedBatch = courseBatchService.saveBatch(latestBatch);
        return courseMapper.toResponseDTO(courseModel, savedBatch);
    }

    public CourseResponseDTO updateCourse(String id, CourseModel course, String batchId) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        CourseModel courseModel = courseRepository.save(course);
        CourseBatchModel updatedBatch = null;

        if (batchId != null) {
            CourseBatchModel batchModel = courseBatchService.getById(batchId);
            if (batchModel != null && batchModel.getCourseId().equals(id)) {
                CourseBatchModel toUpdate = setBatchDetails(batchModel, courseModel);
                toUpdate.setId(batchId);
                updatedBatch = courseBatchService.updateBatch(toUpdate);
            }
        }

        empCoursesAsyncUpdater.updateEnrolledUsersOnCourseChange(id, batchId, courseModel, updatedBatch);
        return courseMapper.toResponseDTO(courseModel, updatedBatch);
    }

    public void deleteCourse(String id, String batchId) throws StripeException {
        if (batchId != null && !batchId.isEmpty()) {
            CourseBatchModel batch = courseBatchService.getById(batchId);
            if (batch != null) {
                courseBatchService.deleteBatch(batchId);
            }
        }
        Optional<CourseModel> course = courseRepository.findById(id);
        if (course.isPresent()) {
            for (InstallmentDTO i: course.get().getInstallment()) {
                if (i.getPriceId() != null) stripeService.archivePrice(i.getPriceId());
            }
        }
        empCoursesAsyncUpdater.deleteCourseFromEmpCourses(id);
        courseRepository.deleteById(id);
    }

    public CourseResponseDTO updateModule(String courseId, ModuleDTO module, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseModel updatedCourse = null;
        CourseBatchModel updatedBatch = null;
        if (batch != null && course != null) {
            List<ModuleDTO> modules = batch.getModules();
            List<ModuleDTO> courseModules = course.getModules();
            if (modules == null) {
                modules = new ArrayList<>();
            }
            if (courseModules == null) {
                courseModules = new ArrayList<>();
            }
            for (int i = 0; i < modules.size(); i++) {
                if (modules.get(i).getId().equals(module.getId())) {
                    modules.set(i, module);
                    batch.setModules(modules);
                    updatedBatch = courseBatchService.updateBatch(batch);
                }
            }
            for (int i = 0; i < courseModules.size(); i++) {
                if (courseModules.get(i).getId().equals(module.getId())) {
                    courseModules.set(i, module);
                    course.setModules(courseModules);
                    updatedCourse = courseRepository.save(course);
                }
            }
            empCoursesAsyncUpdater.updateSingleModule(courseId, batch.getId(), module);
            return courseMapper.toResponseDTO(updatedCourse, updatedBatch);
        } else {
            if (course != null) {
                List<ModuleDTO> modules = course.getModules();
                if (modules == null) {
                    modules = new ArrayList<>();
                }
                for (int i = 0; i < modules.size(); i++) {
                    if (modules.get(i).getId().equals(module.getId())) {
                        modules.set(i, module);
                        course.setModules(modules);
                        updatedCourse = courseRepository.save(course);
                    }
                }
                empCoursesAsyncUpdater.updateSingleModule(courseId, null, module);
                return courseMapper.toResponseDTO(updatedCourse, null);
            }
        }
        return null;
    }

    public void deleteModule(String courseId, String moduleId, String batchId) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (batchId != null && !batchId.isEmpty()) {
            CourseBatchModel batch = courseBatchService.getById(batchId);
            if (batch != null && batch.getCourseId().equals(courseId)) {
                List<ModuleDTO> modules = batch.getModules();
                modules.removeIf(moduleDTO -> moduleDTO.getId().equals(moduleId));
                batch.setModules(modules);
                courseBatchService.updateBatch(batch);
            }
            if (course != null) {
                List<ModuleDTO> courseModules = course.getModules();
                courseModules.removeIf(moduleDTO -> moduleDTO.getId().equals(moduleId));
                course.setModules(courseModules);
                courseRepository.save(course);
            }
            empCoursesAsyncUpdater.deleteSingleModule(courseId, batchId, moduleId);
        } else {
            if (course != null) {
                List<ModuleDTO> courseModules = course.getModules();
                courseModules.removeIf(moduleDTO -> moduleDTO.getId().equals(moduleId));
                course.setModules(courseModules);
                courseRepository.save(course);
            }
            empCoursesAsyncUpdater.deleteSingleModule(courseId, null, moduleId);
        }
    }

    public CourseResponseDTO addModule(String courseId, ModuleDTO module, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseModel newCourse;
        CourseBatchModel newBatch;
        if (batch != null && course != null) {
            List<ModuleDTO> modules = batch.getModules();
            List<ModuleDTO> courseModules = course.getModules();
            if (modules == null) {
                modules = new java.util.ArrayList<>();
            }
            if (courseModules == null) {
                courseModules = new java.util.ArrayList<>();
            }
            modules.add(module);
            courseModules.add(module);
            batch.setModules(modules);
            course.setModules(courseModules);
            newBatch = courseBatchService.updateBatch(batch);
            newCourse = courseRepository.save(course);
            empCoursesAsyncUpdater.addSingleModule(courseId, newBatch.getId(), module);
            return courseMapper.toResponseDTO(newCourse, newBatch);
        } else {
            if (course != null) {
                List<ModuleDTO> modules = course.getModules();
                if (modules == null) {
                    modules = new java.util.ArrayList<>();
                }
                modules.add(module);
                course.setModules(modules);
                newCourse = courseRepository.save(course);
                empCoursesAsyncUpdater.addSingleModule(courseId, null, module);
                return courseMapper.toResponseDTO(newCourse, null);
            }
        }
        return null;
    }

    public CourseResponseDTO updateInstallment(String courseId, InstallmentDTO installment, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseModel updatedCourse = null;
        CourseBatchModel updatedBatch = null;
        if (course != null && batch != null) {
            List<InstallmentDTO> installmentList = course.getInstallment();
            List<InstallmentDTO> batchInstallmentList = batch.getInstallment();
            if (batchInstallmentList == null) {
                batchInstallmentList = new ArrayList<>();
            }
            if (installmentList == null) {
                installmentList = new ArrayList<>();
            }
            for (int i = 0; i < batchInstallmentList.size(); i++) {
                if (batchInstallmentList.get(i).getId().equals(installment.getId())) {
                    batchInstallmentList.set(i, installment);
                    batch.setInstallment(batchInstallmentList);
                    updatedBatch = courseBatchService.updateBatch(batch);
                }
            }
            for (int i = 0; i < installmentList.size(); i++) {
                if (installmentList.get(i).getId().equals(installment.getId())) {
                    installmentList.set(i, installment);
                    course.setInstallment(installmentList);
                    updatedCourse = courseRepository.save(course);
                }
            }
            empCoursesAsyncUpdater.updateSingleInstallment(courseId, batch.getId(), installment);
            return courseMapper.toResponseDTO(updatedCourse, updatedBatch);
        } else {
            if (course != null) {
                List<InstallmentDTO> installmentList = course.getInstallment();
                if (installmentList == null) {
                    installmentList = new ArrayList<>();
                }
                for (int i = 0; i < installmentList.size(); i++) {
                    if (installmentList.get(i).getId().equals(installment.getId())) {
                        installmentList.set(i, installment);
                        course.setInstallment(installmentList);
                        updatedCourse = courseRepository.save(course);
                    }
                }
                empCoursesAsyncUpdater.updateSingleInstallment(courseId, null, installment);
                return courseMapper.toResponseDTO(updatedCourse, null);
            }
        }
        return null;
    }

    public void deleteInstallment(String courseId, String installmentId, String batchId) throws StripeException {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (batchId != null && !batchId.isEmpty() && course != null) {
            CourseBatchModel batch = courseBatchService.getById(batchId);
            if (batch != null && batch.getCourseId().equals(courseId)) {
                List<InstallmentDTO> batchInstallmentList = batch.getInstallment();
                List<InstallmentDTO> courseInstallmentList = course.getInstallment();
                for (int i = 0; i < batchInstallmentList.size(); i++) {
                    if (batchInstallmentList.get(i).getId().equals(installmentId)) {
                        if (batchInstallmentList.get(i).getPriceId() != null) stripeService.archivePrice(batchInstallmentList.get(i).getPriceId());
                        batchInstallmentList.remove(i);
                        batch.setInstallment(batchInstallmentList);
                        courseBatchService.updateBatch(batch);
                        break;
                    }
                }
                for (int i = 0; i < courseInstallmentList.size(); i++) {
                    if (courseInstallmentList.get(i).getId().equals(installmentId)) {
                        if (courseInstallmentList.get(i).getPriceId() != null) stripeService.archivePrice(courseInstallmentList.get(i).getPriceId());
                        courseInstallmentList.remove(i);
                        course.setInstallment(courseInstallmentList);
                        courseRepository.save(course);
                        break;
                    }
                }
                empCoursesAsyncUpdater.deleteSingleInstallment(courseId, batchId, installmentId);
            }
        } else {
            if (course != null) {
                List<InstallmentDTO> installmentList = course.getInstallment();
                for (int i = 0; i < installmentList.size(); i++) {
                    if (installmentList.get(i).getId().equals(installmentId)) {
                        if (installmentList.get(i).getPriceId() != null) stripeService.archivePrice(installmentList.get(i).getPriceId());
                        installmentList.remove(i);
                        course.setInstallment(installmentList);
                        courseRepository.save(course);
                        break;
                    }
                }
                empCoursesAsyncUpdater.deleteSingleInstallment(courseId, null, installmentId);
            }
        }
    }

    public CourseResponseDTO addInstallment(String courseId, InstallmentDTO installment, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseModel newCourse;
        CourseBatchModel newBatch;
        if (batch != null && course != null) {
            List<InstallmentDTO> installmentList = batch.getInstallment();
            if (installmentList == null) {
                installmentList = new java.util.ArrayList<>();
            }
            installmentList.add(installment);
            batch.setInstallment(installmentList);
            newBatch = courseBatchService.updateBatch(batch);
            newCourse = courseRepository.save(course);
            empCoursesAsyncUpdater.addSingleInstallment(courseId, newBatch.getId(), installment);
            return courseMapper.toResponseDTO(newCourse, newBatch);
        } else {
            if (course != null) {
                List<InstallmentDTO> installmentList = course.getInstallment();
                if (installmentList == null) {
                    installmentList = new java.util.ArrayList<>();
                }
                installmentList.add(installment);
                course.setInstallment(installmentList);
                newCourse = courseRepository.save(course);
                empCoursesAsyncUpdater.addSingleInstallment(courseId, null, installment);
                return courseMapper.toResponseDTO(newCourse, null);
            }
        }
        return null;
    }

    public List<EmployeeModel> getUsersEnrolledInCourse(String courseId, String batchId) {
        if (batchId == null || batchId.isEmpty()) {
            // If no batchId is provided, fetch all enrollments for the course
            List<EmpCoursesModel> enrollments = empCoursesRepository.findByCoursesCourseId(courseId);
            List<String> employeeIds = enrollments.stream()
                    .map(EmpCoursesModel::getEmployeeId)
                    .collect(Collectors.toList());
            return employeeRepository.findAllById(employeeIds);
        }

        List<EmpCoursesModel> enrollments = empCoursesRepository.findByCoursesCourseIdAndCoursesBatchId(courseId, batchId);

        List<String> employeeIds = enrollments.stream()
                .map(EmpCoursesModel::getEmployeeId)
                .collect(Collectors.toList());

        return employeeRepository.findAllById(employeeIds);
    }

    public List<EmpCoursesModel> getEnrolls(String courseId, String batchId) {
        if (batchId == null || batchId.isEmpty()) {
            return empCoursesRepository.findByCoursesCourseId(courseId);
        }
        return empCoursesRepository.findByCoursesCourseIdAndCoursesBatchId(courseId, batchId);
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
            List<EmployeeModel> enrolledParticipants = getUsersEnrolledInCourse(course.getId(), null);
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

    public CourseResponseDTO updatePublicity(String courseId, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (batch.getId() != null && !batch.getId().isEmpty()) {
            if (course != null && !batch.getCourseId().equals(courseId)) {
                course.setPublicity(!course.isPublicity());
                CourseModel updatedCourse = courseRepository.save(course);
                batch.setPublicity(!batch.isPublicity());
                CourseBatchModel updatedBatch = courseBatchService.updateBatch(batch);

                return courseMapper.toResponseDTO(updatedCourse, updatedBatch);
            }
        } else {
            if (course != null) {
                course.setPublicity(!course.isPublicity());
                CourseModel updatedCourse = courseRepository.save(course);
                return courseMapper.toResponseDTO(updatedCourse, null);
            }
        }
        return null;
    }

    public CourseResponseDTO addMaterial(String courseId, MaterialsDTO materials, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseModel newCourse;
        CourseBatchModel newBatch;
        if (batch != null && course != null) {
            List<MaterialsDTO> materialsList = batch.getMaterials();
            List<MaterialsDTO> courseMaterials = course.getMaterials();
            if (materialsList == null) {
                materialsList = new ArrayList<>();
            }
            materialsList.add(materials);
            batch.setMaterials(materialsList);

            if (courseMaterials == null) {
                courseMaterials = new ArrayList<>();
            }
            courseMaterials.add(materials);
            course.setMaterials(courseMaterials);

            newCourse =  courseRepository.save(course);
            newBatch = courseBatchService.updateBatch(batch);

            return courseMapper.toResponseDTO(newCourse, newBatch);
        } else {
            if (course != null) {
                List<MaterialsDTO> materialsList = course.getMaterials();
                if (materialsList == null) {
                    materialsList = new ArrayList<>();
                }
                materialsList.add(materials);
                course.setMaterials(materialsList);
                newCourse = courseRepository.save(course);
                return courseMapper.toResponseDTO(newCourse, null);
            }
        }
        return null;
    }

    public CourseResponseDTO updateMaterial(String courseId, String id, MaterialsDTO materials, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseModel updatedCourse = null;
        CourseBatchModel updatedBatch = null;
        if (batch != null && batch.getMaterials() != null && course != null) {
            List<MaterialsDTO> materialsList = batch.getMaterials();
            List<MaterialsDTO> courseMaterials = course.getMaterials();
            for (int i = 0; i < materialsList.size(); i++) {
                if (materialsList.get(i).getId().equals(id)) {
                    materialsList.set(i, materials);
                    batch.setMaterials(materialsList);
                    updatedBatch = courseBatchService.updateBatch(batch);
                }
            }
            for (int i = 0; i < courseMaterials.size(); i++) {
                if (courseMaterials.get(i).getId().equals(id)) {
                    courseMaterials.set(i, materials);
                    course.setMaterials(courseMaterials);
                    updatedCourse = courseRepository.save(course);
                }
            }
            return courseMapper.toResponseDTO(updatedCourse, updatedBatch);
        } else {
            if (course != null) {
                List<MaterialsDTO> materialsList = course.getMaterials();
                for (int i = 0; i < materialsList.size(); i++) {
                    if (materialsList.get(i).getId().equals(id)) {
                        materialsList.set(i, materials);
                        course.setMaterials(materialsList);
                        updatedCourse = courseRepository.save(course);
                    }
                }
                return courseMapper.toResponseDTO(updatedCourse, null);
            }
        }
        return null;
    }

    public void deleteMaterial(String courseId, String id, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<MaterialsDTO> materialsList = course.getMaterials();
            materialsList.removeIf(materialsDTO -> materialsDTO.getId().equals(id));
            course.setMaterials(materialsList);
            courseRepository.save(course);
        }

        if (batch != null) {
            List<MaterialsDTO> batchMaterials = batch.getMaterials();
            batchMaterials.removeIf(materialsDTO -> materialsDTO.getId().equals(id));
            batch.setMaterials(batchMaterials);
            courseBatchService.updateBatch(batch);
        }
    }

    public List<MaterialsDTO> getMaterials(String courseId, CourseBatchModel batch) {
        if (batch != null && batch.getMaterials() != null) {
            return batch.getMaterials();
        }
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            return course.getMaterials();
        }
        return null;
    }

    public MaterialsDTO getMaterial(String courseId, String id, CourseBatchModel batch) {
        if (batch != null && batch.getMaterials() != null) {
            for (MaterialsDTO materialsDTO : batch.getMaterials()) {
                if (materialsDTO.getId().equals(id)) {
                    return materialsDTO;
                }
            }
        }
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

    public CourseResponseDTO incrementMaterialView(String courseId, String id, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseBatchModel updatedBatch;
        CourseModel updatedCourse;
        if (batch != null && batch.getMaterials() != null && course != null) {
            List<MaterialsDTO> materialsList = batch.getMaterials();
            List<MaterialsDTO> courseMaterials = course.getMaterials();
            for (MaterialsDTO materialsDTO : materialsList) {
                if (materialsDTO.getId().equals(id)) {
                    materialsDTO.setViewCount(materialsDTO.getViewCount() + 1);
                }
            }
            for (MaterialsDTO materialsDTO : courseMaterials) {
                if (materialsDTO.getId().equals(id)) {
                    materialsDTO.setViewCount(materialsDTO.getViewCount() + 1);
                }
            }
            batch.setMaterials(materialsList);
            course.setMaterials(courseMaterials);
            updatedBatch = courseBatchService.updateBatch(batch);
            updatedCourse = courseRepository.save(course);
            return courseMapper.toResponseDTO(updatedCourse, updatedBatch);
        } else {
            if (course != null) {
                List<MaterialsDTO> materialsList = course.getMaterials();
                for (MaterialsDTO materialsDTO : materialsList) {
                    if (materialsDTO.getId().equals(id)) {
                        materialsDTO.setViewCount(materialsDTO.getViewCount() + 1);
                    }
                }
                course.setMaterials(materialsList);
                updatedCourse = courseRepository.save(course);
                return courseMapper.toResponseDTO(updatedCourse, null);
            }
        }
        return null;
    }

    public CourseResponseDTO updateMaterialVisibility(String courseId, String id, String status, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseBatchModel updatedBatch;
        CourseModel updatedCourse;
        if (batch != null && batch.getMaterials() != null && course != null) {
            MaterialsDTO material = batch.getMaterials().stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
            if (material != null) {
                batch.setMaterials(batch.getMaterials().stream()
                        .peek(m -> {
                            if (m.getId().equals(id)) {
                                m.setVisibility(status);
                            }
                        })
                        .collect(Collectors.toList()));
                updatedBatch = courseBatchService.updateBatch(batch);
                material = course.getMaterials().stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
                if (material != null) {
                    course.setMaterials(course.getMaterials().stream()
                            .peek(m -> {
                                if (m.getId().equals(id)) {
                                    m.setVisibility(status);
                                }
                            })
                            .collect(Collectors.toList()));
                    updatedCourse = courseRepository.save(course);
                    return courseMapper.toResponseDTO(updatedCourse, updatedBatch);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            if (course != null) {
                MaterialsDTO material = course.getMaterials().stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
                if (material != null) {
                    course.setMaterials(course.getMaterials().stream()
                            .peek(m -> {
                                if (m.getId().equals(id)) {
                                    m.setVisibility(status);
                                }
                            })
                            .collect(Collectors.toList()));
                    updatedCourse = courseRepository.save(course);
                    return courseMapper.toResponseDTO(updatedCourse, null);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public CourseResponseDTO updateCourseStatus(String courseId, String status, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (batch.getId() != null && !batch.getId().isEmpty()) {
            if (course != null && batch.getCourseId().equals(courseId)) {
                course.setCourseStatus(status);
                CourseModel updatedCourse = courseRepository.save(course);
                batch.setCourseStatus(status);
                CourseBatchModel updatedBatch = courseBatchService.updateBatch(batch);

                return courseMapper.toResponseDTO(updatedCourse, updatedBatch);
            }
        } else {
            if (course != null) {
                course.setCourseStatus(status);
                CourseModel updatedCourse = courseRepository.save(course);
                return courseMapper.toResponseDTO(updatedCourse, null);
            }
        }
        return null;
    }

    public CourseResponseDTO addQuiz(String courseId, QuizDTO quiz, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseModel newCourse;
        CourseBatchModel newBatch;
        if (batch != null && course != null) {
            List<QuizDTO> batchQuizList = batch.getQuizzes();
            List<QuizDTO> courseQuizList = course.getQuizzes();
            if (batchQuizList == null) {
                batchQuizList = new ArrayList<>();
            }
            if (courseQuizList == null) {
                courseQuizList = new ArrayList<>();
            }
            batchQuizList.add(quiz);
            courseQuizList.add(quiz);
            batch.setQuizzes(batchQuizList);
            course.setQuizzes(courseQuizList);
            newBatch = courseBatchService.updateBatch(batch);
            newCourse = courseRepository.save(course);
            return courseMapper.toResponseDTO(newCourse, newBatch);
        } else {
            if (course != null) {
                List<QuizDTO> courseQuizList = course.getQuizzes();
                if (courseQuizList == null) {
                    courseQuizList = new ArrayList<>();
                }
                courseQuizList.add(quiz);
                course.setQuizzes(courseQuizList);
                newCourse = courseRepository.save(course);
                return courseMapper.toResponseDTO(newCourse, null);
            }
        }
        return null;
    }

    public CourseResponseDTO updateQuiz(String courseId, String quizId, QuizDTO quiz, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseModel updatedCourse = null;
        CourseBatchModel updatedBatch = null;
        if (batch != null && batch.getQuizzes() != null && course != null) {
            for (int i = 0; i < batch.getQuizzes().size(); i++) {
                if (batch.getQuizzes().get(i).getId().equals(quizId)) {
                    batch.getQuizzes().set(i, quiz);
                    updatedBatch = courseBatchService.updateBatch(batch);
                }
            }
            for (int i = 0; i < course.getQuizzes().size(); i++) {
                if (course.getQuizzes().get(i).getId().equals(quizId)) {
                    course.getQuizzes().set(i, quiz);
                    updatedCourse = courseRepository.save(course);
                }
            }
            return courseMapper.toResponseDTO(updatedCourse, updatedBatch);
        } else {
            if (course != null && course.getQuizzes() != null) {
                for (int i = 0; i < course.getQuizzes().size(); i++) {
                    if (course.getQuizzes().get(i).getId().equals(quizId)) {
                        course.getQuizzes().set(i, quiz);
                        updatedCourse = courseRepository.save(course);
                    }
                }
                return courseMapper.toResponseDTO(updatedCourse, null);
            }
        }
        return null;
    }

    public void deleteQuiz(String courseId, String quizId, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null) {
            List<QuizDTO> quizzes = course.getQuizzes();
            quizzes.removeIf(quizDTO -> quizDTO.getId().equals(quizId));
            course.setQuizzes(quizzes);
            courseRepository.save(course);
        }
        if (batch != null) {
            List<QuizDTO> batchQuizzes = batch.getQuizzes();
            batchQuizzes.removeIf(quizDTO -> quizDTO.getId().equals(quizId));
            batch.setQuizzes(batchQuizzes);
            courseBatchService.updateBatch(batch);
        }
    }

    public List<QuizDTO> getQuizzes(String courseId, CourseBatchModel batch) {
        if (batch != null && batch.getMaterials() != null) {
            return batch.getQuizzes();
        }
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        return (course != null) ? course.getQuizzes() : null;
    }

    public QuizDTO getQuiz(String courseId, String quizId, CourseBatchModel batch) {
        if (batch != null && batch.getQuizzes() != null) {
            return batch.getQuizzes().stream().filter(q -> q.getId().equals(quizId)).findFirst().orElse(null);
        }
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        if (course != null && course.getQuizzes() != null) {
            return course.getQuizzes().stream().filter(q -> q.getId().equals(quizId)).findFirst().orElse(null);
        }
        return null;
    }

    public CourseResponseDTO updateQuizVisibility(String courseId, String quizId, String status, CourseBatchModel batch) {
        CourseModel course = courseRepository.findById(courseId).orElse(null);
        CourseBatchModel updatedBatch;
        CourseModel updatedCourse;
        if (batch != null && batch.getQuizzes() != null && course != null) {
            QuizDTO quiz = batch.getQuizzes().stream().filter(q -> q.getId().equals(quizId)).findFirst().orElse(null);
            if (quiz != null) {
                batch.setQuizzes(batch.getQuizzes().stream()
                        .peek(q -> {
                            if (q.getId().equals(quizId)) {
                                q.setVisibility(status);
                            }
                        })
                        .collect(Collectors.toList()));
                updatedBatch = courseBatchService.updateBatch(batch);
                course.setQuizzes(course.getQuizzes().stream()
                        .peek(q -> {
                            if (q.getId().equals(quizId)) {
                                q.setVisibility(status);
                            }
                        })
                        .collect(Collectors.toList()));
                updatedCourse = courseRepository.save(course);
                return courseMapper.toResponseDTO(updatedCourse, updatedBatch);
            } else {
                return null;
            }
        } else {
            if (course != null) {
                QuizDTO quiz = course.getQuizzes().stream().filter(q -> q.getId().equals(quizId)).findFirst().orElse(null);
                if (quiz != null) {
                    course.setQuizzes(course.getQuizzes().stream()
                            .peek(q -> {
                                if (q.getId().equals(quizId)) {
                                    q.setVisibility(status);
                                }
                            })
                            .collect(Collectors.toList()));
                    updatedCourse = courseRepository.save(course);
                    return courseMapper.toResponseDTO(updatedCourse, null);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private String generateBatchName(String courseName) {
        String firstStrings = courseName.split(" ")[0];
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        return firstStrings + " - " + currentDate;
    }

    private CourseBatchModel setBatchDetails(CourseBatchModel batch, CourseModel course) {
        batch.setCourseId(course.getId());
        batch.setStartDate(course.getStartDate());
        batch.setFromTime(course.getFromTime());
        batch.setToTime(course.getToTime());
        batch.setUtcStart(course.getUtcStart());
        batch.setUtcEnd(course.getUtcEnd());
        batch.setTrainerTimezone(course.getTrainerTimezone());
        batch.setCourseStatus(course.getCourseStatus());
        batch.setPublicity(course.isPublicity());
        batch.setCurrency(course.getCurrency());
        batch.setPrice(course.getPrice());
        batch.setOnetimePayment(course.isOnetimePayment());
        batch.setPaymentMethod(course.getPaymentMethod());
        batch.setDuration(course.getDuration());
        batch.setLanguage(course.getLanguage());
        batch.setPlatform(course.getPlatform());
        batch.setLocation(course.getLocation());
        batch.setImage(course.getImage());
        batch.setLecturer(course.getLecturer());
        batch.setModules(course.getModules());
        batch.setInstallment(course.getInstallment());
        batch.setMaterials(course.getMaterials());
        batch.setQuizzes(course.getQuizzes());
        return batch;
    }
}
