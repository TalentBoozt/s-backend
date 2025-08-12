package com.talentboozt.s_backend.domains.common.controller;

import com.talentboozt.s_backend.domains.com_courses.model.CourseBatchModel;
import com.talentboozt.s_backend.domains.com_courses.service.CourseBatchService;
import com.talentboozt.s_backend.domains.com_job_portal.model.CmpPostedJobsModel;
import com.talentboozt.s_backend.domains.com_job_portal.model.CmpSocialModel;
import com.talentboozt.s_backend.domains.com_job_portal.model.CompanyModel;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.com_courses.service.CourseService;
import com.talentboozt.s_backend.domains.com_job_portal.service.CmpPostedJobsService;
import com.talentboozt.s_backend.domains.com_job_portal.service.CmpSocialService;
import com.talentboozt.s_backend.domains.com_job_portal.service.CompanyService;
import com.talentboozt.s_backend.domains.plat_courses.service.EmpCoursesService;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import com.talentboozt.s_backend.domains.user.model.*;
import com.talentboozt.s_backend.domains.user.service.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

    private final EmployeeService employeeService;
    private final EmpContactService empContactService;
    private final EmpEducationService empEducationService;
    private final EmpSkillsService empSkillsService;
    private final EmpExperiencesService empExperiencesService;
    private final EmpProjectsService empProjectsService;
    private final EmpCertificatesService empCertificatesService;
    private final EmpFollowersService empFollowersService;
    private final EmpFollowingService empFollowingService;
    private final EmpCoursesService empCoursesService;
    private final CredentialsService credentialsService;
    private final CmpPostedJobsService cmpPostedJobsService;
    private final CompanyService companyService;
    private final CmpSocialService cmpSocialService;
    private final CourseService courseService;
    private final JwtService jwtService;
    private final CourseBatchService courseBatchService;

    public BatchController(EmployeeService employeeService, EmpContactService empContactService, EmpEducationService empEducationService,
            EmpSkillsService empSkillsService, EmpExperiencesService empExperiencesService, EmpProjectsService empProjectsService,
            EmpCertificatesService empCertificatesService, EmpFollowersService empFollowersService, EmpFollowingService empFollowingService,
            EmpCoursesService empCoursesService, CredentialsService credentialsService, CmpPostedJobsService cmpPostedJobsService,
            CompanyService companyService, CmpSocialService cmpSocialService, CourseService courseService, JwtService jwtService,
            CourseBatchService courseBatchService) {
        this.employeeService = employeeService;
        this.empContactService = empContactService;
        this.empEducationService = empEducationService;
        this.empSkillsService = empSkillsService;
        this.empExperiencesService = empExperiencesService;
        this.empProjectsService = empProjectsService;
        this.empCertificatesService = empCertificatesService;
        this.empFollowersService = empFollowersService;
        this.empFollowingService = empFollowingService;
        this.empCoursesService = empCoursesService;
        this.credentialsService = credentialsService;
        this.cmpPostedJobsService = cmpPostedJobsService;
        this.companyService = companyService;
        this.cmpSocialService = cmpSocialService;
        this.courseService = courseService;
        this.jwtService = jwtService;
        this.courseBatchService = courseBatchService;
    }

    @GetMapping("/getEmployee/{id}")
    public Map<String, Object> getEmployee(HttpServletRequest request, @PathVariable String id) {
        String extractedToken = jwtService.extractTokenFromHeaderOrCookie(request);
        Map<String, Object> errorResponse = new HashMap<>();
        if (extractedToken == null) {
            errorResponse.put("message", "Private key not found.");
            errorResponse.put("status", 401);
            return errorResponse;
        }
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
    public CompletableFuture<Map<String, Object>> getEmployeeAsync(HttpServletRequest request, @PathVariable String id) throws ExecutionException, InterruptedException {
        CompletableFuture<Map<String, Object>> errorResponse = CompletableFuture.completedFuture(new HashMap<>());
        String extractedToken = jwtService.extractTokenFromHeaderOrCookie(request);
        if (extractedToken == null) {
            errorResponse.get().put("message", "Private key not found.");
            errorResponse.get().put("status", 401);
            return errorResponse;
        }
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

    @GetMapping("/getParticipants/{id}")
    public Map<String, Object> getParticipant(
            @PathVariable String id,
            @RequestParam(required = false) String batchId
    ) {
        Map<String, Object> response = new HashMap<>();
        CourseBatchModel batch = (batchId != null)
                ? courseBatchService.getById(batchId)
                : courseBatchService.getLatestBatchByCourseId(id);
        response.put("user", courseService.getUsersEnrolledInCourse(id, batch.getId()));
        response.put("enrolls", courseService.getEnrolls(id, batch.getId()));
        return response;
    }

}
