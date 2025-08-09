package com.talentboozt.s_backend.domains.com_courses.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.talentboozt.s_backend.domains.com_courses.dto.*;
import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.com_courses.service.CourseBatchMigrationService;
import com.talentboozt.s_backend.domains.com_courses.service.CourseBatchService;
import com.talentboozt.s_backend.domains.com_courses.service.CourseMapperService;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.com_courses.service.CourseService;
import com.talentboozt.s_backend.domains.payment.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/course")
public class CourseController {

    private final CourseService courseService;
    private final StripeService stripeService;
    private final CourseBatchService courseBatchService;
    private final CourseMapperService courseMapper;
    private final CourseBatchMigrationService migrationService;

    public CourseController(
            CourseService courseService,
            StripeService stripeService,
            CourseBatchService courseBatchService,
            CourseMapperService courseMapper,
            CourseBatchMigrationService migrationService
    ) {
        this.courseService = courseService;
        this.stripeService = stripeService;
        this.courseBatchService = courseBatchService;
        this.courseMapper = courseMapper;
        this.migrationService = migrationService;
    }

    @GetMapping("/all")
    public List<CourseResponseDTO> getAllCourses() {
        List<CourseResponseDTO> responseDTOS =  new ArrayList<>();
        List<CourseModel> courses = courseService.getAllCourses();
        for (CourseModel course : courses) {
            CourseBatchModel batch = courseBatchService.getLatestBatchByCourseId(course.getId());
            responseDTOS.add(courseMapper.toResponseDTO(course, batch));
        }
        return responseDTOS;
    }

    @GetMapping("/company/{companyId}")
    public List<CourseResponseDTO> getCoursesByCompanyId(
            @PathVariable String companyId
    ) {
        List<CourseResponseDTO> responseDTOS = new ArrayList<>();
        List<CourseModel> courses = courseService.getCoursesByCompanyId(companyId);
        for (CourseModel course : courses) {
            CourseBatchModel batch = courseBatchService.getLatestBatchByCourseId(course.getId());
            responseDTOS.add(courseMapper.toResponseDTO(course, batch));
        }
        return responseDTOS;
    }

    @GetMapping("/get/{id}")
    public CourseResponseDTO getCourseById(
            @PathVariable String id,
            @RequestParam(required = false) String batchId
    ) {
        CourseModel course = courseService.getCourseById(id);
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(id);

        return courseMapper.toResponseDTO(course, batch);
    }

    @PostMapping("/add")
    public CourseResponseDTO addCourse(@RequestBody CourseModel course) {
        return courseService.createCourse(course);
    }

    @PutMapping("/update/{id}")
    public CourseResponseDTO updateCourse(
            @PathVariable String id,
            @RequestBody CourseModel course,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(id);
        return courseService.updateCourse(id, course, batch.getId());
    }

    @PutMapping("/update-new-batch/{id}")
    public CourseResponseDTO updateCourseWithNewBatch(
            @PathVariable String id,
            @RequestBody CourseModel course
    ) {
        return courseService.updateCourseWithNewBatch(id, course);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCourse(
            @PathVariable String id,
            @RequestParam(required = false) String batchId
    ) throws StripeException {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(id);
        courseService.deleteCourse(id, batch.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-module/{courseId}")
    public CourseResponseDTO updateModule(
            @PathVariable String courseId,
            @RequestBody ModuleDTO module,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.updateModule(courseId, module, batch);
    }

    @DeleteMapping("/delete-module/{courseId}/{moduleId}")
    public void deleteModule(
            @PathVariable String courseId,
            @PathVariable String moduleId,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        courseService.deleteModule(courseId, moduleId, batch.getId());
    }

    @PostMapping("/add-module/{courseId}")
    public CourseResponseDTO addModule(
            @PathVariable String courseId,
            @RequestBody ModuleDTO module,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.addModule(courseId, module, batch);
    }

    @PutMapping("/update-installment/{courseId}")
    public CourseResponseDTO updateInstallment(
            @PathVariable String courseId,
            @RequestBody InstallmentDTO installment,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.updateInstallment(courseId, installment, batch);
    }

    @DeleteMapping("/delete-installment/{courseId}/{installmentId}")
    public void deleteInstallment(
            @PathVariable String courseId,
            @PathVariable String installmentId,
            @RequestParam(required = false) String batchId
    ) throws StripeException {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        courseService.deleteInstallment(courseId, installmentId, batch.getId());
    }

    @PostMapping("/add-installment/{courseId}")
    public CourseResponseDTO addInstallment(
            @PathVariable String courseId,
            @RequestBody InstallmentDTO installment,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.addInstallment(courseId, installment, batch);
    }

    @GetMapping("/get/{courseId}/users")
    public List<EmployeeModel> getUsersEnrolledInCourse(
            @PathVariable String courseId,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.getUsersEnrolledInCourse(courseId, batch.getId());
    }

    @GetMapping("/get/{courseId}/enrolls")
    public List<EmpCoursesModel> getEnrollesInCourse(
            @PathVariable String courseId,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.getEnrolls(courseId, batch.getId());
    }

    @GetMapping("/get/{courseId}/enrolls/summary")
    public List<EmpCoursesModel> getEnrollesInCourseSummary(
            @PathVariable String courseId,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.getEnrollsSummary(courseId, batch.getId());
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

    @PutMapping("/update-status/{courseId}/{status}")
    public CourseResponseDTO updateStatus(
            @PathVariable String courseId,
            @PathVariable String status,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.updateCourseStatus(courseId, status, batch);
    }

    @PutMapping("/update-publicity/{courseId}")
    public CourseResponseDTO updatePublicity(
            @PathVariable String courseId,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.updatePublicity(courseId, batch);
    }

    @PostMapping("/add/material/{courseId}")
    public CourseResponseDTO addMaterial(
            @PathVariable String courseId,
            @RequestBody MaterialsDTO materials,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.addMaterial(courseId, materials, batch);
    }

    @PutMapping("/update/material/{courseId}/{id}")
    public CourseResponseDTO updateMaterial(
            @PathVariable String courseId,
            @PathVariable String id,
            @RequestBody MaterialsDTO materials,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.updateMaterial(courseId, id, materials, batch);
    }

    @DeleteMapping("/delete/material/{courseId}/{id}")
    public void deleteMaterial(
            @PathVariable String courseId,
            @PathVariable String id,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        courseService.deleteMaterial(courseId, id, batch);
    }

    @GetMapping("/get/materials/{courseId}")
    public List<MaterialsDTO> getMaterials(
            @PathVariable String courseId,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.getMaterials(courseId, batch);
    }

    @GetMapping("/get/material/{courseId}/{id}")
    public MaterialsDTO getMaterial(
            @PathVariable String courseId,
            @PathVariable String id,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.getMaterial(courseId, id, batch);
    }

    @PutMapping("/increment/material-view/{courseId}/{id}")
    public CourseResponseDTO incrementMaterialView(
            @PathVariable String courseId,
            @PathVariable String id,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.incrementMaterialView(courseId, id, batch);
    }

    @PutMapping("/visibility/material/{courseId}/{id}/{status}")
    public CourseResponseDTO updateMaterialVisibility(
            @PathVariable String courseId,
            @PathVariable String id,
            @PathVariable String status,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.updateMaterialVisibility(courseId, id, status, batch);
    }

    @PostMapping("/add/quiz/{courseId}")
    public CourseResponseDTO addQuiz(
            @PathVariable String courseId,
            @RequestBody QuizDTO quiz,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.addQuiz(courseId, quiz, batch);
    }

    @PutMapping("/update/quiz/{courseId}/{quizId}")
    public CourseResponseDTO updateQuiz(
            @PathVariable String courseId,
            @PathVariable String quizId,
            @RequestBody QuizDTO quiz,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.updateQuiz(courseId, quizId, quiz, batch);
    }

    @PutMapping("/visibility/quiz/{courseId}/{quizId}/{status}")
    public CourseResponseDTO updateQuizVisibility(
            @PathVariable String courseId,
            @PathVariable String quizId,
            @PathVariable String status,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.updateQuizVisibility(courseId, quizId, status, batch);
    }

    @DeleteMapping("/delete/quiz/{courseId}/{quizId}")
    public void deleteQuiz(
            @PathVariable String courseId,
            @PathVariable String quizId,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        courseService.deleteQuiz(courseId, quizId, batch);
    }

    @GetMapping("/get/quizzes/{courseId}")
    public List<QuizDTO> getQuizzes(
            @PathVariable String courseId,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.getQuizzes(courseId, batch);
    }

    @GetMapping("/get/quiz/{courseId}/{quizId}")
    public QuizDTO getQuiz(
            @PathVariable String courseId,
            @PathVariable String quizId,
            @RequestParam(required = false) String batchId
    ) {
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(courseId);
        return courseService.getQuiz(courseId, quizId, batch);
    }

    @PostMapping("/create/stripe/product/{courseName}")
    public InstallmentDTO createProduct(@RequestBody InstallmentDTO installment, @PathVariable String courseName) throws StripeException {
        Product product = stripeService.createProduct(
                courseName,
                installment.getName() + " for " + courseName + " course."
        );

        String currency;
        switch (installment.getCurrency()) {
            case "€": currency = "eur"; break;
            case "£": currency = "gbp"; break;
            case "Rs": currency = "lkr"; break;
            case "¥": currency = "jpy"; break;
            case "₹": currency = "inr"; break;
            default: currency = "usd";
        }

        long priceInSmallestUnit;
        if ("jpy".equals(currency)) {
            // No decimal support in JPY
            priceInSmallestUnit = Long.parseLong(installment.getPrice());
        } else {
            // Multiply by 100 for other currencies
            double price = Double.parseDouble(installment.getPrice());
            priceInSmallestUnit = Math.round(price * 100);
        }

        Price price = stripeService.createPriceForCourse(product.getId(), priceInSmallestUnit, currency);

        installment.setProductId(product.getId());
        installment.setPriceId(price.getId());
        return installment;
    }

    @PostMapping("/backfill-batches")
    public ResponseEntity<String> backfillBatches() {
        migrationService.backfillMissingBatches();
        return ResponseEntity.ok("Batch migration completed.");
    }
}
