package com.talentboozt.s_backend.domains.user.service;

import com.talentboozt.s_backend.domains.user.dto.EmpExperiencesDTO;
import com.talentboozt.s_backend.domains.user.model.EmpExperiencesModel;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.repository.EmpExperiencesRepository;
import com.talentboozt.s_backend.domains.user.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class EmpExperiencesService {

    @Autowired
    private EmpExperiencesRepository empExperiencesRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmpExperiencesModel> getEmpExperiencesByEmployeeId(String employeeId) {
        return empExperiencesRepository.findByEmployeeId(employeeId);
    }

    public EmpExperiencesModel addEmpExperiences(EmpExperiencesModel empExperiences) {
        List<EmpExperiencesModel> empExperiencesList = empExperiencesRepository.findByEmployeeId(empExperiences.getEmployeeId());
        EmpExperiencesModel empExperiencesModel;

        if (!empExperiencesList.isEmpty()) {
            empExperiencesModel = empExperiencesList.get(0);
            List<EmpExperiencesDTO> experiences = empExperiencesModel.getExperiences();
            if (experiences == null) {
                experiences = new ArrayList<>();
            }
            experiences.addAll(empExperiences.getExperiences());
            empExperiencesModel.setExperiences(experiences);
        } else {
            empExperiencesModel = empExperiencesRepository.save(empExperiences);
        }

        empExperiencesRepository.save(empExperiencesModel);

        Optional<EmployeeModel> employeeModel = employeeRepository.findById(empExperiences.getEmployeeId());
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setExperiences(empExperiencesModel.getId());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>();
            }
            profileCompleted.put("experiences", true);
            existingEmployee.setProfileCompleted(profileCompleted);

            employeeRepository.save(existingEmployee);
        }
        return empExperiencesModel;
    }

    public EmpExperiencesModel updateEmpExperiences(String id, EmpExperiencesModel empExperiences) {
        EmpExperiencesModel existingEmpExperiences = empExperiencesRepository.findById(id).orElse(null);
        if (existingEmpExperiences != null) {
            existingEmpExperiences.setEmployeeId(empExperiences.getEmployeeId());
            existingEmpExperiences.setExperiences(empExperiences.getExperiences());
            return empExperiencesRepository.save(existingEmpExperiences);
        }
        return null;
    }

    public void deleteEmpExperiences(String employeeId) {
        empExperiencesRepository.deleteByEmployeeId(employeeId);
    }

    public EmpExperiencesModel deleteEmpExperience(String employeeId, String experienceId) {
        List<EmpExperiencesModel> empExperiencesList = empExperiencesRepository.findByEmployeeId(employeeId);
        if (!empExperiencesList.isEmpty()) {
            EmpExperiencesModel empExperiencesModel = empExperiencesList.get(0);
            List<EmpExperiencesDTO> experiences = empExperiencesModel.getExperiences();
            if (experiences != null) {
                experiences.removeIf(experience -> experience.getId().equals(experienceId));
                empExperiencesModel.setExperiences(experiences);

                empExperiencesRepository.save(empExperiencesModel);
            }

            return empExperiencesModel;
        }
        throw new RuntimeException("Experiences not found for employeeId: " + employeeId);
    }

    public EmpExperiencesModel editEmpExperience(String employeeId, EmpExperiencesDTO updatedExperience) {
        List<EmpExperiencesModel> empExperiencesList = empExperiencesRepository.findByEmployeeId(employeeId);
        if (!empExperiencesList.isEmpty()) {
            EmpExperiencesModel empExperiencesModel = empExperiencesList.get(0);
            List<EmpExperiencesDTO> experiences = empExperiencesModel.getExperiences();

            if (experiences != null) {
                for (EmpExperiencesDTO experience : experiences) {
                    if (experience.getId().equals(updatedExperience.getId())) {
                        experience.setCompany(updatedExperience.getCompany());
                        experience.setCompanyLogo(updatedExperience.getCompanyLogo());
                        experience.setPosition(updatedExperience.getPosition());
                        experience.setCountry(updatedExperience.getCountry());
                        experience.setStartDate(updatedExperience.getStartDate());
                        experience.setEndDate(updatedExperience.getEndDate());
                        experience.setDescription(updatedExperience.getDescription());
                        break;
                    }
                }
                empExperiencesModel.setExperiences(experiences);

                empExperiencesRepository.save(empExperiencesModel);
            }

            return empExperiencesModel;
        }
        throw new RuntimeException("Experiences not found for employeeId: " + employeeId);
    }

    @Async
    public CompletableFuture<List<EmpExperiencesModel>> getEmpExperiencesByEmployeeIdAsync(String employeeId) {
        // Fetch employees asynchronously
        List<EmpExperiencesModel> empExperiences = getEmpExperiencesByEmployeeId(employeeId);
        return CompletableFuture.completedFuture(empExperiences);
    }
}
