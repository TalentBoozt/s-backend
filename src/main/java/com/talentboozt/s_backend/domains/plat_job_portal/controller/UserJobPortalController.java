package com.talentboozt.s_backend.domains.plat_job_portal.controller;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.plat_job_portal.model.JobApplyModel;
import com.talentboozt.s_backend.domains.plat_job_portal.service.JobApplyService;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/jobs/user")
public class UserJobPortalController {

    @Autowired
    private JobApplyService jobApplyService;

    @Autowired
    private JwtService jwtService;

    private String getEmployeeId(HttpServletRequest request) {
        String token = jwtService.extractTokenFromHeaderOrCookie(request);
        if (token == null || !jwtService.validateToken(token))
            return null;
        CredentialsModel userTokenInfo = jwtService.getUserFromToken(token);
        return userTokenInfo.getEmployeeId();
    }

    @GetMapping("/applications")
    public ResponseEntity<List<JobApplyModel>> getMyApplications(HttpServletRequest request) {
        String employeeId = getEmployeeId(request);
        if (employeeId == null)
            return ResponseEntity.status(401).build();

        List<JobApplyModel> applications = jobApplyService.getJobApplicationsByEmployeeId(employeeId);
        return ResponseEntity.ok(applications);
    }
}
