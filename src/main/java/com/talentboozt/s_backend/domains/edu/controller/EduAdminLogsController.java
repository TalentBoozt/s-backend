package com.talentboozt.s_backend.domains.edu.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/edu/admin/logs")
@RequiredArgsConstructor
public class EduAdminLogsController {

    @GetMapping
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<List<String>> getTailLogs(@RequestParam(defaultValue = "100") int lines) {
        // Attempt to find the standard log file location or use system logs
        // This is a simplified "tail" implementation for admin visibility
        List<String> logEntries = new ArrayList<>();
        try {
            // Check common log locations
            Path logPath = Paths.get("logs/log.txt");
            if (!Files.exists(logPath)) {
                logPath = Paths.get("log.txt");
            }

            if (Files.exists(logPath)) {
                try (Stream<String> stream = Files.lines(logPath)) {
                    logEntries = stream.collect(Collectors.toList());
                    int start = Math.max(0, logEntries.size() - lines);
                    logEntries = logEntries.subList(start, logEntries.size());
                }
            } else {
                logEntries.add("WARN: Log file not found at " + logPath.toAbsolutePath());
                logEntries.add("INFO: Current Working Directory: " + System.getProperty("user.dir"));
            }
        } catch (Exception e) {
            logEntries.add("ERROR fetching logs: " + e.getMessage());
        }
        return ResponseEntity.ok(logEntries);
    }
}
