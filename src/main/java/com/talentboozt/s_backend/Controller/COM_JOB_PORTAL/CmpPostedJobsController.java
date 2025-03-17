package com.talentboozt.s_backend.Controller.COM_JOB_PORTAL;

import com.talentboozt.s_backend.DTO.COM_JOB_PORTAL.PostedJobsDTO;
import com.talentboozt.s_backend.Model.COM_JOB_PORTAL.CmpPostedJobsModel;
import com.talentboozt.s_backend.Service.COM_JOB_PORTAL.CmpPostedJobsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/cmp_posted_jobs")
public class CmpPostedJobsController {

    @Autowired
    CmpPostedJobsService cmpPostedJobsService;

    @GetMapping("/getByCompanyId/{companyId}")
    public List<CmpPostedJobsModel> getCmpPostedJobsByCompanyId(@PathVariable String companyId) {
        return cmpPostedJobsService.getCmpPostedJobsByCompanyId(companyId);
    }

    @GetMapping("/getAll")
    public List<CmpPostedJobsModel> getAllCmpPostedJobs() {
        return cmpPostedJobsService.getAllCmpPostedJobs();
    }

    @PostMapping("/add")
    public CmpPostedJobsModel addCmpPostedJobs(@RequestBody CmpPostedJobsModel cmpPostedJobs) {
        return cmpPostedJobsService.addCmpPostedJobs(cmpPostedJobs);
    }

    @GetMapping("/getByCompanyId/{companyId}/postedJob/{jobId}")
    public PostedJobsDTO getPostedJobByJobId(
            @PathVariable String companyId, @PathVariable String jobId) {
        return cmpPostedJobsService.getPostedJobByJobId(companyId, jobId);
    }

    @PutMapping("/updateByCompanyId/{companyId}/postedJob/{jobId}")
    public PostedJobsDTO updatePostedJob(
            @PathVariable String companyId, @PathVariable String jobId, @RequestBody PostedJobsDTO updatedJob) {
        return cmpPostedJobsService.updatePostedJob(companyId, jobId, updatedJob);
    }

    @DeleteMapping("/deleteByCompanyId/{companyId}/postedJob/{jobId}")
    public void deletePostedJob(@PathVariable String companyId, @PathVariable String jobId) {
        cmpPostedJobsService.deletePostedJob(companyId, jobId);
    }
}
