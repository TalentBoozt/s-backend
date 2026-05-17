package com.talentboozt.s_backend.domains.pipeline.repository.mongodb;

import com.talentboozt.s_backend.domains.pipeline.model.PipelineModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PipelineRepository extends MongoRepository<PipelineModel, String> {
    List<PipelineModel> findByOrganizationId(String organizationId);
    List<PipelineModel> findByJobId(String jobId);
}
