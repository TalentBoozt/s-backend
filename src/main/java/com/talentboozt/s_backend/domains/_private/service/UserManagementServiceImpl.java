package com.talentboozt.s_backend.domains._private.service;

import com.talentboozt.s_backend.domains._private.dto.PagedResponseDTO;
import com.talentboozt.s_backend.domains._private.dto.StatusUpdateDTO;
import com.talentboozt.s_backend.domains._private.dto.UserDetailDTO;
import com.talentboozt.s_backend.domains._private.dto.UserManagementDTO;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.repository.mongodb.CredentialsRepository;
import com.talentboozt.s_backend.domains.auth.service.UserPermissionsService;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.repository.mongodb.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserManagementServiceImpl implements UserManagementService {
    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserPermissionsService userPermissionsService;

    @Override
    public PagedResponseDTO<UserManagementDTO> getUsers(String search, String role,
                                                        String platform, Boolean active,
                                                        int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<CredentialsModel> filtered;

        if (search != null && !search.isBlank()) {
            filtered = credentialsRepository.searchByEmail(search);
        } else {
            filtered = credentialsRepository.findAll();
        }

        // Apply in-memory filters
        List<UserManagementDTO> result = filtered.stream()
                .filter(c -> role.equals("") || (c.getRoles() != null && c.getRoles().contains(role)))
                .filter(c -> platform.equals("") || (c.getAccessedPlatforms() != null && c.getAccessedPlatforms().contains(platform)))
                .filter(c -> active == null || c.isActive() == active)
                .map(this::mapToDTO)
                .toList();

        int start = Math.min((int) pageable.getOffset(), result.size());
        int end = Math.min((start + pageable.getPageSize()), result.size());

        return new PagedResponseDTO<>(new PageImpl<>(Objects.requireNonNull(result.subList(start, end)), pageable, result.size()));
    }

    private UserManagementDTO mapToDTO(CredentialsModel model) {
        UserManagementDTO dto = new UserManagementDTO();
        dto.setUserId(model.getEmployeeId());
        dto.setFirstname(model.getFirstname());
        dto.setLastname(model.getLastname());
        dto.setEmail(model.getEmail());
        dto.setRoles(model.getRoles());
        dto.setActive(model.isActive());
        dto.setDisabled(model.isDisabled());
        dto.setAmbassador(model.isAmbassador());
        dto.setRegisteredFrom(model.getRegisteredFrom());
        dto.setAccessedPlatforms(model.getAccessedPlatforms());
        return dto;
    }

    @Override
    public UserDetailDTO getUserDetails(String userId) {
        CredentialsModel creds = credentialsRepository.findByEmployeeId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<EmployeeModel> employeeOpt = employeeRepository.findByEmail(creds.getEmail());

        UserDetailDTO detail = new UserDetailDTO();
        detail.setUserId(creds.getId());
        detail.setFirstname(creds.getFirstname());
        detail.setLastname(creds.getLastname());
        detail.setEmail(creds.getEmail());
        detail.setRoles(creds.getRoles());
        detail.setPermissions(userPermissionsService.resolvePermissions(creds.getRoles()));
        detail.setActive(creds.isActive());
        detail.setDisabled(creds.isDisabled());
        detail.setAmbassador(creds.isAmbassador());
        detail.setRegisteredFrom(creds.getRegisteredFrom());
        detail.setAccessedPlatforms(creds.getAccessedPlatforms());
        detail.setOrganizations(creds.getOrganizations());
        detail.setUserLevel(creds.getUserLevel());
        detail.setReferrerId(creds.getReferrerId());

        employeeOpt.ifPresent(emp -> {
            detail.setProfileStatus(emp.getProfileStatus());
            detail.setProfileCompleted(String.valueOf(emp.getProfileCompleted()));
            detail.setProfileImage(emp.getImage());
        });

        return detail;
    }

    @Override
    public void updateStatus(String userId, StatusUpdateDTO status) {
        CredentialsModel user = credentialsRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(status.isActive());
        user.setDisabled(status.isDisabled());
        user.setAmbassador(status.isAmbassador());

        credentialsRepository.save(user);
    }

    @Override
    public void updateRoles(String userId, List<String> roles) {
        CredentialsModel user = credentialsRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRoles(roles);
        credentialsRepository.save(user);
    }

    @Override
    public void updatePermissions(String userId, List<String> permissions) {
        CredentialsModel user = credentialsRepository.findById(Objects.requireNonNull(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPermissions(permissions);
        credentialsRepository.save(user);
    }
}
