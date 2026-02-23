package com.talentboozt.s_backend.domains.com_courses.service;

import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.repository.mongodb.CourseBatchRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CourseBatchService {
    @Autowired
    private CourseBatchRepository courseBatchRepository;

    public CourseBatchModel getById(String batchId) {
        return courseBatchRepository.findById(Objects.requireNonNull(batchId))
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));
    }

    public CourseBatchModel getLatestBatchByCourseId(String id) {
        return courseBatchRepository.findTopByCourseIdOrderByStartDateDescIdDesc(id)
                .orElseGet(() -> {
                    // System.out.println("No batch found for course ID: "+ id);
                    return null;
                });
    }

    public CourseBatchModel saveBatch(CourseBatchModel courseBatch) {
        return courseBatchRepository.save(Objects.requireNonNull(courseBatch));
    }

    public CourseBatchModel updateBatch(CourseBatchModel courseBatch) {
        if (!courseBatchRepository.existsById(Objects.requireNonNull(courseBatch.getId()))) {
            throw new RuntimeException("Batch not found with id: " + courseBatch.getId());
        }
        return courseBatchRepository.save(courseBatch);
    }

    public void deleteBatch(String batchId) {
        if (!courseBatchRepository.existsById(Objects.requireNonNull(batchId))) {
            throw new RuntimeException("Batch not found with id: " + batchId);
        }
        courseBatchRepository.deleteById(batchId);
    }

    public List<CourseBatchModel> getAllCourseBatches() {
        return courseBatchRepository.findAll();
    }

    public List<CourseBatchModel> getCourseBatchesByCourseId(String courseId) {
        return courseBatchRepository.findByCourseId(courseId);
    }
}
