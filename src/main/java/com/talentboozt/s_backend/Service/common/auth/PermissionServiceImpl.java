package com.talentboozt.s_backend.Service.common.auth;

import com.talentboozt.s_backend.DTO.common.auth.PermissionRequest;
import com.talentboozt.s_backend.Model.common.auth.PermissionModel;
import com.talentboozt.s_backend.Repository.common.auth.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository repository;

    @Override
    public List<PermissionModel> getAllPermissions() {
        return repository.findAll();
    }

    @Override
    public PermissionModel createPermission(PermissionRequest request) {
        if (repository.existsById(request.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Permission already exists");
        }
        PermissionModel permission = PermissionModel.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        return repository.save(permission);
    }

    @Override
    public PermissionModel updatePermission(String id, PermissionRequest request) {
        PermissionModel existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found"));
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setCategory(request.getCategory());
        existing.setUpdatedAt(Instant.now());
        return repository.save(existing);
    }

    @Override
    public void deletePermission(String id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Permission not found");
        }
        repository.deleteById(id);
    }
}
