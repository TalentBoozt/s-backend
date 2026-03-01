package com.talentboozt.s_backend.domains.plat_courses.controller;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.service.CredentialsService;
import com.talentboozt.s_backend.domains.plat_courses.model.EmpCoursesModel;
import com.talentboozt.s_backend.domains.plat_courses.service.EmpCoursesService;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v2/lms/user")
public class UserLmsController {

    @Autowired
    private EmpCoursesService empCoursesService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CredentialsService credentialsService;

    private String getEmployeeId(HttpServletRequest request) {
        String token = jwtService.extractTokenFromHeaderOrCookie(request);
        if (token == null || !jwtService.validateToken(token))
            return null;
        CredentialsModel userTokenInfo = jwtService.getUserFromToken(token);
        return userTokenInfo.getEmployeeId();
    }

    @GetMapping("/enrollments")
    public ResponseEntity<List<EmpCoursesModel>> getMyEnrollments(HttpServletRequest request) {
        String employeeId = getEmployeeId(request);
        if (employeeId == null)
            return ResponseEntity.status(401).build();

        List<EmpCoursesModel> enrollments = empCoursesService.getEmpCoursesByEmployeeId(employeeId);
        return ResponseEntity.ok(enrollments);
    }
}
