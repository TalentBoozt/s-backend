package com.talentboozt.s_backend.domains.jobs.controller;

import com.talentboozt.s_backend.domains.jobs.model.JobPostModel;
import com.talentboozt.s_backend.domains.jobs.model.ApplicantModel;
import com.talentboozt.s_backend.domains.jobs.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    // Public Browsing
    @GetMapping("/all")
    public List<JobPostModel> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<JobPostModel> getJob(@PathVariable String id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Company Specific
    @GetMapping("/company/{companyId}")
    public List<JobPostModel> getCompanyJobs(@PathVariable String companyId) {
        return jobService.getJobsByCompany(companyId);
    }

    @PostMapping("/add")
    public JobPostModel addJob(@RequestBody JobPostModel job) {
        return jobService.saveJob(job);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable String id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok().build();
    }

    // Applications
    @GetMapping("/{jobId}/applicants")
    public List<ApplicantModel> getApplicants(@PathVariable String jobId) {
        return jobService.getApplicantsByJob(jobId);
    }

    @PostMapping("/apply")
    public ApplicantModel apply(@RequestBody ApplicantModel application) {
        return jobService.applyForJob(application);
    }

    @PutMapping("/applications/{id}/status")
    public ApplicantModel updateStatus(@PathVariable String id, @RequestParam String status) {
        return jobService.updateApplicationStatus(id, status);
    }
}
