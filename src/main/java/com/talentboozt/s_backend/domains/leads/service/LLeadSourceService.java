package com.talentboozt.s_backend.domains.leads.service;

import com.talentboozt.s_backend.domains.leads.model.LLeadSource;
import com.talentboozt.s_backend.domains.leads.repository.LLeadSourceRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class LLeadSourceService {

    private final LLeadSourceRepository leadSourceRepository;

    public LLeadSourceService(LLeadSourceRepository leadSourceRepository) {
        this.leadSourceRepository = leadSourceRepository;
    }

    public List<LLeadSource> getSourcesByWorkspace(String workspaceId) {
        return leadSourceRepository.findByWorkspaceId(workspaceId);
    }

    public Optional<LLeadSource> getSourceById(String id) {
        return leadSourceRepository.findById(id);
    }

    public LLeadSource createSource(LLeadSource source) {
        source.setCreatedAt(Instant.now());
        source.setUpdatedAt(Instant.now());
        return leadSourceRepository.save(source);
    }

    public LLeadSource updateSource(String id, LLeadSource update) {
        return leadSourceRepository.findById(id)
                .map(existing -> {
                    existing.setName(update.getName());
                    existing.setActive(update.isActive());
                    existing.setConfig(update.getConfig());
                    existing.setUpdatedAt(Instant.now());
                    return leadSourceRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("Source not found"));
    }

    public void deleteSource(String id, String workspaceId) {
        leadSourceRepository.findById(id).ifPresent(source -> {
            if (source.getWorkspaceId().equals(workspaceId)) {
                leadSourceRepository.deleteById(id);
            }
        });
    }
}
