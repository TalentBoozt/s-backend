package com.talentboozt.s_backend.domains.drive;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/drive")
@RequiredArgsConstructor
public class DriveController {

    private final GoogleDriveService googleDriveService;

    @GetMapping("/files")
    public ResponseEntity<List<DriveFileDTO>> listFiles(
            @RequestParam(required = false, defaultValue = "root") String parentId) {
        try {
            List<DriveFileDTO> files = googleDriveService.listFiles(parentId);
            return ResponseEntity.ok(files);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/files")
    public ResponseEntity<DriveFileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false, defaultValue = "root") String parentId) {
        try {
            DriveFileDTO uploadedFile = googleDriveService.uploadFile(file, parentId);
            return ResponseEntity.ok(uploadedFile);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/files/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable String id) {
        try {
            googleDriveService.deleteFile(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/files/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) {
        try {
            byte[] data = googleDriveService.downloadFile(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "downloaded_file");
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/folders")
    public ResponseEntity<DriveFileDTO> createFolder(
            @RequestParam("name") String name,
            @RequestParam(required = false, defaultValue = "root") String parentId) {
        try {
            DriveFileDTO folder = googleDriveService.createFolder(name, parentId);
            return ResponseEntity.ok(folder);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
