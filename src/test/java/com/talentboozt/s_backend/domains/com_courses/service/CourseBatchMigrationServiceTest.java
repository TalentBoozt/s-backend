package com.talentboozt.s_backend.domains.com_courses.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.com_courses.repository.mongodb.CourseBatchRepository;
import com.talentboozt.s_backend.domains.com_courses.repository.mongodb.CourseRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CourseBatchMigrationServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseBatchRepository courseBatchRepository;

    @Mock
    private CourseBatchService courseBatchService;

    @InjectMocks
    private CourseBatchMigrationService courseBatchMigrationService;

    private CourseModel mockCourse;
    private CourseBatchModel mockBatch;

    @BeforeEach
    void setUp() {
        mockCourse = new CourseModel();
        mockCourse.setId("course1");
        mockCourse.setName("Java Bootcamp");
        mockCourse.setCurrency("USD");
        mockCourse.setPrice("100.00");
        mockCourse.setOnetimePayment(true);
        mockCourse.setCourseStatus("ongoing");
        mockCourse.setPublicity(true);
        mockCourse.setModules(new ArrayList<>());
        mockCourse.setInstallment(new ArrayList<>());
        mockCourse.setMaterials(new ArrayList<>());
        mockCourse.setQuizzes(new ArrayList<>());

        mockBatch = new CourseBatchModel();
        mockBatch.setId("batch1");
        mockBatch.setCourseId("course1");
        mockBatch.setBatchName("Default Batch - Migrated");
    }

    @Test
    void backfillMissingBatches_createsBatchForCourseWithoutBatch() {
        when(courseRepository.findAll()).thenReturn(List.of(mockCourse));
        when(courseBatchRepository.existsByCourseId("course1")).thenReturn(false);
        when(courseBatchService.saveBatch(any(CourseBatchModel.class))).thenReturn(mockBatch);

        courseBatchMigrationService.backfillMissingBatches();

        verify(courseRepository).findAll();
        verify(courseBatchRepository).existsByCourseId("course1");
        verify(courseBatchService).saveBatch(any(CourseBatchModel.class));
    }

    @Test
    void backfillMissingBatches_skipsCourseWithExistingBatch() {
        when(courseRepository.findAll()).thenReturn(List.of(mockCourse));
        when(courseBatchRepository.existsByCourseId("course1")).thenReturn(true);

        courseBatchMigrationService.backfillMissingBatches();

        verify(courseRepository).findAll();
        verify(courseBatchRepository).existsByCourseId("course1");
        verify(courseBatchService, never()).saveBatch(any());
    }

    @Test
    void backfillMissingBatches_noCourses_doesNothing() {
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        courseBatchMigrationService.backfillMissingBatches();

        verify(courseRepository).findAll();
        verify(courseBatchRepository, never()).existsByCourseId(any());
        verify(courseBatchService, never()).saveBatch(any());
    }

    @Test
    void backfillMissingBatches_setsCorrectBatchProperties() {
        when(courseRepository.findAll()).thenReturn(List.of(mockCourse));
        when(courseBatchRepository.existsByCourseId("course1")).thenReturn(false);
        when(courseBatchService.saveBatch(any(CourseBatchModel.class))).thenAnswer(invocation -> {
            CourseBatchModel batch = invocation.getArgument(0);
            assertEquals("course1", batch.getCourseId());
            assertEquals("Default Batch - Migrated", batch.getBatchName());
            assertEquals(mockCourse.getCurrency(), batch.getCurrency());
            assertEquals(mockCourse.getPrice(), batch.getPrice());
            assertEquals(mockCourse.isOnetimePayment(), batch.isOnetimePayment());
            assertEquals(mockCourse.getInstallment(), batch.getInstallment());
            assertEquals(mockCourse.getDuration(), batch.getDuration());
            assertEquals(mockCourse.getModules(), batch.getModules());
            assertEquals(mockCourse.getLanguage(), batch.getLanguage());
            assertEquals(mockCourse.getLecturer(), batch.getLecturer());
            assertEquals(mockCourse.getImage(), batch.getImage());
            assertEquals(mockCourse.getPlatform(), batch.getPlatform());
            assertEquals(mockCourse.getLocation(), batch.getLocation());
            assertEquals(mockCourse.getStartDate(), batch.getStartDate());
            assertEquals(mockCourse.getFromTime(), batch.getFromTime());
            assertEquals(mockCourse.getToTime(), batch.getToTime());
            assertEquals(mockCourse.getUtcStart(), batch.getUtcStart());
            assertEquals(mockCourse.getUtcEnd(), batch.getUtcEnd());
            assertEquals(mockCourse.getTrainerTimezone(), batch.getTrainerTimezone());
            assertEquals(mockCourse.getCourseStatus(), batch.getCourseStatus());
            assertEquals(mockCourse.getPaymentMethod(), batch.getPaymentMethod());
            assertEquals(mockCourse.isPublicity(), batch.isPublicity());
            assertEquals(mockCourse.getMaterials(), batch.getMaterials());
            assertEquals(mockCourse.getQuizzes(), batch.getQuizzes());
            assertNotNull(batch.getEnrolledUserIds());
            assertTrue(batch.getEnrolledUserIds().isEmpty());
            return mockBatch;
        });

        courseBatchMigrationService.backfillMissingBatches();

        verify(courseRepository).findAll();
        verify(courseBatchRepository).existsByCourseId("course1");
        verify(courseBatchService).saveBatch(any(CourseBatchModel.class));
    }
}
