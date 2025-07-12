package com.talentboozt.s_backend.domains.auth.controller;

import com.talentboozt.s_backend.domains.auth.dto.PermissionRequest;
import com.talentboozt.s_backend.domains.auth.model.PermissionModel;
import com.talentboozt.s_backend.domains.auth.service.PermissionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionServiceImpl service;

    @GetMapping("/get")
    public List<PermissionModel> getAll() {
        return service.getAllPermissions();
    }

    @PostMapping("/add")
    public ResponseEntity<PermissionModel> create(@RequestBody PermissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createPermission(request));
    }

    @PutMapping("/update/{id}")
    public PermissionModel update(@PathVariable String id, @RequestBody PermissionRequest request) {
        return service.updatePermission(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}

