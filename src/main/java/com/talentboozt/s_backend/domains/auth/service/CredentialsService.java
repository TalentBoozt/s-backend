package com.talentboozt.s_backend.domains.auth.service;

import com.talentboozt.s_backend.domains.com_job_portal.model.CompanyModel;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.auth.model.RoleModel;
import com.talentboozt.s_backend.domains.com_job_portal.repository.CompanyRepository;
import com.talentboozt.s_backend.domains.auth.repository.CredentialsRepository;
import com.talentboozt.s_backend.domains.user.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CredentialsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserPermissionsService userPermissionsService;

    public CredentialsModel addCredentials(CredentialsModel credentials, String platform, String referrer) {
        Optional<CredentialsModel> optionalCredentials = Optional.ofNullable(credentialsRepository.findByEmail(credentials.getEmail()));

        if (optionalCredentials.isPresent()) {
            CredentialsModel existingUser = optionalCredentials.get();

            // Ensure accessedPlatforms is a Set to prevent duplicates
            Set<String> platforms = new HashSet<>(existingUser.getAccessedPlatforms());
            if (platforms.add(platform)) { // Adds only if it's not already present
                existingUser.setAccessedPlatforms(new ArrayList<>(platforms));
                credentialsRepository.save(existingUser);
            }
            return existingUser;
        } else {
            credentials.setRegisteredFrom(platform);
            credentials.setReferrerId(referrer);
            if (credentials.getAccessedPlatforms() == null) credentials.setAccessedPlatforms(new ArrayList<>());
            if (credentials.getRoles() == null) credentials.setRoles(new ArrayList<>());
            if (credentials.getOrganizations() == null) credentials.setOrganizations(new ArrayList<>());

            Set<String> userRoles = new HashSet<>(credentials.getRoles());
            Set<String> userPermissions = new HashSet<>();
            for (String roleName : userRoles) {
                Optional<RoleModel> role = roleService.getRoleByName(roleName);
                if (role.isPresent()){
                    RoleModel optRole = role.get();
                    if (optRole.getPermissions() != null) {
                        userPermissions.addAll(optRole.getPermissions());
                    }
                }
                if (role.isEmpty()) {
                    RoleModel newRole = new RoleModel();
                    newRole.setName(roleName);
                    newRole.setPermissions(new ArrayList<>());
                    newRole.setDescription("Default role for " + roleName);
                    RoleModel savedRole = roleService.addRole(newRole);

                    userRoles.add(savedRole.getName());
                    userPermissions.addAll(savedRole.getPermissions());
                }
            }

            credentials.setAccessedPlatforms(new ArrayList<>(Set.of(platform))); // Ensures uniqueness
            credentials.setRoles(new ArrayList<>(userRoles));
            credentials.setPermissions(new ArrayList<>(userPermissions));

            // Create EmployeeModel and set initial profile completion
            EmployeeModel emp = new EmployeeModel();
            emp.setFirstname(credentials.getFirstname());
            emp.setLastname(credentials.getLastname());
            emp.setEmail(credentials.getEmail());

            Map<String, Boolean> profileCompleted = new HashMap<>();
            profileCompleted.put("name", true);
            profileCompleted.put("email", true);
            profileCompleted.put("resume", false);
            profileCompleted.put("occupation", false);
            profileCompleted.put("profilePic", false);
            profileCompleted.put("coverPic", false);
            profileCompleted.put("intro", false);
            profileCompleted.put("skills", false);
            profileCompleted.put("experiences", false);
            profileCompleted.put("education", false);
            profileCompleted.put("projects", false);
            profileCompleted.put("certificates", false);
            profileCompleted.put("contactInfo", false);
            profileCompleted.put("socialLinks", false);
            emp.setProfileCompleted(profileCompleted);

            // Check if user is an employer or higher level
            if (credentials.getUserLevel().equals("2") || credentials.getUserLevel().equals("3") || credentials.getUserLevel().equals("4") || credentials.getUserLevel().equals("5")) {
                CompanyModel cmp = new CompanyModel();

                Map<String, Boolean> cmpProfileCompleted = new HashMap<>();
                cmpProfileCompleted.put("name", false);
                cmpProfileCompleted.put("email", false);
                cmpProfileCompleted.put("logo", false);
                cmpProfileCompleted.put("coverPic", false);
                cmpProfileCompleted.put("image1", false);
                cmpProfileCompleted.put("image2", false);
                cmpProfileCompleted.put("image3", false);
                cmpProfileCompleted.put("story", false);
                cmpProfileCompleted.put("founderName", false);
                cmpProfileCompleted.put("foundedDate", false);
                cmpProfileCompleted.put("location", false);
                cmpProfileCompleted.put("numberOfEmployees", false);
                cmpProfileCompleted.put("website", false);
                cmpProfileCompleted.put("socialLinks", false);

                cmp.setProfileCompleted(cmpProfileCompleted);
                cmp.setCompanyLevel(credentials.getUserLevel());

                CompanyModel savedCmp = companyRepository.save(cmp);
                emp.setCompanyId(savedCmp.getId());
                credentials.setCompanyId(savedCmp.getId());
                Map<String, String> orgs = new HashMap<>();
                orgs.put(platform, savedCmp.getId());
                credentials.setOrganizations(new ArrayList<>(Set.of(orgs))); // Ensures uniqueness
            }

            EmployeeModel savedEmp = employeeRepository.save(emp);
            credentials.setEmployeeId(savedEmp.getId());

            return credentialsRepository.save(credentials);
        }
    }

    public Iterable<CredentialsModel> getAllCredentials() {
        List<CredentialsModel> credentials = credentialsRepository.findAll();
        for (CredentialsModel credential : credentials) {
            credential.setPassword(null);
            credential.setPermissions(userPermissionsService.resolvePermissions(credential.getRoles()));
        }
        return credentials;
    }

    public boolean isExistsByEmail(String email) {
        return credentialsRepository.existsByEmail(email);
    }

    public Optional<CredentialsModel> getCredentials(String employeeId) {
        Optional<CredentialsModel> credentials = credentialsRepository.findByEmployeeId(employeeId);
        if (credentials.isPresent()) {
            credentials.get().setPassword(null);
            credentials.get().setPermissions(userPermissionsService.resolvePermissions(credentials.get().getRoles()));
            return credentials;
        }
        return Optional.empty();
    }

    public CredentialsModel getCredentialsByEmail(String email) {
        CredentialsModel credentials = credentialsRepository.findByEmail(email);
        if (credentials != null) {
            credentials.setPermissions(userPermissionsService.resolvePermissions(credentials.getRoles()));
        }
        return credentials;
    }

    public Optional<CredentialsModel> getCredentialsByEmployeeId(String employeeId) {
        Optional<CredentialsModel> credentials = credentialsRepository.findByEmployeeId(employeeId);
        if (credentials.isPresent()) {
            credentials.get().setPassword(null);
            credentials.get().setPermissions(userPermissionsService.resolvePermissions(credentials.get().getRoles()));
            return credentials;
        }
        return Optional.empty();
    }

    public CredentialsModel updateCredentials(String employeeId, CredentialsModel credentials) {
        Optional<CredentialsModel> optionalCredentials = credentialsRepository.findById(Objects.requireNonNull(credentials.getId()));
        if (optionalCredentials.isPresent()) {
            CredentialsModel credentials1 = optionalCredentials.get();
            credentials1.setEmployeeId(employeeId);
            credentials1.setFirstname(credentials.getFirstname());
            credentials1.setLastname(credentials.getLastname());
            credentials1.setEmail(credentials.getEmail());
            credentials1.setPassword(credentials.getPassword());
            credentials1.setRole(credentials.getRole());
            return credentialsRepository.save(credentials1);
        }
        return null;
    }

    public CredentialsModel updatePassword(String credentialsId, String password) {
        Optional<CredentialsModel> optionalCredentials = credentialsRepository.findById(Objects.requireNonNull(credentialsId));
        if (optionalCredentials.isPresent()) {
            CredentialsModel credentials1 = optionalCredentials.get();
            credentials1.setPassword(password);
            return credentialsRepository.save(credentials1);
        }
        return null;
    }

    public CredentialsModel deleteCredentials(String employeeId) {
        return credentialsRepository.deleteByEmployeeId(employeeId);
    }

    public void findAndUpdateCompanyLevel(String companyId, String userLevel) {
        Optional<CredentialsModel> optionalCredentials = credentialsRepository.findByCompanyId(companyId);
        if (optionalCredentials.isPresent()) {
            CredentialsModel credentials1 = optionalCredentials.get();
            credentials1.setUserLevel(userLevel);
            credentialsRepository.save(credentials1);
        }
    }

    public long getUserCountByLevel(String userLevel) {
        return credentialsRepository.countByUserLevel(userLevel);
    }
}
