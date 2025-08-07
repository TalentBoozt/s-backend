package com.talentboozt.s_backend.domains.common.controller;

import com.talentboozt.s_backend.domains.common.dto.LoginMetaDTO;
import com.talentboozt.s_backend.domains.common.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public/logins")
public class LoginController {

    @Autowired
    private LoginService loginService;

    // Record daily login
    @PostMapping("/{userId}/record")
    public ResponseEntity<String> recordLogin(@PathVariable String userId, @RequestBody LoginMetaDTO metaData) {
        loginService.recordLogin(userId, metaData);
        return ResponseEntity.ok("Login recorded successfully!");
    }

    @PostMapping("/{userId}/event")
    public ResponseEntity<String> recordEvent(
            @PathVariable String userId,
            @RequestParam String type, // e.g. "taskCompletions"
            @RequestParam int count
    ) {
        loginService.recordEvent(userId, type, count);
        return ResponseEntity.ok("Event recorded");
    }

    // Fetch login dates for a user and year
    @GetMapping("/{userId}/year/{year}")
    public ResponseEntity<List<String>> getLoginDatesForYear(@PathVariable String userId, @PathVariable int year) {
        List<String> loginDates = loginService.getLoginDatesForYear(userId, year);
        return ResponseEntity.ok(loginDates);
    }

    @GetMapping("/{userId}/yearly-events/{year}")
    public ResponseEntity<Map<String, Map<String, Object>>> getEventsForYear(
            @PathVariable String userId,
            @PathVariable int year
    ) {
        Map<String, Map<String, Object>> result = loginService.getAllEventsByYear(userId, year);
        return ResponseEntity.ok(result);
    }

}
