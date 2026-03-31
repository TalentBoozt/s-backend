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

        // Size limit (currently 512MB, matching application.properties)
        if (file.getSize() > 512 * 1024 * 1024) {
            return ResponseEntity.badRequest().body("File size exceeds 512MB limit");
        }

        // Expanded File type validation (MP4, PDF, Images, ZIP, DOCX)
        String contentType = file.getContentType();
        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        
        boolean isValidType = (contentType != null && (
            contentType.startsWith("video/") || 
            contentType.startsWith("image/") || 
            contentType.equals("application/pdf") ||
            contentType.equals("application/zip") ||
            contentType.equals("application/x-zip-compressed") ||
            contentType.contains("officedocument") ||
            contentType.contains("msword")
        )) || (
            originalName.endsWith(".mp4") || 
            originalName.endsWith(".pdf") || 
            originalName.endsWith(".zip") || 
            originalName.endsWith(".docx")
        );

        if (!isValidType) {
            return ResponseEntity.badRequest().body("Invalid file type. Supported: Videos, Images, PDFs, ZIP, and Documents.");
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
