package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.dto.workspace.LearningPathRequest;
import com.talentboozt.s_backend.domains.edu.model.ELearningPaths;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ELearningPathsRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EWorkspacesRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class EduLearningPathService {

    private final ELearningPathsRepository pathRepository;
    private final EWorkspacesRepository workspaceRepository;

    public EduLearningPathService(ELearningPathsRepository pathRepository, EWorkspacesRepository workspaceRepository) {
        this.pathRepository = pathRepository;
        this.workspaceRepository = workspaceRepository;
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
                
        ELearningPaths saved = pathRepository.save(path);
        
        workspaceRepository.findById(workspaceId).ifPresent(ws -> {
            ws.setTotalLearningPaths(ws.getTotalLearningPaths() + 1);
            workspaceRepository.save(ws);
        });

        return saved;
    }

    public List<ELearningPaths> getWorkspacePaths(String workspaceId) {
        return pathRepository.findByWorkspaceId(workspaceId);
    }
}
