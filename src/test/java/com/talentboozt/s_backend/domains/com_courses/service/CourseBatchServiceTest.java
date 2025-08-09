package com.talentboozt.s_backend.domains.com_courses.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.model.CourseModel;
import com.talentboozt.s_backend.domains.com_courses.repository.CourseBatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CourseBatchServiceTest {

    @Mock
    private CourseBatchRepository courseBatchRepository;

    @InjectMocks
    private CourseBatchService courseBatchService;

    private CourseBatchModel mockBatch;
    private CourseModel mockCourse;

    @BeforeEach
    void setUp() {
        mockBatch = new CourseBatchModel();
        mockBatch.setId("batch1");
        mockBatch.setCourseId("course1");
        mockBatch.setBatchName("Java Batch - August 2025");

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
    }

    @Test
    void getById_returnsBatchWhenFound() {
        when(courseBatchRepository.findById("batch1")).thenReturn(Optional.of(mockBatch));

        CourseBatchModel result = courseBatchService.getById("batch1");

        assertNotNull(result);
        assertEquals("batch1", result.getId());
        assertEquals("course1", result.getCourseId());
        verify(courseBatchRepository).findById("batch1");
    }

    @Test
    void getById_batchNotFound_throwsException() {
        when(courseBatchRepository.findById("batch1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseBatchService.getById("batch1");
        });

        assertEquals("Batch not found with id: batch1", exception.getMessage());
        verify(courseBatchRepository).findById("batch1");
    }

    @Test
    void getLatestBatchByCourseId_returnsLatestBatch() {
        when(courseBatchRepository.findTopByCourseIdOrderByStartDateDescIdDesc("course1"))
                .thenReturn(Optional.of(mockBatch));

        CourseBatchModel result = courseBatchService.getLatestBatchByCourseId("course1");

        assertNotNull(result);
        assertEquals("batch1", result.getId());
        assertEquals("course1", result.getCourseId());
        verify(courseBatchRepository).findTopByCourseIdOrderByStartDateDescIdDesc("course1");
    }

    @Test
    void getLatestBatchByCourseId_noBatchFound_returnsNull() {
        when(courseBatchRepository.findTopByCourseIdOrderByStartDateDescIdDesc("course1"))
                .thenReturn(Optional.empty());

        CourseBatchModel result = courseBatchService.getLatestBatchByCourseId("course1");

        assertNull(result);
        verify(courseBatchRepository).findTopByCourseIdOrderByStartDateDescIdDesc("course1");
    }

    @Test
    void saveBatch_savesAndReturnsBatch() {
        when(courseBatchRepository.save(mockBatch)).thenReturn(mockBatch);

        CourseBatchModel result = courseBatchService.saveBatch(mockBatch);

        assertNotNull(result);
        assertEquals("batch1", result.getId());
        verify(courseBatchRepository).save(mockBatch);
    }

    @Test
    void updateBatch_updatesExistingBatch() {
        when(courseBatchRepository.existsById("batch1")).thenReturn(true);
        when(courseBatchRepository.save(mockBatch)).thenReturn(mockBatch);

        CourseBatchModel result = courseBatchService.updateBatch(mockBatch);

        assertNotNull(result);
        assertEquals("batch1", result.getId());
        verify(courseBatchRepository).existsById("batch1");
        verify(courseBatchRepository).save(mockBatch);
    }

    @Test
    void updateBatch_batchNotFound_throwsException() {
        when(courseBatchRepository.existsById("batch1")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseBatchService.updateBatch(mockBatch);
        });

        assertEquals("Batch not found with id: batch1", exception.getMessage());
        verify(courseBatchRepository).existsById("batch1");
        verify(courseBatchRepository, never()).save(any());
    }

    @Test
    void deleteBatch_deletesExistingBatch() {
        when(courseBatchRepository.existsById("batch1")).thenReturn(true);

        courseBatchService.deleteBatch("batch1");

        verify(courseBatchRepository).existsById("batch1");
        verify(courseBatchRepository).deleteById("batch1");
    }

    @Test
    void deleteBatch_batchNotFound_throwsException() {
        when(courseBatchRepository.existsById("batch1")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            courseBatchService.deleteBatch("batch1");
        });

        assertEquals("Batch not found with id: batch1", exception.getMessage());
        verify(courseBatchRepository).existsById("batch1");
        verify(courseBatchRepository, never()).deleteById(any());
    }
}