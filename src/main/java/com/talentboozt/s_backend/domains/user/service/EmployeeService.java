package com.talentboozt.s_backend.domains.user.service;

import com.talentboozt.s_backend.domains.plat_job_portal.dto.FavJobDTO;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.repository.CredentialsRepository;
import com.talentboozt.s_backend.shared.mail.service.EmailService;
import com.talentboozt.s_backend.shared.events.EventPublisher;
import com.talentboozt.s_backend.domains.user.dto.EmpFollowersDTO;
import com.talentboozt.s_backend.domains.user.dto.EmpFollowingDTO;
import com.talentboozt.s_backend.domains.user.model.*;
import com.talentboozt.s_backend.domains.user.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmpEducationRepository empEducationRepository;

    @Autowired
    private EmpContactRepository empContactRepository;

    @Autowired
    private EmpExperiencesRepository empExperiencesRepository;

    @Autowired
    private EmpSkillsRepository empSkillsRepository;

    @Autowired
    private EmpFollowersRepository empFollowersRepository;

    @Autowired
    private EmpFollowingRepository empFollowingRepository;

    @Autowired
    private ProfileUpdateService profileUpdateService;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private EmpFollowingService empFollowingService;

    @Autowired
    private EmpFollowersService empFollowersService;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<EmployeeModel> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<EmployeeModel> getEmployeesPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAllBy(pageable);
    }

    public List<EmployeeModel> searchEmployees(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findByFirstnameContainingIgnoreCaseOrLastnameContainingIgnoreCase(query, query,
                pageable);
    }

    public EmployeeModel getEmployee(String id) {
        return employeeRepository.findById(Objects.requireNonNull(id)).orElse(null);
    }

    public EmployeeModel addEmployee(EmployeeModel employee) {
        return employeeRepository.save(Objects.requireNonNull(employee));
    }

    public EmployeeModel updateEmployee(EmployeeModel employee) {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(employee.getId()));
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setFirstname(employee.getFirstname());
            existingEmployee.setLastname(employee.getLastname());
            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setOccupation(employee.getOccupation());
            existingEmployee.setDob(employee.getDob());
            existingEmployee.setIntro(employee.getIntro());

            // Handle null profileCompleted
            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>(); // Initialize if null
            }

            // Update profileCompleted
            profileCompleted.put("occupation", employee.getOccupation() != null && !employee.getOccupation().isEmpty());
            profileCompleted.put("intro", employee.getIntro() != null && !employee.getIntro().isEmpty());
            existingEmployee.setProfileCompleted(profileCompleted);

            EmployeeModel updatedEmployee = employeeRepository.save(existingEmployee);

            // comment this and uncomment event publisher to enable microserver
            profileUpdateService.bulkUpdateFollowingsAndFollowers(updatedEmployee.getId(),
                    updatedEmployee.getFirstname() + " " + updatedEmployee.getLastname(),
                    updatedEmployee.getOccupation(), updatedEmployee.getImage());

            // UserProfileUpdatedEvent event = new UserProfileUpdatedEvent(
            // existingEmployee.getId(),
            // existingEmployee.getFirstname() + " " + existingEmployee.getLastname(),
            // existingEmployee.getOccupation(),
            // existingEmployee.getImage()
            // );
            // eventPublisher.publish(event);
            return updatedEmployee;
        }
        return employee;
    }

    public EmployeeModel updateSearchAppearance(EmployeeModel employee) {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(employee.getId()));
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setExpectedSalaryRange(employee.getExpectedSalaryRange());
            existingEmployee.setCurrentExperience(employee.getCurrentExperience());
            existingEmployee.setKeywords(employee.getKeywords());
            employeeRepository.save(existingEmployee);
        }
        return employee;
    }

    public EmployeeModel updateNotifications(EmployeeModel employee) {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(employee.getId()));
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            if (existingEmployee.getAccountNotifications() == null) {
                existingEmployee.setAccountNotifications(new HashMap<>());
            }
            if (existingEmployee.getMarketingNotifications() == null) {
                existingEmployee.setMarketingNotifications(new HashMap<>());
            }
            existingEmployee.setAccountNotifications(employee.getAccountNotifications());
            existingEmployee.setMarketingNotifications(employee.getMarketingNotifications());
            employeeRepository.save(existingEmployee);
        }
        return employee;
    }

    public EmployeeModel updateProfilePic(EmployeeModel employee) {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(employee.getId()));
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setImage(employee.getImage());

            // Handle null profileCompleted
            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>(); // Initialize if null
            }

            // Update profilePic in profileCompleted
            profileCompleted.put("profilePic", employee.getImage() != null && !employee.getImage().isEmpty());
            existingEmployee.setProfileCompleted(profileCompleted);

            employeeRepository.save(existingEmployee);
        }
        return employee;
    }

    public EmployeeModel updateCoverPic(EmployeeModel employee) {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(employee.getId()));
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setCoverImage(employee.getCoverImage());

            // Handle null profileCompleted
            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>(); // Initialize if null
            }

            // Update coverPic in profileCompleted
            profileCompleted.put("coverPic", employee.getCoverImage() != null && !employee.getCoverImage().isEmpty());
            existingEmployee.setProfileCompleted(profileCompleted);

            employeeRepository.save(existingEmployee);
        }
        return employee;
    }

    public EmployeeModel updateResume(EmployeeModel employee) {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(employee.getId()));
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setResume(employee.getResume());

            // Handle null profileCompleted
            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>(); // Initialize if null
            }

            // Update resume in profileCompleted
            profileCompleted.put("resume", employee.getResume() != null && !employee.getResume().isEmpty());
            existingEmployee.setProfileCompleted(profileCompleted);

            employeeRepository.save(existingEmployee);
        }
        return employee;
    }

    public EmployeeModel saveFavoriteJob(String empId, FavJobDTO jobDTO) {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(empId));
        if (employeeModel.isPresent()) {
            EmployeeModel employee = employeeModel.get();
            List<FavJobDTO> favJobs = employee.getSavedJobs();
            if (favJobs == null) {
                favJobs = new ArrayList<>();
            }
            favJobs.add(jobDTO);
            employee.setSavedJobs(favJobs);
            return employeeRepository.save(employee);
        } else {
            throw new RuntimeException("Employee not found for id: " + empId);
        }
    }

    public EmployeeModel removeFavoriteJob(String empId, String jobId) {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(empId));
        if (employeeModel.isPresent()) {
            EmployeeModel employee = employeeModel.get();
            List<FavJobDTO> favJobs = employee.getSavedJobs();
            if (favJobs == null) {
                favJobs = new ArrayList<>();
            }
            favJobs.removeIf(job -> job.getJobId().equals(jobId));
            employee.setSavedJobs(favJobs);
            return employeeRepository.save(employee);
        } else {
            throw new RuntimeException("Employee not found for id: " + empId);
        }
    }

    public EmployeeModel changeFavoriteJobStatus(String empId, FavJobDTO jobDto) throws IOException {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(empId));
        if (employeeModel.isPresent()) {
            EmployeeModel employee = employeeModel.get();
            List<FavJobDTO> favJobs = employee.getSavedJobs();
            if (favJobs == null) {
                favJobs = new ArrayList<>();
            }
            for (FavJobDTO favJob : favJobs) {
                if (!employee.getEmail().isEmpty()){
                    if (jobDto.getStatus().equals("inprogress")) {
                        emailService.sendSelectionNotification(employee.getEmail(), employee.getFirstname());
                    } else if (jobDto.getStatus().equals("rejected")) {
                        emailService.sendRejectionNotification(employee.getEmail(), employee.getFirstname());
                    }
                }
                if (favJob.getJobId().equals(jobDto.getJobId())) {
                    favJob.setStatus(jobDto.getStatus());
                    return employeeRepository.save(employee);
                } else {
                    saveFavoriteJob(empId, jobDto);
                }
            }
        } else {
            throw new RuntimeException("Employee not found for id: " + empId);
        }
        return null;
    }

    public EmployeeModel follow(String followerId, String targetId) {
        EmployeeModel follower = getEmployee(followerId);
        EmployeeModel target = getEmployee(targetId);

        if (follower != null && target != null) {
            // Add to following
            EmpFollowingModel followingModel = EmpFollowingModel.builder()
                    .employeeId(followerId)
                    .followings(Collections.singletonList(EmpFollowingDTO.builder()
                            .id(targetId)
                            .followingId(targetId)
                            .followingName(target.getFirstname() + " " + target.getLastname())
                            .followingOccupation(target.getOccupation())
                            .followingImage(target.getImage())
                            .build()))
                    .build();
            empFollowingService.addEmpFollowing(followingModel);

            // Add to followers
            EmpFollowersModel followersModel = EmpFollowersModel.builder()
                    .employeeId(targetId)
                    .followers(Collections.singletonList(EmpFollowersDTO.builder()
                            .id(followerId)
                            .followerId(followerId)
                            .followerName(follower.getFirstname() + " " + follower.getLastname())
                            .followerOccupation(follower.getOccupation())
                            .followerImage(follower.getImage())
                            .build()))
                    .build();
            empFollowersService.addEmpFollowers(followersModel);

            return follower;
        }
        return null;
    }

    public EmployeeModel unfollow(String followerId, String targetId) {
        empFollowingService.deleteFollowing(followerId, targetId);
        empFollowersService.deleteEmpFollower(targetId, followerId);
        return getEmployee(followerId);
    }

    public void deleteEmployee(String id) {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(id);
        Optional<CredentialsModel> credentialsModel = credentialsRepository.findByEmployeeId(id);
        List<EmpEducationModel> empEducationModel = empEducationRepository.findByEmployeeId(id);
        List<EmpContactModel> empContactModel = empContactRepository.findByEmployeeId(id);
        List<EmpExperiencesModel> empExperienceModel = empExperiencesRepository.findByEmployeeId(id);
        List<EmpSkillsModel> empSkillsModel = empSkillsRepository.findByEmployeeId(id);
        List<EmpFollowersModel> empFollowersModel = empFollowersRepository.findByEmployeeId(id);
        List<EmpFollowingModel> empFollowingModel = empFollowingRepository.findByEmployeeId(id);

        if (employeeModel.isPresent() && credentialsModel.isPresent()) {
            EmployeeModel employee = employeeModel.get();
            CredentialsModel credentials = credentialsModel.get();

            if (empEducationModel != null) {
                empEducationRepository.deleteByEmployeeId(id);
            }
            if (empContactModel != null){
                empContactRepository.deleteByEmployeeId(id);
            }
            if (empExperienceModel != null){
                empExperiencesRepository.deleteByEmployeeId(id);
            }
            if (empSkillsModel != null){
                empSkillsRepository.deleteByEmployeeId(id);
            }
            if (empFollowersModel != null){
                empFollowersRepository.deleteByEmployeeId(id);
            }
            if (empFollowingModel != null){
                empFollowingRepository.deleteByEmployeeId(id);
            }

            employeeRepository.delete(Objects.requireNonNull(employee));
            credentialsRepository.delete(Objects.requireNonNull(credentials));
        } else {
            throw new RuntimeException("Employee not found for id: " + id);
        }
    }

    public void deleteCompany(String id) {
        Optional<EmployeeModel> employeeModel = employeeRepository.findById(id);
        Optional<CredentialsModel> credentialsModel = credentialsRepository.findByEmployeeId(id);
        if (employeeModel.isPresent() && credentialsModel.isPresent()) {
            EmployeeModel employee = employeeModel.get();
            CredentialsModel credentials = credentialsModel.get();

            credentials.setUserLevel("1");
            credentials.setRole("candidate");
            employee.setCompanyId(null);
            employeeRepository.save(employee);
            credentialsRepository.save(credentials);
        } else {
            throw new RuntimeException("Employee not found for id: " + id);
        }
    }

    @Async
    public CompletableFuture<List<EmployeeModel>> getAllEmployeesAsync() {
        // Fetch employees asynchronously
        List<EmployeeModel> employees = getAllEmployees();
        return CompletableFuture.completedFuture(employees);
    }

    @Async
    public CompletableFuture<EmployeeModel> getEmployeeByIdAsync(String id) {
        EmployeeModel employee = getEmployee(id);
        return CompletableFuture.completedFuture(employee);
    }

    @Async
    public CompletableFuture<EmployeeModel> createEmployeeAsync(EmployeeModel employee) {
        EmployeeModel savedEmployee = addEmployee(employee);
        return CompletableFuture.completedFuture(savedEmployee);
    }
}

