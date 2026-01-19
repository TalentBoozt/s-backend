package com.talentboozt.s_backend.domains.plat_job_portal.service;

import com.talentboozt.s_backend.domains.plat_job_portal.dto.JobApplicantDTO;
import com.talentboozt.s_backend.domains.plat_job_portal.dto.JobViewerDTO;
import com.talentboozt.s_backend.domains.plat_job_portal.model.JobApplyModel;
import com.talentboozt.s_backend.domains.plat_job_portal.repository.JobApplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobApplyService {

    @Autowired
    private JobApplyRepository jobApplyRepository;

    public List<JobApplyModel> getAllJobApply() {
        return jobApplyRepository.findAll();
    }

    public Optional<List<JobApplyModel>> getJobApplyByCompanyId(String companyId) {
        return jobApplyRepository.findAllByCompanyId(companyId);
    }

    public Optional<List<JobApplyModel>> getJobApplyByJobId(String jobId) {
        return jobApplyRepository.findAllByJobId(jobId);
    }

    public JobApplyModel addJobApply(JobApplyModel jobApply) {
        return jobApplyRepository.save(Objects.requireNonNull(jobApply));
    }

    public JobApplyModel addJobApplicant(String companyId, String jobId, JobApplicantDTO newApplicant) {
        Optional<List<JobApplyModel>> jobApplyList = jobApplyRepository.findAllByCompanyId(companyId);

        // Check if there is an existing job posting for the company and job
        JobApplyModel jobApplyModel = null;
        if (jobApplyList.isPresent()) {
            for (JobApplyModel applyModel : jobApplyList.get()) {
                if (applyModel.getJobId().equals(jobId)) {
                    jobApplyModel = applyModel;
                    break;
                }
            }
        }

        if (jobApplyModel != null) {
            // If a job posting exists, add the new applicant
            List<JobApplicantDTO> applicants = jobApplyModel.getApplicants();
            if (applicants == null) {
                applicants = new ArrayList<>();
            }
            applicants.add(newApplicant);
            jobApplyModel.setApplicants(applicants);
        } else {
            // If no job posting exists, create a new JobApplyModel and add the applicant
            jobApplyModel = new JobApplyModel();
            jobApplyModel.setCompanyId(companyId);
            jobApplyModel.setJobId(jobId);
            jobApplyModel.setApplicants(Collections.singletonList(newApplicant));
        }

        // Save and return the updated or newly created JobApplyModel
        return jobApplyRepository.save(jobApplyModel);
    }

    public JobApplyModel addJobViewer(String companyId, String jobId, JobViewerDTO newViewer) {
        Optional<List<JobApplyModel>> jobApplyList = jobApplyRepository.findAllByCompanyId(companyId);

        // Check if there is an existing job posting for the company and job
        JobApplyModel jobApplyModel = null;
        if (jobApplyList.isPresent()) {
            for (JobApplyModel applyModel : jobApplyList.get()) {
                if (applyModel.getJobId().equals(jobId)) {
                    jobApplyModel = applyModel;
                    break;
                }
            }
        }

        if (jobApplyModel != null) {
            // If a job posting exists, add the new applicant
            List<JobViewerDTO> viewers = jobApplyModel.getViewers();
            if (viewers == null) {
                viewers = new ArrayList<>();
            }
            viewers.add(newViewer);
            jobApplyModel.setViewers(viewers);
        } else {
            // If no job posting exists, create a new JobApplyModel and add the applicant
            jobApplyModel = new JobApplyModel();
            jobApplyModel.setCompanyId(companyId);
            jobApplyModel.setJobId(jobId);
            jobApplyModel.setViewers(Collections.singletonList(newViewer));
        }

        // Save and return the updated or newly created JobApplyModel
        return jobApplyRepository.save(jobApplyModel);
    }

    public JobApplicantDTO getSingleJobApplyByJobId(String companyId, String applicantId) {
        Optional<List<JobApplyModel>> jobApplyList = jobApplyRepository.findAllByCompanyId(companyId);

        if (jobApplyList.isPresent()) {
            JobApplyModel jobApplyModel = jobApplyList.get().get(0);

            return jobApplyModel.getApplicants().stream()
                    .filter(applicant -> applicant.getId().equals(applicantId))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    public JobViewerDTO getSingleJobViewerByJobId(String companyId, String viewerId) {
        Optional<List<JobApplyModel>> jobApplyList = jobApplyRepository.findAllByCompanyId(companyId);

        if (jobApplyList.isPresent()) {
            JobApplyModel jobApplyModel = jobApplyList.get().get(0);

            return jobApplyModel.getViewers().stream()
                    .filter(viewer -> viewer.getId().equals(viewerId))
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }

    public JobApplicantDTO updateSingleJobApply(String companyId, String applicantId, JobApplicantDTO updatedJobApply) {
        Optional<List<JobApplyModel>> jobApplyList = jobApplyRepository.findAllByCompanyId(companyId);

        if (jobApplyList.isPresent()) {
            JobApplyModel jobApplyModel = jobApplyList.get().get(0);

            List<JobApplicantDTO> applicantList = jobApplyModel.getApplicants();
            for (int i = 0; i < applicantList.size(); i++) {
                JobApplicantDTO applicant = applicantList.get(i);

                if (applicant.getId().equals(applicantId)) {
                    applicantList.set(i, updatedJobApply);
                    return updatedJobApply;
                }
            }
        }
        return null;
    }

    public JobApplicantDTO updateSingleJobApplyByJobId(String jobId, String applicantId, JobApplicantDTO updatedJobApply) {
        Optional<JobApplyModel> jobApplyList = jobApplyRepository.findByJobId(jobId);

        if (jobApplyList.isPresent()) {
            JobApplyModel jobApplyModel = jobApplyList.get();

            List<JobApplicantDTO> applicantList = jobApplyModel.getApplicants();
            for (int i = 0; i < applicantList.size(); i++) {
                JobApplicantDTO applicant = applicantList.get(i);

                if (applicant.getId().equals(applicantId)) {
                    applicantList.set(i, updatedJobApply);

                    jobApplyRepository.save(jobApplyModel);
                    return updatedJobApply;
                }
            }
        }

        return null;
    }

    public void deleteJobApply(String id) {
        jobApplyRepository.deleteById(Objects.requireNonNull(id));
    }

    public void deleteSingleJobApply(String companyId, String applicantId) {
        Optional<List<JobApplyModel>> jobApplyList = jobApplyRepository.findAllByCompanyId(companyId);

        if (jobApplyList.isPresent()) {
            JobApplyModel jobApplyModel = jobApplyList.get().get(0);

            List<JobApplicantDTO> applicantList = jobApplyModel.getApplicants();
            applicantList.removeIf(applicant -> applicant.getId().equals(applicantId));

            jobApplyModel.setApplicants(applicantList);
            jobApplyRepository.save(jobApplyModel);
        }
    }

    public void deleteSingleJobApplyByJobId(String jobId, String applicantId) {
        Optional<JobApplyModel> jobApplyList = jobApplyRepository.findByJobId(jobId);

        if (jobApplyList.isPresent()) {
            JobApplyModel jobApplyModel = jobApplyList.get();

            List<JobApplicantDTO> applicantList = jobApplyModel.getApplicants();
            applicantList.removeIf(applicant -> applicant.getId().equals(applicantId));

            jobApplyModel.setApplicants(applicantList);
            jobApplyRepository.save(jobApplyModel);
        }
    }
}
