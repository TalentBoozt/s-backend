package com.talentboozt.s_backend.domains.recruiter.service;

import com.talentboozt.s_backend.domains.recruiter.model.RecruiterModel;
import com.talentboozt.s_backend.domains.recruiter.repository.mongodb.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruiterService {
    private final RecruiterRepository recruiterRepository;

    public RecruiterModel getOrCreateRecruiter(String userId, String organizationId) {
        return recruiterRepository.findByUserId(userId)
                .orElseGet(() -> {
                    RecruiterModel newRecruiter = RecruiterModel.builder()
                            .userId(userId)
                            .organizationId(organizationId)
                            .settings(new RecruiterModel.RecruiterSettings())
                            .preferences(new RecruiterModel.RecruiterPreferences())
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .build();
                    return recruiterRepository.save(newRecruiter);
                });
    }

    public RecruiterModel updateRecruiter(String id, RecruiterModel updatedData) {
        RecruiterModel existing = recruiterRepository.findById(id).orElseThrow();
        existing.setJobTitle(updatedData.getJobTitle());
        existing.setDepartment(updatedData.getDepartment());
        existing.setSettings(updatedData.getSettings());
        existing.setPreferences(updatedData.getPreferences());
        existing.setUpdatedAt(Instant.now());
        return recruiterRepository.save(existing);
    }

    public List<RecruiterModel> getRecruitersByOrganization(String organizationId) {
        return recruiterRepository.findByOrganizationId(organizationId);
    }
}
