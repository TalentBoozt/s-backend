package com.talentboozt.s_backend.Service;

import com.talentboozt.s_backend.DTO.PostedJobsDTO;
import com.talentboozt.s_backend.Model.CmpPostedJobsModel;
import com.talentboozt.s_backend.Model.CompanyModel;
import com.talentboozt.s_backend.Repository.CmpPostedJobsRepository;
import com.talentboozt.s_backend.Repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CmpPostedJobsService {
    @Autowired
    CmpPostedJobsRepository cmpPostedJobsRepository;

    @Autowired
    CompanyRepository companyRepository;

    public List<CmpPostedJobsModel> getCmpPostedJobsByCompanyId(String companyId) {
        return cmpPostedJobsRepository.findByCompanyId(companyId);
    }

    public List<CmpPostedJobsModel> getAllCmpPostedJobs() {
        return cmpPostedJobsRepository.findAll();
    }

    public CmpPostedJobsModel addCmpPostedJobs(CmpPostedJobsModel cmpPostedJobs) {
        List<CmpPostedJobsModel> cmpPostedJobsList = cmpPostedJobsRepository.findByCompanyId(cmpPostedJobs.getCompanyId());

        CmpPostedJobsModel cmpPostedJobsModel;

        if (!cmpPostedJobsList.isEmpty()) {
            // Use the first CmpPostedJobsModel if it exists (assuming only one entry should exist)
            cmpPostedJobsModel = cmpPostedJobsList.get(0);

            List<PostedJobsDTO> postedJobs = cmpPostedJobsModel.getPostedJobs();
            if (postedJobs == null) {
                postedJobs = new ArrayList<>();
            }

            // Append the new postedJobs from the incoming cmpPostedJobs object
            postedJobs.addAll(cmpPostedJobs.getPostedJobs());

            cmpPostedJobsModel.setPostedJobs(postedJobs);
        } else {
            // If no postedJobs exist, create a new CmpPostedJobsModel entry
            cmpPostedJobsModel = cmpPostedJobsRepository.save(cmpPostedJobs);
        }

        // Save the updated CmpPostedJobsModel to the repository
        cmpPostedJobsRepository.save(cmpPostedJobsModel);

        // Now update the CompanyModel to reference the cmpPostedJobsModel ID (if needed)
        Optional<CompanyModel> companyModel = companyRepository.findById(cmpPostedJobs.getCompanyId());
        if (companyModel.isPresent()) {
            CompanyModel existingCompany = companyModel.get();
            existingCompany.setPostedJobs(cmpPostedJobsModel.getId());

            companyRepository.save(existingCompany);
        }

        return cmpPostedJobsModel;
    }

    // Get a single posted job by companyId and jobId
    public PostedJobsDTO getPostedJobByJobId(String companyId, String jobId) {
        List<CmpPostedJobsModel> cmpPostedJobsList = cmpPostedJobsRepository.findByCompanyId(companyId);

        if (!cmpPostedJobsList.isEmpty()) {
            CmpPostedJobsModel cmpPostedJobsModel = cmpPostedJobsList.get(0);

            return cmpPostedJobsModel.getPostedJobs().stream()
                    .filter(job -> job.getId().equals(jobId))
                    .findFirst()
                    .orElse(null);  // Return null if the job is not found
        }
        return null;
    }

    // Update a specific posted job
    public PostedJobsDTO updatePostedJob(String companyId, String jobId, PostedJobsDTO updatedJob) {
        List<CmpPostedJobsModel> cmpPostedJobsList = cmpPostedJobsRepository.findByCompanyId(companyId);

        if (!cmpPostedJobsList.isEmpty()) {
            CmpPostedJobsModel cmpPostedJobsModel = cmpPostedJobsList.get(0);

            List<PostedJobsDTO> postedJobs = cmpPostedJobsModel.getPostedJobs();
            for (int i = 0; i < postedJobs.size(); i++) {
                PostedJobsDTO job = postedJobs.get(i);
                if (job.getId().equals(jobId)) {
                    // Update the job details
                    postedJobs.set(i, updatedJob);
                    cmpPostedJobsModel.setPostedJobs(postedJobs);
                    cmpPostedJobsRepository.save(cmpPostedJobsModel);  // Save changes to the database
                    return updatedJob;
                }
            }
        }
        return null;  // Job not found or company doesn't exist
    }

    // Delete a specific posted job
    public void deletePostedJob(String companyId, String jobId) {
        List<CmpPostedJobsModel> cmpPostedJobsList = cmpPostedJobsRepository.findByCompanyId(companyId);

        if (!cmpPostedJobsList.isEmpty()) {
            CmpPostedJobsModel cmpPostedJobsModel = cmpPostedJobsList.get(0);

            List<PostedJobsDTO> postedJobs = cmpPostedJobsModel.getPostedJobs();
            postedJobs.removeIf(job -> job.getId().equals(jobId));  // Remove the job if it matches jobId

            cmpPostedJobsModel.setPostedJobs(postedJobs);
            cmpPostedJobsRepository.save(cmpPostedJobsModel);  // Save changes to the database
        }
    }

    @Async
    public CompletableFuture<List<CmpPostedJobsModel>> getCmpPostedJobsByCompanyIdAsync(String companyId) {
        List<CmpPostedJobsModel> cmpPostedJobsList = cmpPostedJobsRepository.findByCompanyId(companyId);

        return CompletableFuture.completedFuture(cmpPostedJobsList);
    }

    public void findAndUpdateCompanyLevel(String companyId, String companyLevel) {
        List<CmpPostedJobsModel> company = cmpPostedJobsRepository.findByCompanyId(companyId);

        if (!company.isEmpty()) {
            CmpPostedJobsModel cmpPostedJobsModel = company.get(0);
            cmpPostedJobsModel.setCompanyLevel(companyLevel);
            cmpPostedJobsRepository.save(cmpPostedJobsModel);
        }
    }
}
