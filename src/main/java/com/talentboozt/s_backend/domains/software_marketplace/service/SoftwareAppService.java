package com.talentboozt.s_backend.domains.software_marketplace.service;

import com.talentboozt.s_backend.domains.software_marketplace.model.SoftwareAppModel;
import com.talentboozt.s_backend.domains.software_marketplace.repository.mongodb.SoftwareAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SoftwareAppService {

    @Autowired
    private SoftwareAppRepository softwareAppRepository;

    public List<SoftwareAppModel> getPurchasedApps(String companyId) {
        List<SoftwareAppModel> companyApps = softwareAppRepository.findByCompanyId(companyId);
        List<SoftwareAppModel> globalApps = softwareAppRepository.findByIsGlobalTrue();

        List<SoftwareAppModel> allApps = new ArrayList<>(globalApps);
        for (SoftwareAppModel app : companyApps) {
            if (allApps.stream().noneMatch(a -> a.getId().equals(app.getId()))) {
                allApps.add(app);
            }
        }
        return allApps;
    }

    public SoftwareAppModel saveApp(SoftwareAppModel app) {
        return softwareAppRepository.save(app);
    }

    public Optional<SoftwareAppModel> getAppById(String id) {
        return softwareAppRepository.findById(id);
    }

    public void deleteApp(String id) {
        softwareAppRepository.deleteById(id);
    }
}
