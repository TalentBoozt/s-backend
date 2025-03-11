package com.talentboozt.s_backend.Controller.common;

import com.talentboozt.s_backend.DTO.common.LoginMetaDTO;
import com.talentboozt.s_backend.Service.common.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/logins")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
public class LoginController {

    @Autowired
    private LoginService loginService;

    // Record daily login
    @PostMapping("/{userId}/record")
    public ResponseEntity<String> recordLogin(@PathVariable String userId, @RequestBody LoginMetaDTO metaData) {
        loginService.recordLogin(userId, metaData);
        return ResponseEntity.ok("Login recorded successfully!");
    }

    // Fetch login dates for a user and year
    @GetMapping("/{userId}/year/{year}")
    public ResponseEntity<List<String>> getLoginDatesForYear(@PathVariable String userId, @PathVariable int year) {
        List<String> loginDates = loginService.getLoginDatesForYear(userId, year);
        return ResponseEntity.ok(loginDates);
    }
}
