package com.talentboozt.s_backend.Controller.common;

import com.talentboozt.s_backend.Model.COM_JOB_PORTAL.CmpPostedJobsModel;
import com.talentboozt.s_backend.Model.COM_JOB_PORTAL.CmpSocialModel;
import com.talentboozt.s_backend.Model.COM_JOB_PORTAL.CompanyModel;
import com.talentboozt.s_backend.Model.EndUser.*;
import com.talentboozt.s_backend.Model.PLAT_COURSES.EmpCoursesModel;
import com.talentboozt.s_backend.Model.common.auth.CredentialsModel;
import com.talentboozt.s_backend.Service.COM_JOB_PORTAL.CmpPostedJobsService;
import com.talentboozt.s_backend.Service.COM_JOB_PORTAL.CmpSocialService;
import com.talentboozt.s_backend.Service.COM_JOB_PORTAL.CompanyService;
import com.talentboozt.s_backend.Service.EndUser.*;
import com.talentboozt.s_backend.Service.PLAT_COURSES.EmpCoursesService;
import com.talentboozt.s_backend.Service.common.CredentialsService;
import com.talentboozt.s_backend.Service.common.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v2/batch")
public class BatchController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmpContactService empContactService;

    @Autowired
    private EmpEducationService empEducationService;

    @Autowired
    private EmpSkillsService empSkillsService;

    @Autowired
    private EmpExperiencesService empExperiencesService;

    @Autowired
    private EmpProjectsService empProjectsService;

    @Autowired
    private EmpCertificatesService empCertificatesService;

    @Autowired
    private EmpFollowersService empFollowersService;

    @Autowired
    private EmpFollowingService empFollowingService;

    @Autowired
    private EmpCoursesService empCoursesService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private CmpPostedJobsService cmpPostedJobsService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CmpSocialService cmpSocialService;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/getEmployee/{id}")
    public Map<String, Object> getEmployee(@RequestHeader("Authorization") String token, @PathVariable String id) {
        Map<String, Object> errorResponse = new HashMap<>();
        if (token == null) {
            errorResponse.put("message", "Private key not found.");
            errorResponse.put("status", 401);
            return errorResponse;
        }
        String extractedToken = token.substring(7);
        if (!jwtService.validateToken(extractedToken)) {
            errorResponse.put("message", "Invalid or expired token.");
            errorResponse.put("status", 401);
            return errorResponse;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("employee", employeeService.getEmployee(id));
        response.put("empContact", empContactService.getEmpContactByEmployeeId(id));
        response.put("empEducation", empEducationService.getEmpEducationByEmployeeId(id));
        response.put("empSkills", empSkillsService.getEmpSkillsByEmployeeId(id));
        response.put("empExperiences", empExperiencesService.getEmpExperiencesByEmployeeId(id));
        response.put("empProjects", empProjectsService.getEmpProjectsByEmployeeId(id));
        response.put("empCertificates", empCertificatesService.getEmpCertificatesByEmployeeId(id));
        response.put("auth", credentialsService.getCredentialsByEmployeeId(id));
        response.put("empFollowers", empFollowersService.getEmpFollowersByEmployeeId(id));
        response.put("empFollowing", empFollowingService.getEmpFollowingByEmployeeId(id));
        response.put("empCourses", empCoursesService.getEmpCoursesByEmployeeId(id));
        return response;
    }

    @GetMapping("/async/getEmployee/{id}")
    public CompletableFuture<Map<String, Object>> getEmployeeAsync(@RequestHeader("Authorization") String token, @PathVariable String id) throws ExecutionException, InterruptedException {
        CompletableFuture<Map<String, Object>> errorResponse = CompletableFuture.completedFuture(new HashMap<>());
        if (token == null) {
            errorResponse.get().put("message", "Private key not found.");
            errorResponse.get().put("status", 401);
            return errorResponse;
        }
        String extractedToken = token.substring(7);
        if (!jwtService.validateToken(extractedToken)) {

            errorResponse.get().put("message", "Invalid or expired token.");
            errorResponse.get().put("status", 401);
            return errorResponse;
        }
        CompletableFuture<EmployeeModel> employeeFuture = employeeService.getEmployeeByIdAsync(id);
        CompletableFuture<List<EmpContactModel>> contactFuture = empContactService.getEmpContactByEmployeeIdAsync(id);
        CompletableFuture<List<EmpEducationModel>> educationFuture = empEducationService.getEmpEducationByEmployeeIdAsync(id);
        CompletableFuture<List<EmpSkillsModel>> skillsFuture = empSkillsService.getEmpSkillsByEmployeeIdAsync(id);
        CompletableFuture<List<EmpExperiencesModel>> experiencesFuture = empExperiencesService.getEmpExperiencesByEmployeeIdAsync(id);
        CompletableFuture<List<EmpProjectsModel>> projectsFuture = empProjectsService.getEmpProjectsByEmployeeIdAsync(id);
        CompletableFuture<List<EmpCertificatesModel>> certificatesFuture = empCertificatesService.getEmpCertificatesByEmployeeIdAsync(id);
        Optional<CredentialsModel> credentialsFuture = credentialsService.getCredentialsByEmployeeId(id);
        CompletableFuture<List<EmpFollowersModel>> followersFuture = empFollowersService.getEmpFollowersByEmployeeIdAsync(id);
        CompletableFuture<List<EmpFollowingModel>> followingFuture = empFollowingService.getEmpFollowingByEmployeeIdAsync(id);
        CompletableFuture<List<EmpCoursesModel>> coursesFuture = empCoursesService.getEmpCoursesByEmployeeIdAsync(id);

        // Wait for all async calls to complete
        return CompletableFuture.allOf(employeeFuture, contactFuture, educationFuture, skillsFuture, experiencesFuture)
                .thenApply(v -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("employee", employeeFuture.join());
                    response.put("empContact", contactFuture.join());
                    response.put("empEducation", educationFuture.join());
                    response.put("empSkills", skillsFuture.join());
                    response.put("empExperiences", experiencesFuture.join());
                    response.put("empProjects", projectsFuture.join());
                    response.put("empCertificates", certificatesFuture.join());
                    response.put("auth", credentialsFuture);
                    response.put("empFollowers", followersFuture.join());
                    response.put("empFollowing", followingFuture.join());
                    response.put("empCourses", coursesFuture.join());
                    return response;
                });
    }

    @GetMapping("/getCompany/{id}")
    public Map<String, Object> getCompany(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("postedJobs", cmpPostedJobsService.getCmpPostedJobsByCompanyId(id));
        response.put("socials", cmpSocialService.getCmpSocialsByCompanyId(id));
        response.put("company", companyService.getCompany(id));
        return response;
    }

    @GetMapping("/async/getCompany/{id}")
    public CompletableFuture<Map<String, Object>> getCompanyAsync(@PathVariable String id) {
        CompletableFuture<List<CmpPostedJobsModel>> cmpPostedJobsFuture = cmpPostedJobsService.getCmpPostedJobsByCompanyIdAsync(id);
        CompletableFuture<CompanyModel> companyFuture = companyService.getCompanyByIdAsync(id);
        CompletableFuture<List<CmpSocialModel>> cmpSocialsFuture = cmpSocialService.getCmpSocialsByCompanyIdAsync(id);
        // Wait for all async calls to complete
        return CompletableFuture.allOf(cmpPostedJobsFuture, companyFuture)
                .thenApply(v -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("postedJobs", cmpPostedJobsFuture.join());
                    response.put("socials", cmpSocialsFuture.join());
                    response.put("company", companyFuture.join());
                    return response;
                });
    }

}
