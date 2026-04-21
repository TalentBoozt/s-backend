package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.ESystemSettings;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ESystemSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EduSettingsService {

    private final ESystemSettingsRepository settingsRepository;

    public List<ESystemSettings> getAllSettings() {
        return settingsRepository.findAll();
    }

    public ESystemSettings getSettingsByCategory(String category) {
        return settingsRepository.findByCategory(category)
                .orElse(ESystemSettings.builder()
                        .category(category)
                        .settings(Map.of())
                        .build());
    }

    public ESystemSettings updateSettings(String category, Map<String, Object> newSettings, String adminId) {
        ESystemSettings settings = settingsRepository.findByCategory(category)
                .orElse(ESystemSettings.builder().category(category).build());
        
        settings.setSettings(newSettings);
        settings.setUpdatedBy(adminId);
        settings.setUpdatedAt(Instant.now());
        
        return settingsRepository.save(settings);
    }
}
