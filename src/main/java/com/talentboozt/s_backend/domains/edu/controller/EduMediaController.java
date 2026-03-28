package com.talentboozt.s_backend.domains.edu.controller;

import com.talentboozt.s_backend.domains.edu.service.R2StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/edu/media")
@RequiredArgsConstructor
public class EduMediaController {

    private final R2StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        // Validation
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        // Size limit (currently 50MB, matching frontendMaxSize)
        if (file.getSize() > 50 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File size exceeds 50MB limit");
        }

        // File type validation (MP4, PDF, Images)
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("video/mp4") && 
            !contentType.equals("application/pdf") && 
            !contentType.startsWith("image/"))) {
            return ResponseEntity.badRequest().body("Invalid file type. Only MP4, PDF, and Images (JPG/PNG/WEBP) are allowed.");
        }

        try {
            String url = storageService.uploadFile(file);
            return ResponseEntity.ok(Map.of(
                "url", url,
                "name", file.getOriginalFilename(),
                "size", file.getSize(),
                "type", contentType
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Upload failed: " + e.getMessage());
        }
    }
}
