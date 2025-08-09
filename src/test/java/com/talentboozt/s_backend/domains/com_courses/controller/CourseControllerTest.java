package com.talentboozt.s_backend.domains.com_courses.controller;

import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.talentboozt.s_backend.domains.com_courses.dto.*;
import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.com_courses.service.CourseBatchMigrationService;
import com.talentboozt.s_backend.domains.com_courses.service.CourseBatchService;
import com.talentboozt.s_backend.domains.com_courses.service.CourseMapperService;
import com.talentboozt.s_backend.domains.com_courses.service.CourseService;
import com.talentboozt.s_backend.domains.payment.service.StripeService;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @Mock
    private StripeService stripeService;

    @Mock
    private CourseBatchService courseBatchService;

    @Mock
    private CourseMapperService courseMapper;

    @Mock
    private CourseBatchMigrationService migrationService;

    @InjectMocks
    private CourseController courseController;

    private CourseModel mockCourse;
    private CourseBatchModel mockBatch;
    private CourseResponseDTO mockDTO;

    @BeforeEach
    void setUp() {
        mockCourse = new CourseModel();
        mockCourse.setId("course1");

        mockBatch = new CourseBatchModel();
        mockBatch.setId("batch1");

        mockDTO = new CourseResponseDTO();
        mockDTO.setId("course1");
    }

    @Test
    void getAllCourses_returnsListOfCourses() {
        when(courseService.getAllCourses()).thenReturn(List.of(mockCourse));
        when(courseBatchService.getLatestBatchByCourseId("course1")).thenReturn(mockBatch);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        List<CourseResponseDTO> result = courseController.getAllCourses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("course1", result.get(0).getId());

        verify(courseService).getAllCourses();
        verify(courseBatchService).getLatestBatchByCourseId("course1");
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void getCourseById_returnsCourseDTO() {
        when(courseService.getCourseById("course1")).thenReturn(mockCourse);
        when(courseBatchService.getLatestBatchByCourseId("course1")).thenReturn(mockBatch);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.getCourseById("course1", null);

        assertNotNull(result);
        assertEquals("course1", result.getId());

        verify(courseService).getCourseById("course1");
        verify(courseBatchService).getLatestBatchByCourseId("course1");
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void getCoursesByCompany_returnsListOfCourses() {
        when(courseService.getCoursesByCompanyId("company1")).thenReturn(List.of(mockCourse));
        when(courseBatchService.getLatestBatchByCourseId("course1")).thenReturn(mockBatch);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        List<CourseResponseDTO> result = courseController.getCoursesByCompanyId("company1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("course1", result.get(0).getId());

        verify(courseService).getCoursesByCompanyId("company1");
        verify(courseBatchService).getLatestBatchByCourseId("course1");
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void addCourse_callsServiceAndReturnsResponse() {
        when(courseService.createCourse(mockCourse)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.addCourse(mockCourse);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseService).createCourse(mockCourse);
    }

    @Test
    void updateCourseById_updatesAndReturnsCourse() {
        String courseId = "course1";
        String batchId = "batch1";

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.updateCourse(courseId, mockCourse, batchId)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updateCourse(courseId, mockCourse, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).updateCourse(courseId, mockCourse, batchId);
    }

    @Test
    void updateCourseById_withoutBatchId_fetchesLatestBatch() {
        String courseId = "course1";

        when(courseBatchService.getLatestBatchByCourseId(courseId)).thenReturn(mockBatch);
        when(courseService.updateCourse(courseId, mockCourse, mockBatch.getId())).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updateCourse(courseId, mockCourse, null);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getLatestBatchByCourseId(courseId);
        verify(courseService).updateCourse(courseId, mockCourse, mockBatch.getId());
    }

    @Test
    void updateCourseById_withNewBatch_fetchesLatestBatch() {
        String courseId = "course1";

        when(courseService.updateCourseWithNewBatch(courseId, mockCourse)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updateCourseWithNewBatch(courseId, mockCourse);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseService).updateCourseWithNewBatch(courseId, mockCourse);
    }

    @Test
    void deleteCourse_deletesCorrectly() throws Exception {
        when(courseBatchService.getLatestBatchByCourseId("course1")).thenReturn(mockBatch);

        courseController.deleteCourse("course1", null);

        verify(courseBatchService).getLatestBatchByCourseId("course1");
        verify(courseService).deleteCourse("course1", "batch1");
    }

    @Test
    void updateModuleByCourseId_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String moduleId = "module1";
        String batchId = "batch1";

        ModuleDTO mockModule = new ModuleDTO();
        mockModule.setId(moduleId);

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.updateModule(courseId, mockModule, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updateModule(courseId, mockModule, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).updateModule(courseId, mockModule, mockBatch);
    }

    @Test
    void deleteModule_deletesCorrectly() {
        when(courseBatchService.getLatestBatchByCourseId("course1")).thenReturn(mockBatch);

        courseController.deleteModule("course1", "module1", null);

        verify(courseBatchService).getLatestBatchByCourseId("course1");
        verify(courseService).deleteModule("course1", "module1", "batch1");
    }

    @Test
    void addModule_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String batchId = "batch1";

        ModuleDTO mockModule = new ModuleDTO();
        mockModule.setId("module1");

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.addModule(courseId, mockModule, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.addModule(courseId, mockModule, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).addModule(courseId, mockModule, mockBatch);
    }

    @Test
    void updateInstallmentsByCourseId_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String batchId = "batch1";

        InstallmentDTO mockInstallment = new InstallmentDTO();
        mockInstallment.setId("installment1");

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.updateInstallment(courseId, mockInstallment, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updateInstallment(courseId, mockInstallment, batchId);
        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).updateInstallment(courseId, mockInstallment, mockBatch);
    }

    @Test
    void deleteInstallment_deletesCorrectly() throws StripeException {
        when(courseBatchService.getLatestBatchByCourseId("course1")).thenReturn(mockBatch);

        courseController.deleteInstallment("course1", "installment1", null);

        verify(courseBatchService).getLatestBatchByCourseId("course1");
        verify(courseService).deleteInstallment("course1", "installment1", "batch1");
    }

    @Test
    void addInstallment_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String batchId = "batch1";

        InstallmentDTO mockInstallment = new InstallmentDTO();
        mockInstallment.setId("installment1");

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.addInstallment(courseId, mockInstallment, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.addInstallment(courseId, mockInstallment, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).addInstallment(courseId, mockInstallment, mockBatch);
    }

    @Test
    void getUsersEnrolledInCourse_returnsListOfUsers() {
        String courseId = "course1";
        String batchId = "batch1";

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        List<EmployeeModel> mockUsers = List.of(
            new EmployeeModel("user1"),
            new EmployeeModel("user2")
        );

        when(courseService.getUsersEnrolledInCourse(courseId, mockBatch.getId())).thenReturn(mockUsers);

        List<EmployeeModel> result = courseController.getUsersEnrolledInCourse(courseId, batchId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getId().equals("user1") || result.get(0).getId().equals("user2"));

        verify(courseBatchService).getById(batchId);
        verify(courseService).getUsersEnrolledInCourse(courseId, mockBatch.getId());
    }

    @Test
    void getEnrollesInCourse_returnsListOfEnrollments() {
        String courseId = "course1";
        String batchId = "batch1";

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        List<EmpCoursesModel> mockUsers = List.of(
            new EmpCoursesModel("user1"),
            new EmpCoursesModel("user2")
        );

        when(courseService.getEnrolls(courseId, mockBatch.getId())).thenReturn(mockUsers);

        List<EmpCoursesModel> result = courseController.getEnrollesInCourse(courseId, batchId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getId().equals("user1") || result.get(0).getId().equals("user2"));

        verify(courseBatchService).getById(batchId);
        verify(courseService).getEnrolls(courseId, mockBatch.getId());
    }

    @Test
    void getCategories_returnsListOfStrings() {
        List<String> mockCategories = List.of("Category1", "Category2");

        when(courseService.getCategories()).thenReturn(mockCategories);

        List<String> result = courseController.getCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Category1"));
        assertTrue(result.contains("Category2"));

        verify(courseService).getCategories();
    }

    @Test
    void getCoursesOverviewByCompanyId_returnsMapOfStringInteger() {
        Map<String, Integer> mockOverview = Map.of(
                "totalCourses", 10,
                "activeCourses", 5
        );

        when(courseService.getCoursesOverviewByCompanyId("company1")).thenReturn(mockOverview);

        ResponseEntity<Map<String, Integer>> response = courseController.getCoursesOverviewByCompanyId("company1");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().containsKey("totalCourses"));
        assertTrue(response.getBody().containsKey("activeCourses"));

        verify(courseService).getCoursesOverviewByCompanyId("company1");
    }

    @Test
    void getCoursesOverviewByCompanyId_whenCompanyHasNoCourses_returnsEmptyMap() {
        when(courseService.getCoursesOverviewByCompanyId("unknownCompany")).thenReturn(Collections.emptyMap());

        ResponseEntity<Map<String, Integer>> response = courseController.getCoursesOverviewByCompanyId("unknownCompany");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(courseService).getCoursesOverviewByCompanyId("unknownCompany");
    }

    @Test
    void getCoursesOverviewByCompanyId_whenServiceThrowsException_propagatesException() {
        when(courseService.getCoursesOverviewByCompanyId("company1"))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> courseController.getCoursesOverviewByCompanyId("company1")
        );

        assertEquals("Database error", exception.getMessage());
        verify(courseService).getCoursesOverviewByCompanyId("company1");
    }

    @Test
    void getCoursesOverviewByCompanyId_whenServiceReturnsNull_returnsEmptyBody() {
        when(courseService.getCoursesOverviewByCompanyId("company1")).thenReturn(null);

        ResponseEntity<Map<String, Integer>> response = courseController.getCoursesOverviewByCompanyId("company1");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        response.getBody();
        assertTrue(response == null || response.getBody() == null || response.getBody().isEmpty());

        verify(courseService).getCoursesOverviewByCompanyId("company1");
    }

    @Test
    void updateStatusByCourseId_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String batchId = "batch1";
        String status = "active";

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.updateCourseStatus(courseId, status, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updateStatus(courseId, status, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).updateCourseStatus(courseId, status, mockBatch);
    }

    @Test
    void updatePublicityByCourseId_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String batchId = "batch1";

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.updatePublicity(courseId, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updatePublicity(courseId, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).updatePublicity(courseId, mockBatch);
    }

    @Test
    void addMaterialByCourseId_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String batchId = "batch1";
        String materialId = "material1";

        MaterialsDTO mockMaterial = new MaterialsDTO();
        mockMaterial.setId(materialId);

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.addMaterial(courseId, mockMaterial, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.addMaterial(courseId, mockMaterial, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).addMaterial(courseId, mockMaterial, mockBatch);
    }

    @Test
    void updateMaterialByCourseIdAndId_updateAndReturnsCourse() {
        String courseId = "course1";
        String materialId = "material1";
        String batchId = "batch1";

        MaterialsDTO mockMaterial = new MaterialsDTO();
        mockMaterial.setId(materialId);

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.updateMaterial(courseId, materialId, mockMaterial, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updateMaterial(courseId, materialId, mockMaterial, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).updateMaterial(courseId, materialId, mockMaterial, mockBatch);
    }

    @Test
    void deleteMaterialByCourseIdAndId_deletesCorrectly() {
        String courseId = "course1";
        String materialId = "material1";
        String batchId = "batch1";

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);

        courseController.deleteMaterial(courseId, materialId, batchId);

        verify(courseBatchService).getById(batchId);
        verify(courseService).deleteMaterial(courseId, materialId, mockBatch);
    }

    @Test
    void getMaterialsByCourseId_returnsListOfMaterials() {
        String courseId = "course1";
        String batchId = "batch1";

        List<MaterialsDTO> mockMaterials = List.of(
            new MaterialsDTO("material1"),
            new MaterialsDTO("material2")
        );

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.getMaterials(courseId, mockBatch)).thenReturn(mockMaterials);
        List<MaterialsDTO> result = courseController.getMaterials(courseId, batchId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getId().equals("material1") || result.get(0).getId().equals("material2"));

        verify(courseBatchService).getById(batchId);
        verify(courseService).getMaterials(courseId, mockBatch);
    }

    @Test
    void getMaterialByCourseIdAndId_returnsMaterial() {
        String courseId = "course1";
        String materialId = "material1";
        String batchId = "batch1";

        MaterialsDTO mockMaterial = new MaterialsDTO();
        mockMaterial.setId(materialId);

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.getMaterial(courseId, materialId, mockBatch)).thenReturn(mockMaterial);

        MaterialsDTO result = courseController.getMaterial(courseId, materialId, batchId);

        assertNotNull(result);
        assertEquals(materialId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).getMaterial(courseId, materialId, mockBatch);
    }

    @Test
    void incrementMaterialViewByCourseIdAndId_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String materialId = "material1";
        String batchId = "batch1";
        MaterialsDTO mockMaterial = new MaterialsDTO();
        mockMaterial.setId(materialId);

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.incrementMaterialView(courseId, materialId, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.incrementMaterialView(courseId, materialId, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).incrementMaterialView(courseId, materialId, mockBatch);
    }

    @Test
    void updateMaterialVisibilityByCourseIdAndId_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String materialId = "material1";
        String batchId = "batch1";
        String visibility = "public";

        MaterialsDTO mockMaterial = new MaterialsDTO();
        mockMaterial.setId(materialId);

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.updateMaterialVisibility(courseId, materialId, visibility, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updateMaterialVisibility(courseId, materialId, visibility, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).updateMaterialVisibility(courseId, materialId, visibility, mockBatch);
    }

    @Test
    void addQuiz_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String batchId = "batch1";
        String quizId = "quiz1";

        QuizDTO mockQuiz = new QuizDTO();
        mockQuiz.setId(quizId);

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.addQuiz(courseId, mockQuiz, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.addQuiz(courseId, mockQuiz, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).addQuiz(courseId, mockQuiz, mockBatch);
    }

    @Test
    void updateQuizByCourseIdAndId_updatesAndReturnsCourse() {
        String courseId = "course1";
        String quizId = "quiz1";
        String batchId = "batch1";

        QuizDTO mockQuiz = new QuizDTO();
        mockQuiz.setId(quizId);

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.updateQuiz(courseId, quizId, mockQuiz, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updateQuiz(courseId, quizId, mockQuiz, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).updateQuiz(courseId, quizId, mockQuiz, mockBatch);
    }

    @Test
    void updateQuizVisibilityByCourseIdAndId_callsServiceAndReturnsResponse() {
        String courseId = "course1";
        String quizId = "quiz1";
        String batchId = "batch1";
        String visibility = "public";

        QuizDTO mockQuiz = new QuizDTO();
        mockQuiz.setId(quizId);

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.updateQuizVisibility(courseId, quizId, visibility, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseController.updateQuizVisibility(courseId, quizId, visibility, batchId);

        assertNotNull(result);
        assertEquals(courseId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).updateQuizVisibility(courseId, quizId, visibility, mockBatch);
    }

    @Test
    void deleteQuizByCourseIdAndId_deletesCorrectly() {
        String courseId = "course1";
        String quizId = "quiz1";
        String batchId = "batch1";

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);

        courseController.deleteQuiz(courseId, quizId, batchId);

        verify(courseBatchService).getById(batchId);
        verify(courseService).deleteQuiz(courseId, quizId, mockBatch);
    }

    @Test
    void getQuizzesByCourseId_returnsListOfQuizzes() {
        String courseId = "course1";
        String batchId = "batch1";

        List<QuizDTO> mockQuizzes = List.of(
            new QuizDTO("quiz1"),
            new QuizDTO("quiz2")
        );

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.getQuizzes(courseId, mockBatch)).thenReturn(mockQuizzes);

        List<QuizDTO> result = courseController.getQuizzes(courseId, batchId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getId().equals("quiz1") || result.get(0).getId().equals("quiz2"));

        verify(courseBatchService).getById(batchId);
        verify(courseService).getQuizzes(courseId, mockBatch);
    }

    @Test
    void getQuizByCourseIdAndId_returnsQuiz() {
        String courseId = "course1";
        String quizId = "quiz1";
        String batchId = "batch1";

        QuizDTO mockQuiz = new QuizDTO();
        mockQuiz.setId(quizId);

        when(courseBatchService.getById(batchId)).thenReturn(mockBatch);
        when(courseService.getQuiz(courseId, quizId, mockBatch)).thenReturn(mockQuiz);

        QuizDTO result = courseController.getQuiz(courseId, quizId, batchId);

        assertNotNull(result);
        assertEquals(quizId, result.getId());

        verify(courseBatchService).getById(batchId);
        verify(courseService).getQuiz(courseId, quizId, mockBatch);
    }

    @Test
    void createProduct_withStandardCurrency_returnsInstallmentDTO() throws Exception {
        // Arrange
        InstallmentDTO installment = createInstallment("€", "49.99"); // EUR
        String courseName = "JavaBootcamp";
        Product mockProduct = mock(Product.class);
        Price mockPrice = mock(Price.class);

        when(mockProduct.getId()).thenReturn("prod_123");
        when(mockPrice.getId()).thenReturn("price_123");

        when(stripeService.createProduct(eq(courseName), anyString())).thenReturn(mockProduct);
        when(stripeService.createPriceForCourse(eq("prod_123"), eq(4999L), eq("eur"))).thenReturn(mockPrice);

        // Act
        InstallmentDTO result = courseController.createProduct(installment, courseName);

        // Assert
        assertNotNull(result);
        assertEquals("prod_123", result.getProductId());
        assertEquals("price_123", result.getPriceId());

        verify(stripeService).createProduct(eq(courseName), contains("Monthly for JavaBootcamp course."));
        verify(stripeService).createPriceForCourse("prod_123", 4999L, "eur");
    }

    @Test
    void createProduct_withJPYCurrency_handlesNoDecimal() throws Exception {
        InstallmentDTO installment = createInstallment("¥", "5000"); // JPY
        String courseName = "DataScience";
        Product mockProduct = mock(Product.class);
        Price mockPrice = mock(Price.class);

        when(mockProduct.getId()).thenReturn("prod_jpy");
        when(mockPrice.getId()).thenReturn("price_jpy");

        when(stripeService.createProduct(anyString(), anyString())).thenReturn(mockProduct);
        when(stripeService.createPriceForCourse(eq("prod_jpy"), eq(5000L), eq("jpy"))).thenReturn(mockPrice);

        InstallmentDTO result = courseController.createProduct(installment, courseName);

        assertEquals("prod_jpy", result.getProductId());
        assertEquals("price_jpy", result.getPriceId());

        verify(stripeService).createPriceForCourse("prod_jpy", 5000L, "jpy");
    }

    @Test
    void createProduct_withUnknownCurrency_defaultsToUSD() throws Exception {
        InstallmentDTO installment = createInstallment("XYZ", "10.50");
        String courseName = "AI101";
        Product mockProduct = mock(Product.class);
        Price mockPrice = mock(Price.class);

        when(mockProduct.getId()).thenReturn("prod_usd");
        when(mockPrice.getId()).thenReturn("price_usd");

        when(stripeService.createProduct(anyString(), anyString())).thenReturn(mockProduct);
        when(stripeService.createPriceForCourse(eq("prod_usd"), eq(1050L), eq("usd"))).thenReturn(mockPrice);

        InstallmentDTO result = courseController.createProduct(installment, courseName);

        assertEquals("prod_usd", result.getProductId());
        assertEquals("price_usd", result.getPriceId());

        verify(stripeService).createPriceForCourse("prod_usd", 1050L, "usd");
    }

    @Test
    void createProduct_whenStripeServiceFails_throwsException() throws Exception {
        InstallmentDTO installment = createInstallment("€", "100");
        String courseName = "FailureCase";

        when(stripeService.createProduct(any(), any()))
                .thenThrow(new InvalidRequestException("Stripe error", null, "400", null, null, null));

        assertThrows(InvalidRequestException.class, () -> {
            courseController.createProduct(installment, courseName);
        });

        verify(stripeService).createProduct(any(), any());
    }

    private InstallmentDTO createInstallment(String currency, String price) {
        InstallmentDTO dto = new InstallmentDTO();
        dto.setName("Monthly");
        dto.setCurrency(currency);
        dto.setPrice(price);
        return dto;
    }
}
