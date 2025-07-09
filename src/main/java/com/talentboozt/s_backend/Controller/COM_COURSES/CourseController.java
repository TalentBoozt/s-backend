package com.talentboozt.s_backend.Controller.COM_COURSES;

import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.talentboozt.s_backend.DTO.COM_COURSES.InstallmentDTO;
import com.talentboozt.s_backend.DTO.COM_COURSES.MaterialsDTO;
import com.talentboozt.s_backend.DTO.COM_COURSES.ModuleDTO;
import com.talentboozt.s_backend.DTO.COM_COURSES.QuizDTO;
import com.talentboozt.s_backend.Model.COM_COURSES.CourseModel;
import com.talentboozt.s_backend.Model.EndUser.EmployeeModel;
import com.talentboozt.s_backend.Model.PLAT_COURSES.EmpCoursesModel;
import com.talentboozt.s_backend.Service.COM_COURSES.CourseService;
import com.talentboozt.s_backend.Service.common.payment.StripeService;
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

    @Autowired
    private StripeService stripeService;

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
    public void deleteCourse(@PathVariable String id) throws StripeException {
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
    public void deleteInstallment(@PathVariable String courseId, @PathVariable String installmentId) throws StripeException {
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

    @PutMapping("/update-status/{courseId}/{status}")
    public CourseModel updateStatus(@PathVariable String courseId, @PathVariable String status) {
        return courseService.updateCourseStatus(courseId, status);
    }

    @PutMapping("/update-publicity/{courseId}")
    public CourseModel updatePublicity(@PathVariable String courseId) {
        return courseService.updatePublicity(courseId);
    }

    @PostMapping("/add/material/{courseId}")
    public CourseModel addMaterial(@PathVariable String courseId, @RequestBody MaterialsDTO materials) {
        return courseService.addMaterial(courseId, materials);
    }

    @PutMapping("/update/material/{courseId}/{id}")
    public CourseModel updateMaterial(@PathVariable String courseId, @PathVariable String id, @RequestBody MaterialsDTO materials) {
        return courseService.updateMaterial(courseId, id, materials);
    }

    @DeleteMapping("/delete/material/{courseId}/{id}")
    public void deleteMaterial(@PathVariable String courseId, @PathVariable String id) {
        courseService.deleteMaterial(courseId, id);
    }

    @GetMapping("/get/materials/{courseId}")
    public List<MaterialsDTO> getMaterials(@PathVariable String courseId) {
        return courseService.getMaterials(courseId);
    }

    @GetMapping("/get/material/{courseId}/{id}")
    public MaterialsDTO getMaterial(@PathVariable String courseId, @PathVariable String id) {
        return courseService.getMaterial(courseId, id);
    }

    @PutMapping("/increment/material-view/{courseId}/{id}")
    public CourseModel incrementMaterialView(@PathVariable String courseId, @PathVariable String id) {
        return courseService.incrementMaterialView(courseId, id);
    }

    @PutMapping("/visibility/material/{courseId}/{id}/{status}")
    public CourseModel updateMaterialVisibility(@PathVariable String courseId, @PathVariable String id, @PathVariable String status) {
        return courseService.updateMaterialVisibility(courseId, id, status);
    }

    @PostMapping("/add/quiz/{courseId}")
    public CourseModel addQuiz(@PathVariable String courseId, @RequestBody QuizDTO quiz) {
        return courseService.addQuiz(courseId, quiz);
    }

    @PutMapping("/update/quiz/{courseId}/{quizId}")
    public CourseModel updateQuiz(@PathVariable String courseId, @PathVariable String quizId, @RequestBody QuizDTO quiz) {
        return courseService.updateQuiz(courseId, quizId, quiz);
    }

    @PutMapping("/visibility/quiz/{courseId}/{quizId}/{status}")
    public CourseModel updateQuizVisibility(@PathVariable String courseId, @PathVariable String quizId, @PathVariable String status) {
        return courseService.updateQuizVisibility(courseId, quizId, status);
    }

    @DeleteMapping("/delete/quiz/{courseId}/{quizId}")
    public void deleteQuiz(@PathVariable String courseId, @PathVariable String quizId) {
        courseService.deleteQuiz(courseId, quizId);
    }

    @GetMapping("/get/quizzes/{courseId}")
    public List<QuizDTO> getQuizzes(@PathVariable String courseId) {
        return courseService.getQuizzes(courseId);
    }

    @GetMapping("/get/quiz/{courseId}/{quizId}")
    public QuizDTO getQuiz(@PathVariable String courseId, @PathVariable String quizId) {
        return courseService.getQuiz(courseId, quizId);
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
            case "RS": currency = "lkr"; break;
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
}
