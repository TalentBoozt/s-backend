package com.talentboozt.s_backend.domains.jobs.service;

import com.talentboozt.s_backend.domains.jobs.model.JobPostModel;
import com.talentboozt.s_backend.domains.jobs.model.ApplicantModel;
import com.talentboozt.s_backend.domains.jobs.repository.mongodb.JobPostRepository;
import com.talentboozt.s_backend.domains.jobs.repository.mongodb.ApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    // Job CRUD
    public List<JobPostModel> getAllJobs() {
        return jobPostRepository.findAll();
    }

    public List<JobPostModel> getJobsByCompany(String companyId) {
        return jobPostRepository.findByCompanyId(companyId);
    }

    public Optional<JobPostModel> getJobById(String id) {
        return jobPostRepository.findById(id);
    }

    public JobPostModel saveJob(JobPostModel job) {
        if (job.getId() == null) {
            job.setCreatedAt(Instant.now());
            job.setStatus("OPEN");
        }
        job.setUpdatedAt(Instant.now());
        return jobPostRepository.save(job);
    }

    public void deleteJob(String id) {
        jobPostRepository.deleteById(id);
    }

    // Applicant CRUD
    public List<ApplicantModel> getApplicantsByJob(String jobId) {
        return applicantRepository.findByJobId(jobId);
    }

    public ApplicantModel applyForJob(ApplicantModel application) {
        application.setAppliedAt(Instant.now());
        application.setStatus("PENDING");
        ApplicantModel saved = applicantRepository.save(application);

        // Update job stats
        jobPostRepository.findById(application.getJobId()).ifPresent(job -> {
            job.setApplicationsCount(job.getApplicationsCount() + 1);
            jobPostRepository.save(job);
        });

        return saved;
    }

    public ApplicantModel updateApplicationStatus(String applicationId, String status) {
        return applicantRepository.findById(applicationId).map(app -> {
            app.setStatus(status);
            app.setUpdatedAt(Instant.now());
            return applicantRepository.save(app);
        }).orElse(null);
    }
}
