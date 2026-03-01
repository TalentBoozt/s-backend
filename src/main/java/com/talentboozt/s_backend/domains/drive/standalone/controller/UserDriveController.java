package com.talentboozt.s_backend.domains.drive.standalone.controller;

import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.drive.standalone.model.StandaloneFileModel;
import com.talentboozt.s_backend.domains.drive.standalone.service.LocalFileStorageService;
import com.talentboozt.s_backend.shared.security.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v2/drive/personal")
public class UserDriveController {

    @Autowired
    private LocalFileStorageService storageService;

    @Autowired
    private JwtService jwtService;

    private String getEmployeeId(HttpServletRequest request) {
        String token = jwtService.extractTokenFromHeaderOrCookie(request);
        if (token == null || !jwtService.validateToken(token))
            return null;
        CredentialsModel userTokenInfo = jwtService.getUserFromToken(token);
        return userTokenInfo.getEmployeeId();
    }

    @GetMapping("/files")
    public ResponseEntity<List<StandaloneFileModel>> listFiles(
            @RequestParam(required = false) String parentId,
            HttpServletRequest request) {
        String employeeId = getEmployeeId(request);
        if (employeeId == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(storageService.listFiles(parentId, employeeId));
    }

    @PostMapping("/upload")
    public ResponseEntity<StandaloneFileModel> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String parentId,
            HttpServletRequest request) throws Exception {
        String employeeId = getEmployeeId(request);
        if (employeeId == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(storageService.uploadFile(file, parentId, employeeId));
    }

    @PostMapping("/folders")
    public ResponseEntity<StandaloneFileModel> createFolder(
            @RequestParam String name,
            @RequestParam(required = false) String parentId,
            HttpServletRequest request) {
        String employeeId = getEmployeeId(request);
        if (employeeId == null)
            return ResponseEntity.status(401).build();

        return ResponseEntity.ok(storageService.createFolder(name, parentId, employeeId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String id,
            HttpServletRequest request) throws Exception {
        String employeeId = getEmployeeId(request);
        if (employeeId == null)
            return ResponseEntity.status(401).build();

        storageService.deleteFile(id, employeeId);
        return ResponseEntity.noContent().build();
    }
}
