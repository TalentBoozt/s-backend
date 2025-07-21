package com.talentboozt.s_backend.domains.com_courses.service;

import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.repository.CourseBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseBatchService {
    @Autowired
    private CourseBatchRepository courseBatchRepository;

    public CourseBatchModel getById(String batchId) {
        return courseBatchRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found with id: " + batchId));
    }

    public CourseBatchModel getLatestBatchByCourseId(String id) {
        return courseBatchRepository.findTopByCourseIdOrderByStartDateDesc(id)
                .orElseGet(() -> {
                    System.out.println("No batch found for course ID: "+ id);
                    return null;
                });
    }

    public CourseBatchModel saveBatch(CourseBatchModel courseBatch) {
        return courseBatchRepository.save(courseBatch);
    }

    public CourseBatchModel updateBatch(CourseBatchModel courseBatch) {
        if (!courseBatchRepository.existsById(courseBatch.getId())) {
            throw new RuntimeException("Batch not found with id: " + courseBatch.getId());
        }
        return courseBatchRepository.save(courseBatch);
    }

    public void deleteBatch(String batchId) {
        if (!courseBatchRepository.existsById(batchId)) {
            throw new RuntimeException("Batch not found with id: " + batchId);
        }
        courseBatchRepository.deleteById(batchId);
    }
}
