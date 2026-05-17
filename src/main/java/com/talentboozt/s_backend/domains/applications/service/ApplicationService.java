package com.talentboozt.s_backend.domains.applications.service;

import com.talentboozt.s_backend.domains.applications.model.ApplicationModel;
import com.talentboozt.s_backend.domains.applications.repository.mongodb.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationModel apply(ApplicationModel application) {
        application.setAppliedAt(Instant.now());
        application.setUpdatedAt(Instant.now());
        application.setStatus("APPLIED");
        return applicationRepository.save(application);
    }

    public List<ApplicationModel> getByEmployee(String employeeId) {
        return applicationRepository.findByEmployeeId(employeeId);
    }

    public List<ApplicationModel> getByJob(String jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public List<ApplicationModel> getByCompany(String companyId) {
        return applicationRepository.findByCompanyId(companyId);
    }

    public ApplicationModel updateStatus(String id, String status) {
        ApplicationModel application = applicationRepository.findById(id).orElseThrow();
        application.setStatus(status);
        application.setUpdatedAt(Instant.now());
        return applicationRepository.save(application);
    }
}
