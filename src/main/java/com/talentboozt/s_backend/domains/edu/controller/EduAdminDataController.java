package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.service.EduDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/edu/admin/data")
@RequiredArgsConstructor
public class EduAdminDataController {

    private final EduDataService dataService;

    @GetMapping("/export/{collection}")
    @PreAuthorize("hasAuthority('PLATFORM_ADMIN')")
    public ResponseEntity<byte[]> exportCollection(
            @PathVariable String collection,
            @RequestParam(defaultValue = "json") String format) {
        
        String content;
        String filename = "export_" + collection + "_" + System.currentTimeMillis();
        MediaType mediaType;

        if ("csv".equalsIgnoreCase(format)) {
            content = dataService.exportCollectionToCsv(collection);
            filename += ".csv";
            mediaType = MediaType.parseMediaType("text/csv");
        } else {
            content = dataService.exportCollectionToJson(collection);
            filename += ".json";
            mediaType = MediaType.APPLICATION_JSON;
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(mediaType)
                .body(content.getBytes());
    }
}
