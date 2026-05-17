package com.talentboozt.s_backend.domains.pipeline.service;

import com.talentboozt.s_backend.domains.pipeline.model.PipelineModel;
import com.talentboozt.s_backend.domains.pipeline.repository.mongodb.PipelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PipelineService {
    private final PipelineRepository pipelineRepository;

    public PipelineModel createDefaultPipeline(String organizationId, String name) {
        List<PipelineModel.PipelineStage> stages = new ArrayList<>();
        stages.add(new PipelineModel.PipelineStage(UUID.randomUUID().toString(), "Applied", 0, false, null));
        stages.add(new PipelineModel.PipelineStage(UUID.randomUUID().toString(), "Screening", 1, false, null));
        stages.add(new PipelineModel.PipelineStage(UUID.randomUUID().toString(), "Interview", 2, false, null));
        stages.add(new PipelineModel.PipelineStage(UUID.randomUUID().toString(), "Offer", 3, false, null));
        stages.add(new PipelineModel.PipelineStage(UUID.randomUUID().toString(), "Hired", 4, false, null));

        PipelineModel pipeline = PipelineModel.builder()
                .name(name)
                .organizationId(organizationId)
                .stages(stages)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        
        return pipelineRepository.save(pipeline);
    }

    public PipelineModel getPipelineByJob(String jobId) {
        return pipelineRepository.findByJobId(jobId).stream().findFirst()
                .orElseGet(() -> {
                    // Fallback to org default or create one
                    return null; 
                });
    }
}
