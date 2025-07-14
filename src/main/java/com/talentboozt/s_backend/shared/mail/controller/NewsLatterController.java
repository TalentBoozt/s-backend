package com.talentboozt.s_backend.shared.mail.controller;

import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import com.talentboozt.s_backend.shared.mail.model.NewsLatterModel;
import com.talentboozt.s_backend.shared.mail.service.EmailService;
import com.talentboozt.s_backend.shared.mail.service.NewsLatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/news-latter")
public class NewsLatterController {

    @Autowired
    private NewsLatterService newsLatterService;

    @Autowired
    private EmailService emailService;

    @PutMapping("/subscribe")
    public ResponseEntity<ApiResponse> subscribeNewsLatter(@RequestBody NewsLatterModel newsLatterModel) {
        try {
            newsLatterService.subscribeNewsLatter(newsLatterModel);
            return ResponseEntity.ok(new ApiResponse("Email sent successfully."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Something went wrong. Please try again."));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse> test() throws IOException {
        ZonedDateTime localTime = ZonedDateTime.ofInstant(
                Instant.parse("2025-07-13T10:00:00Z"),
                ZoneId.of("America/New_York")
        );

        String formattedTime = localTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"));

        String subject = "Reminder: Upcoming Course Module - Test Module";
        Map<String, String> variables = new HashMap<>();
        variables.put("employeeName", "Test Learner");
        variables.put("reminderType", "1h");
        variables.put("courseName", "Test Course");
        variables.put("moduleName", "Test Module (Don't click)");
        variables.put("startTime", formattedTime);
        variables.put("meetingLink", "https://zoom.us/j/123456789");
        variables.put("year", String.valueOf(Year.now().getValue()));

        emailService.sendCourseReminderEmail("dilum@talentboozt.com", subject, variables);
        return ResponseEntity.ok(new ApiResponse("Email sent successfully."));
    }

    @GetMapping("/test2")
    public ResponseEntity<ApiResponse> test2() throws IOException {
        emailService.sendRejectionNotification("kavindu@talentboozt.com", "Test Learner");
        return ResponseEntity.ok(new ApiResponse("Email sent successfully."));
    }
}
