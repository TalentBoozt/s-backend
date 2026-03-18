package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.workspace.LearningPathRequest;
import com.talentboozt.s_backend.domains.edu.model.ELearningPaths;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ELearningPathsRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EduLearningPathService {

    private final ELearningPathsRepository pathRepository;

    public EduLearningPathService(ELearningPathsRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    public ELearningPaths createPath(String workspaceId, String creatorId, LearningPathRequest request) {
        ELearningPaths path = ELearningPaths.builder()
                .workspaceId(workspaceId)
                .title(request.getTitle())
                .description(request.getDescription())
                .orderedCourseIds(request.getOrderedCourseIds().toArray(new String[0]))
                .createdBy(creatorId)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
                
        return pathRepository.save(path);
    }

    public List<ELearningPaths> getWorkspacePaths(String workspaceId) {
        return pathRepository.findByWorkspaceId(workspaceId);
    }
}
