package com.talentboozt.s_backend.domains.com_courses.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.stripe.exception.StripeException;
import com.talentboozt.s_backend.domains.com_courses.dto.*;
import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.com_courses.repository.mongodb.CourseRepository;
import com.talentboozt.s_backend.domains.payment.service.StripeService;
import com.talentboozt.s_backend.domains.plat_courses.dto.CourseEnrollment;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.repository.mongodb.EmpCoursesRepository;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.repository.mongodb.EmployeeRepository;
import com.talentboozt.s_backend.shared.async.EmpCoursesAsyncUpdater;
import com.talentboozt.s_backend.shared.mail.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EmpCoursesRepository empCoursesRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private StripeService stripeService;

    @Mock
    private CourseBatchService courseBatchService;

    @Mock
    private CourseMapperService courseMapper;

    @Mock
    private EmpCoursesAsyncUpdater empCoursesAsyncUpdater;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private CourseService courseService;

    private CourseModel mockCourse;
    private CourseBatchModel mockBatch;
    private CourseResponseDTO mockDTO;
    private ModuleDTO mockModule;
    private InstallmentDTO mockInstallment;
    private MaterialsDTO mockMaterial;
    private QuizDTO mockQuiz;
    private EmployeeModel mockEmployee;
    private EmpCoursesModel mockEmpCourse;

    @BeforeEach
    void setUp() {
        mockCourse = new CourseModel();
        mockCourse.setId("course1");
        mockCourse.setName("Java Bootcamp");
        mockCourse.setCompanyId("company1");
        mockCourse.setCategory("Programming");
        mockCourse.setCourseStatus("ongoing");
        mockCourse.setPublicity(true);
        CourseMissedNotify notify1 = new CourseMissedNotify("email1@gmail.com", "batch1", "2023-08-01");
        CourseMissedNotify notify2 = new CourseMissedNotify("email2@gmail.com", "batch1", "2023-08-01");
        mockCourse.setNotifiers(List.of(notify1, notify2));

        mockBatch = new CourseBatchModel();
        mockBatch.setId("batch1");
        mockBatch.setCourseId("course1");

        mockDTO = new CourseResponseDTO();
        mockDTO.setId("course1");

        mockModule = new ModuleDTO();
        mockModule.setId("module1");

        mockInstallment = new InstallmentDTO();
        mockInstallment.setId("installment1");
        mockInstallment.setPriceId("price_123");

        mockMaterial = new MaterialsDTO();
        mockMaterial.setId("material1");
        mockMaterial.setViewCount(0);

        mockQuiz = new QuizDTO();
        mockQuiz.setId("quiz1");

        mockEmployee = new EmployeeModel();
        mockEmployee.setId("user1");

        mockEmpCourse = new EmpCoursesModel();
        mockEmpCourse.setEmployeeId("user1");

        CourseEnrollment mockEnrollment = new CourseEnrollment();
        mockEnrollment.setCourseId("courseId");
        mockEnrollment.setBatchId("batchId");

        List<CourseEnrollment> mockEnrollments = new ArrayList<>(List.of(mockEnrollment));
        mockEmpCourse.setCourses(mockEnrollments);
    }

    @Test
    void getAllCourses_returnsListOfCourses() {
        when(courseRepository.findAll()).thenReturn(List.of(mockCourse));

        List<CourseModel> result = courseService.getAllCourses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("course1", result.get(0).getId());
        verify(courseRepository).findAll();
    }

    @Test
    void getCoursesByCompanyId_returnsCoursesForCompany() {
        when(courseRepository.findByCompanyId("company1")).thenReturn(List.of(mockCourse));

        List<CourseModel> result = courseService.getCoursesByCompanyId("company1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("course1", result.get(0).getId());
        verify(courseRepository).findByCompanyId("company1");
    }

    @Test
    void getCourseById_returnsCourseWhenFound() {
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        CourseModel result = courseService.getCourseById("course1");

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
    }

    @Test
    void getCourseById_returnsNullWhenNotFound() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseModel result = courseService.getCourseById("course1");

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void createCourse_savesCourseAndBatch() {
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseBatchService.saveBatch(any(CourseBatchModel.class))).thenReturn(mockBatch);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.createCourse(mockCourse);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).save(mockCourse);
        verify(courseBatchService).saveBatch(any(CourseBatchModel.class));
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void updateCourse_withValidBatchId_updatesCourseAndBatch() {
        when(courseRepository.existsById("course1")).thenReturn(true);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseBatchService.getById("batch1")).thenReturn(mockBatch);
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateCourse("course1", mockCourse, "batch1");

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).existsById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseBatchService).getById("batch1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(empCoursesAsyncUpdater).updateEnrolledUsersOnCourseChange("course1", "batch1", mockCourse, mockBatch);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void updateCourse_withoutBatchId_updatesCourseOnly() {
        when(courseRepository.existsById("course1")).thenReturn(true);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateCourse("course1", mockCourse, null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).existsById("course1");
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).updateEnrolledUsersOnCourseChange("course1", null, mockCourse, null);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void updateCourse_courseNotFound_throwsException() {
        when(courseRepository.existsById("course1")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.updateCourse("course1", mockCourse, "batch1");
        });

        assertEquals("Course not found with id: course1", exception.getMessage());
        verify(courseRepository).existsById("course1");
    }

    @Test
    void updateCourseWithNewBatch_createsNewBatch() throws IOException {
        when(courseRepository.existsById("course1")).thenReturn(true);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseBatchService.saveBatch(any(CourseBatchModel.class))).thenReturn(mockBatch);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        doNothing().when(emailService).sendCourseBatchStartEmail(anyString(), anyString(), anyMap());

        CourseResponseDTO result = courseService.updateCourseWithNewBatch("course1", mockCourse);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).existsById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseBatchService).saveBatch(any(CourseBatchModel.class));
        verify(empCoursesAsyncUpdater).updateEnrolledUsersOnCourseChange("course1", "batch1", mockCourse, mockBatch);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void updateCourseWithNewBatch_courseNotFound_throwsException() {
        when(courseRepository.existsById("course1")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseService.updateCourseWithNewBatch("course1", mockCourse);
        });

        assertEquals("Course not found with id: course1", exception.getMessage());
        verify(courseRepository).existsById("course1");
    }

    @Test
    void deleteCourse_withBatchId_deletesCourseAndBatch() throws StripeException {
        mockCourse.setInstallment(List.of(mockInstallment));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.getById("batch1")).thenReturn(mockBatch);

        courseService.deleteCourse("course1", "batch1");

        verify(courseBatchService).getById("batch1");
        verify(courseBatchService).deleteBatch("batch1");
        verify(stripeService).archivePrice("price_123");
        verify(empCoursesAsyncUpdater).deleteCourseFromEmpCourses("course1");
        verify(courseRepository).deleteById("course1");
    }

    @Test
    void deleteCourse_withoutBatchId_deletesCourseOnly() throws StripeException {
        mockCourse.setInstallment(List.of(mockInstallment));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        courseService.deleteCourse("course1", null);

        verify(stripeService).archivePrice("price_123");
        verify(empCoursesAsyncUpdater).deleteCourseFromEmpCourses("course1");
        verify(courseRepository).deleteById("course1");
    }

    @Test
    void updateModule_withBatch_updatesCourseAndBatch() {
        mockCourse.setModules(new ArrayList<>(List.of(mockModule)));
        mockBatch.setModules(new ArrayList<>(List.of(mockModule)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateModule("course1", mockModule, mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).updateSingleModule("course1", "batch1", mockModule);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void updateModule_withoutBatch_updatesCourseOnly() {
        mockCourse.setModules(new ArrayList<>(List.of(mockModule)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateModule("course1", mockModule, null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).updateSingleModule("course1", null, mockModule);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void updateModule_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.updateModule("course1", mockModule, mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void deleteModule_withBatchId_deletesFromCourseAndBatch() {
        mockCourse.setModules(new ArrayList<>(List.of(mockModule)));
        mockBatch.setModules(new ArrayList<>(List.of(mockModule)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.getById("batch1")).thenReturn(mockBatch);
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);

        courseService.deleteModule("course1", "module1", "batch1");

        verify(courseRepository).findById("course1");
        verify(courseBatchService).getById("batch1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).deleteSingleModule("course1", "batch1", "module1");
    }

    @Test
    void deleteModule_withoutBatchId_deletesFromCourseOnly() {
        mockCourse.setModules(new ArrayList<>(List.of(mockModule)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);

        courseService.deleteModule("course1", "module1", null);

        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).deleteSingleModule("course1", null, "module1");
    }

    @Test
    void addModule_withBatch_addsToCourseAndBatch() {
        mockCourse.setModules(new ArrayList<>());
        mockBatch.setModules(new ArrayList<>());
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.addModule("course1", mockModule, mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).addSingleModule("course1", "batch1", mockModule);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void addModule_withoutBatch_addsToCourseOnly() {
        mockCourse.setModules(new ArrayList<>());
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.addModule("course1", mockModule, null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).addSingleModule("course1", null, mockModule);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void addModule_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.addModule("course1", mockModule, mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void updateInstallment_withBatch_updatesCourseAndBatch() throws StripeException {
        mockCourse.setInstallment(new ArrayList<>(List.of(mockInstallment)));
        mockBatch.setInstallment(new ArrayList<>(List.of(mockInstallment)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateInstallment("course1", mockInstallment, mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).updateSingleInstallment("course1", "batch1", mockInstallment);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void updateInstallment_withoutBatch_updatesCourseOnly() throws StripeException {
        mockCourse.setInstallment(new ArrayList<>(List.of(mockInstallment)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateInstallment("course1", mockInstallment, null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).updateSingleInstallment("course1", null, mockInstallment);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void updateInstallment_courseNotFound_returnsNull() throws StripeException {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.updateInstallment("course1", mockInstallment, mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void deleteInstallment_withBatchId_deletesFromCourseAndBatch() throws StripeException {
        mockCourse.setInstallment(new ArrayList<>(List.of(mockInstallment)));
        mockBatch.setInstallment(new ArrayList<>(List.of(mockInstallment)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.getById("batch1")).thenReturn(mockBatch);
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);

        courseService.deleteInstallment("course1", "installment1", "batch1");

        verify(courseRepository).findById("course1");
        verify(courseBatchService).getById("batch1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(stripeService, times(2)).archivePrice("price_123");
        verify(empCoursesAsyncUpdater).deleteSingleInstallment("course1", "batch1", "installment1");
    }

    @Test
    void deleteInstallment_withoutBatchId_deletesFromCourseOnly() throws StripeException {
        mockCourse.setInstallment(new ArrayList<>(List.of(mockInstallment)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);

        courseService.deleteInstallment("course1", "installment1", null);

        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(stripeService).archivePrice("price_123");
        verify(empCoursesAsyncUpdater).deleteSingleInstallment("course1", null, "installment1");
    }

    @Test
    void addInstallment_withBatch_addsToCourseAndBatch() {
        mockCourse.setInstallment(new ArrayList<>());
        mockBatch.setInstallment(new ArrayList<>());
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.addInstallment("course1", mockInstallment, mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).addSingleInstallment("course1", "batch1", mockInstallment);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void addInstallment_withoutBatch_addsToCourseOnly() {
        mockCourse.setInstallment(new ArrayList<>());
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.addInstallment("course1", mockInstallment, null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(empCoursesAsyncUpdater).addSingleInstallment("course1", null, mockInstallment);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void addInstallment_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.addInstallment("course1", mockInstallment, mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void getUsersEnrolledInCourse_withBatchId_returnsEnrolledUsers() {
        when(empCoursesRepository.findByCoursesCourseIdAndCoursesBatchId("course1", "batch1")).thenReturn(List.of(mockEmpCourse));
        when(employeeRepository.findAllById(List.of("user1"))).thenReturn(List.of(mockEmployee));

        List<EmployeeModel> result = courseService.getUsersEnrolledInCourse("course1", "batch1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getId());
        verify(empCoursesRepository).findByCoursesCourseIdAndCoursesBatchId("course1", "batch1");
        verify(employeeRepository).findAllById(List.of("user1"));
    }

    @Test
    void getUsersEnrolledInCourse_withoutBatchId_returnsAllEnrolledUsers() {
        when(empCoursesRepository.findByCoursesCourseId("course1")).thenReturn(List.of(mockEmpCourse));
        when(employeeRepository.findAllById(List.of("user1"))).thenReturn(List.of(mockEmployee));

        List<EmployeeModel> result = courseService.getUsersEnrolledInCourse("course1", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getId());
        verify(empCoursesRepository).findByCoursesCourseId("course1");
        verify(employeeRepository).findAllById(List.of("user1"));
    }

    @Test
    void getEnrolls_withBatchId_returnsEnrollments() {
        when(empCoursesRepository.findByCoursesCourseIdAndCoursesBatchId("course1", "batch1")).thenReturn(List.of(mockEmpCourse));

        List<EmpCoursesModel> result = courseService.getEnrolls("course1", "batch1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getEmployeeId());
        verify(empCoursesRepository).findByCoursesCourseIdAndCoursesBatchId("course1", "batch1");
    }

    @Test
    void getEnrolls_withoutBatchId_returnsAllEnrollments() {
        when(empCoursesRepository.findByCoursesCourseId("course1")).thenReturn(List.of(mockEmpCourse));

        List<EmpCoursesModel> result = courseService.getEnrolls("course1", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getEmployeeId());
        verify(empCoursesRepository).findByCoursesCourseId("course1");
    }

    @Test
    void getEnrollsSummary_withBatchId_returnsEnrollments() {
        when(empCoursesRepository.findByCoursesCourseIdAndCoursesBatchId("course1", "batch1")).thenReturn(List.of(mockEmpCourse));

        List<EmpCoursesModel> result = courseService.getEnrollsSummary("course1", "batch1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getEmployeeId());
        verify(empCoursesRepository).findByCoursesCourseIdAndCoursesBatchId("course1", "batch1");
    }

    @Test
    void getEnrollsSummary_withoutBatchId_returnsAllEnrollments() {
        when(empCoursesRepository.findByCoursesCourseId("course1")).thenReturn(List.of(mockEmpCourse));

        List<EmpCoursesModel> result = courseService.getEnrollsSummary("course1", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getEmployeeId());
        verify(empCoursesRepository).findByCoursesCourseId("course1");
    }

    @Test
    void getCategories_returnsDistinctCategories() {
        mockCourse.setCategory("Programming");
        when(courseRepository.findAll()).thenReturn(List.of(mockCourse));

        List<String> result = courseService.getCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Programming", result.get(0));
        verify(courseRepository).findAll();
    }

    @Test
    void getCoursesOverviewByCompanyId_returnsOverviewMap() {
        mockCourse.setModules(new ArrayList<>(List.of(new ModuleDTO("module1"))));
        mockCourse.setCourseStatus("ongoing");
        when(courseRepository.findByCompanyId("company1")).thenReturn(List.of(mockCourse));
        when(empCoursesRepository.findByCoursesCourseId("course1")).thenReturn(List.of(mockEmpCourse));
        when(employeeRepository.findAllById(List.of("user1"))).thenReturn(List.of(mockEmployee));

        Map<String, Integer> result = courseService.getCoursesOverviewByCompanyId("company1");

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(0, result.get("totalTrainingHours"));
        assertEquals(0, result.get("totalCompletedTrainings"));
        assertEquals(1, result.get("totalInProgressTrainings"));
        assertEquals(1, result.get("totalParticipants"));
        verify(courseRepository).findByCompanyId("company1");
        verify(empCoursesRepository).findByCoursesCourseId("course1");
        verify(employeeRepository).findAllById(List.of("user1"));
    }

    @Test
    void getCoursesOverviewByCompanyId_noCourses_returnsEmptyMap() {
        when(courseRepository.findByCompanyId("company1")).thenReturn(Collections.emptyList());

        Map<String, Integer> result = courseService.getCoursesOverviewByCompanyId("company1");

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(0, result.get("totalTrainingHours"));
        assertEquals(0, result.get("totalCompletedTrainings"));
        assertEquals(0, result.get("totalInProgressTrainings"));
        assertEquals(0, result.get("totalParticipants"));
        verify(courseRepository).findByCompanyId("company1");
    }

    @Test
    void updatePublicity_withBatch_updatesCourseAndBatch() {
        mockCourse.setPublicity(false);
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updatePublicity("course1", mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
        assertTrue(mockCourse.isPublicity());
        assertTrue(mockBatch.isPublicity());
    }

    @Test
    void updatePublicity_withoutBatch_updatesCourseOnly() {
        mockCourse.setPublicity(false);
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updatePublicity("course1", new CourseBatchModel());

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, null);
        assertTrue(mockCourse.isPublicity());
    }

    @Test
    void updatePublicity_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.updatePublicity("course1", mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void addMaterial_withBatch_addsToCourseAndBatch() {
        mockCourse.setMaterials(new ArrayList<>());
        mockBatch.setMaterials(new ArrayList<>());
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.addMaterial("course1", mockMaterial, mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void addMaterial_withoutBatch_addsToCourseOnly() {
        mockCourse.setMaterials(new ArrayList<>());
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.addMaterial("course1", mockMaterial, null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void addMaterial_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.addMaterial("course1", mockMaterial, mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void updateMaterial_withBatch_updatesCourseAndBatch() {
        mockCourse.setMaterials(new ArrayList<>(List.of(mockMaterial)));
        mockBatch.setMaterials(new ArrayList<>(List.of(mockMaterial)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateMaterial("course1", "material1", mockMaterial, mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void updateMaterial_withoutBatch_updatesCourseOnly() {
        mockCourse.setMaterials(new ArrayList<>(List.of(mockMaterial)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateMaterial("course1", "material1", mockMaterial, null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void updateMaterial_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.updateMaterial("course1", "material1", mockMaterial, mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void deleteMaterial_withBatch_deletesFromCourseAndBatch() {
        mockCourse.setMaterials(new ArrayList<>(List.of(mockMaterial)));
        mockBatch.setMaterials(new ArrayList<>(List.of(mockMaterial)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);

        courseService.deleteMaterial("course1", "material1", mockBatch);

        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
    }

    @Test
    void deleteMaterial_withoutBatch_deletesFromCourseOnly() {
        mockCourse.setMaterials(new ArrayList<>(List.of(mockMaterial)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);

        courseService.deleteMaterial("course1", "material1", null);

        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
    }

    @Test
    void getMaterials_withBatch_returnsBatchMaterials() {
        mockBatch.setMaterials(List.of(mockMaterial));

        List<MaterialsDTO> result = courseService.getMaterials("course1", mockBatch);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("material1", result.get(0).getId());
    }

    @Test
    void getMaterials_withoutBatch_returnsCourseMaterials() {
        mockCourse.setMaterials(List.of(mockMaterial));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        List<MaterialsDTO> result = courseService.getMaterials("course1", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("material1", result.get(0).getId());
        verify(courseRepository).findById("course1");
    }

    @Test
    void getMaterials_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        List<MaterialsDTO> result = courseService.getMaterials("course1", null);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void getMaterial_withBatch_returnsMaterial() {
        mockMaterial.setCourseId("course1");
        mockMaterial.setId("material1");
        mockBatch.setMaterials(new ArrayList<>(List.of(mockMaterial)));

        MaterialsDTO result = courseService.getMaterial("course1", "material1", mockBatch);

        assertNotNull(result);
        assertEquals("material1", result.getId());
    }

    @Test
    void getMaterial_withoutBatch_returnsCourseMaterial() {
        mockCourse.setMaterials(List.of(mockMaterial));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        MaterialsDTO result = courseService.getMaterial("course1", "material1", null);

        assertNotNull(result);
        assertEquals("material1", result.getId());
        verify(courseRepository).findById("course1");
    }

    @Test
    void getMaterial_notFound_returnsNull() {
        mockCourse.setMaterials(List.of(mockMaterial));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        MaterialsDTO result = courseService.getMaterial("course1", "material2", null);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void incrementMaterialView_withBatch_incrementsViewCount() {
        mockCourse.setMaterials(new ArrayList<>(List.of(mockMaterial)));
        mockBatch.setMaterials(new ArrayList<>(List.of(mockMaterial)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.incrementMaterialView("course1", "material1", mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        assertEquals(2, mockMaterial.getViewCount());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void incrementMaterialView_withoutBatch_incrementsCourseViewCount() {
        mockCourse.setMaterials(List.of(mockMaterial));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.incrementMaterialView("course1", "material1", null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        assertEquals(1, mockMaterial.getViewCount());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void incrementMaterialView_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.incrementMaterialView("course1", "material1", mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void updateMaterialVisibility_withBatch_updatesVisibility() {
        mockMaterial.setVisibility("private");
        mockCourse.setMaterials(List.of(mockMaterial));
        mockBatch.setMaterials(List.of(mockMaterial));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateMaterialVisibility("course1", "material1", "public", mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        assertEquals("public", mockMaterial.getVisibility());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void updateMaterialVisibility_withoutBatch_updatesCourseVisibility() {
        mockMaterial.setVisibility("private");
        mockCourse.setMaterials(List.of(mockMaterial));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateMaterialVisibility("course1", "material1", "public", null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        assertEquals("public", mockMaterial.getVisibility());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void updateMaterialVisibility_materialNotFound_returnsNull() {
        mockCourse.setMaterials(List.of(mockMaterial));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        CourseResponseDTO result = courseService.updateMaterialVisibility("course1", "material2", "public", null);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void updateCourseStatus_withBatch_updatesCourseAndBatch() {
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateCourseStatus("course1", "completed", mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        assertEquals("completed", mockCourse.getCourseStatus());
        assertEquals("completed", mockBatch.getCourseStatus());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void updateCourseStatus_withoutBatch_updatesCourseOnly() {
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateCourseStatus("course1", "completed", new CourseBatchModel());

        assertNotNull(result);
        assertEquals("course1", result.getId());
        assertEquals("completed", mockCourse.getCourseStatus());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void updateCourseStatus_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.updateCourseStatus("course1", "completed", mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void addQuiz_withBatch_addsToCourseAndBatch() {
        mockCourse.setQuizzes(new ArrayList<>());
        mockBatch.setQuizzes(new ArrayList<>());
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.addQuiz("course1", mockQuiz, mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void addQuiz_withoutBatch_addsToCourseOnly() {
        mockCourse.setQuizzes(new ArrayList<>());
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.addQuiz("course1", mockQuiz, null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void addQuiz_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.addQuiz("course1", mockQuiz, mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void updateQuiz_withBatch_updatesCourseAndBatch() {
        mockCourse.setQuizzes(new ArrayList<>(List.of(mockQuiz)));
        mockBatch.setQuizzes(new ArrayList<>(List.of(mockQuiz)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateQuiz("course1", "quiz1", mockQuiz, mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void updateQuiz_withoutBatch_updatesCourseOnly() {
        mockQuiz.setId("quiz1");
        mockCourse.setQuizzes(new ArrayList<>(List.of(mockQuiz)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateQuiz("course1", "quiz1", mockQuiz, null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void updateQuiz_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        CourseResponseDTO result = courseService.updateQuiz("course1", "quiz1", mockQuiz, mockBatch);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void deleteQuiz_withBatch_deletesFromCourseAndBatch() {
        mockCourse.setQuizzes(new ArrayList<>(List.of(mockQuiz)));
        mockBatch.setQuizzes(new ArrayList<>(List.of(mockQuiz)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);

        courseService.deleteQuiz("course1", "quiz1", mockBatch);

        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
    }

    @Test
    void deleteQuiz_withoutBatch_deletesFromCourseOnly() {
        mockCourse.setQuizzes(new ArrayList<>(List.of(mockQuiz)));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);

        courseService.deleteQuiz("course1", "quiz1", null);

        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
    }

    @Test
    void getQuizzes_withNullBatch_fetchesFromRepository() {
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        mockCourse.setQuizzes(new ArrayList<>(List.of(mockQuiz)));

        List<QuizDTO> result = courseService.getQuizzes("course1", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("quiz1", result.get(0).getId());

        verify(courseRepository).findById("course1");
    }

    @Test
    void getQuizzes_withoutBatch_returnsCourseQuizzes() {
        mockCourse.setQuizzes(List.of(mockQuiz));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        List<QuizDTO> result = courseService.getQuizzes("course1", null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("quiz1", result.get(0).getId());
        verify(courseRepository).findById("course1");
    }

    @Test
    void getQuizzes_courseNotFound_returnsNull() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        List<QuizDTO> result = courseService.getQuizzes("course1", null);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void getQuiz_withBatch_returnsQuiz() {
        mockQuiz.setCourseId("course1");
        mockQuiz.setId("quiz1");
        mockBatch.setQuizzes(new ArrayList<>(List.of(mockQuiz)));

        QuizDTO result = courseService.getQuiz("course1", "quiz1", mockBatch);

        assertNotNull(result);
        assertEquals("quiz1", result.getId());
    }

    @Test
    void getQuiz_withoutBatch_fetchesFromRepository() {
        mockQuiz.setCourseId("course1");
        mockQuiz.setId("quiz1");
        mockCourse.setId("course1");
        mockCourse.setQuizzes(new ArrayList<>(List.of(mockQuiz)));

        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        QuizDTO result = courseService.getQuiz("course1", "quiz1", null);

        assertNotNull(result);
        assertEquals("quiz1", result.getId());

        verify(courseRepository).findById("course1");
    }

    @Test
    void getQuiz_withoutBatch_returnsCourseQuiz() {
        mockCourse.setQuizzes(List.of(mockQuiz));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        QuizDTO result = courseService.getQuiz("course1", "quiz1", null);

        assertNotNull(result);
        assertEquals("quiz1", result.getId());
        verify(courseRepository).findById("course1");
    }

    @Test
    void getQuiz_notFound_returnsNull() {
        mockCourse.setQuizzes(List.of(mockQuiz));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        QuizDTO result = courseService.getQuiz("course1", "quiz2", null);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }

    @Test
    void updateQuizVisibility_withBatch_updatesVisibility() {
        mockQuiz.setVisibility("private");
        mockCourse.setQuizzes(List.of(mockQuiz));
        mockBatch.setQuizzes(List.of(mockQuiz));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseBatchService.updateBatch(mockBatch)).thenReturn(mockBatch);
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, mockBatch)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateQuizVisibility("course1", "quiz1", "public", mockBatch);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        assertEquals("public", mockQuiz.getVisibility());
        verify(courseRepository).findById("course1");
        verify(courseBatchService).updateBatch(mockBatch);
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, mockBatch);
    }

    @Test
    void updateQuizVisibility_withoutBatch_updatesCourseVisibility() {
        mockQuiz.setVisibility("private");
        mockCourse.setQuizzes(List.of(mockQuiz));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));
        when(courseRepository.save(mockCourse)).thenReturn(mockCourse);
        when(courseMapper.toResponseDTO(mockCourse, null)).thenReturn(mockDTO);

        CourseResponseDTO result = courseService.updateQuizVisibility("course1", "quiz1", "public", null);

        assertNotNull(result);
        assertEquals("course1", result.getId());
        assertEquals("public", mockQuiz.getVisibility());
        verify(courseRepository).findById("course1");
        verify(courseRepository).save(mockCourse);
        verify(courseMapper).toResponseDTO(mockCourse, null);
    }

    @Test
    void updateQuizVisibility_quizNotFound_returnsNull() {
        mockCourse.setQuizzes(List.of(mockQuiz));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(mockCourse));

        CourseResponseDTO result = courseService.updateQuizVisibility("course1", "quiz2", "public", null);

        assertNull(result);
        verify(courseRepository).findById("course1");
    }
}
