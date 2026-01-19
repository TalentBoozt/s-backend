package com.talentboozt.s_backend.domains.user.service;

import com.talentboozt.s_backend.domains.user.dto.EmpEducationDTO;
import com.talentboozt.s_backend.domains.user.model.EmpEducationModel;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.repository.EmpEducationRepository;
import com.talentboozt.s_backend.domains.user.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class EmpEducationService {

    @Autowired
    private EmpEducationRepository empEducationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmpEducationModel> getEmpEducationByEmployeeId(String employeeId) {
        return empEducationRepository.findByEmployeeId(employeeId);
    }

    public EmpEducationModel addEmpEducation(EmpEducationModel empEducation) {
        List<EmpEducationModel> empEducationList = empEducationRepository.findByEmployeeId(empEducation.getEmployeeId());
        EmpEducationModel empEducationModel;

        if (!empEducationList.isEmpty()) {
            empEducationModel = empEducationList.get(0);
            List<EmpEducationDTO> educations = empEducationModel.getEducation();
            if (educations == null) {
                educations = new ArrayList<>();
            }
            educations.addAll(empEducation.getEducation());
            empEducationModel.setEducation(educations);
        } else {
            empEducationModel = empEducationRepository.save(empEducation);
        }

        empEducationRepository.save(empEducationModel);

        Optional<EmployeeModel> employeeModel = employeeRepository.findById(Objects.requireNonNull(empEducation.getEmployeeId()));
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setEducation(empEducationModel.getId());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>();
            }
            profileCompleted.put("education", true);
            existingEmployee.setProfileCompleted(profileCompleted);

            employeeRepository.save(existingEmployee);
        }
        return empEducationModel;
    }

    public EmpEducationModel updateEmpEducation(String id, EmpEducationModel empEducation) {
        EmpEducationModel existingEmpEducation = empEducationRepository.findById(Objects.requireNonNull(id)).orElse(null);
        if (existingEmpEducation != null) {
            existingEmpEducation.setEmployeeId(empEducation.getEmployeeId());
            existingEmpEducation.setEducation(empEducation.getEducation());
            return empEducationRepository.save(existingEmpEducation);
        }
        return null;
    }

    public void deleteEmpEducations(String employeeId) {
        empEducationRepository.deleteByEmployeeId(employeeId);
    }

    public EmpEducationModel deleteEmpEducation(String employeeId, String educationId) {
        List<EmpEducationModel> empEducationsList = empEducationRepository.findByEmployeeId(employeeId);
        if (!empEducationsList.isEmpty()) {
            EmpEducationModel empEducationsModel = empEducationsList.get(0);
            List<EmpEducationDTO> educations = empEducationsModel.getEducation();
            if (educations != null) {
                educations.removeIf(experience -> experience.getId().equals(educationId));
                empEducationsModel.setEducation(educations);

                empEducationRepository.save(empEducationsModel);
            }

            return empEducationsModel;
        }
        throw new RuntimeException("Educations not found for employeeId: " + employeeId);
    }

    public EmpEducationModel editEmpEducation(String employeeId, EmpEducationDTO updatedEducation) {
        List<EmpEducationModel> empEducationsList = empEducationRepository.findByEmployeeId(employeeId);
        if (!empEducationsList.isEmpty()) {
            EmpEducationModel empEducationsModel = empEducationsList.get(0);
            List<EmpEducationDTO> educations = empEducationsModel.getEducation();

            if (educations != null) {
                for (EmpEducationDTO education : educations) {
                    if (education.getId().equals(updatedEducation.getId())) {
                        education.setSchool(updatedEducation.getSchool());
                        education.setSchoolLogo(updatedEducation.getSchoolLogo());
                        education.setDegree(updatedEducation.getDegree());
                        education.setCountry(updatedEducation.getCountry());
                        education.setStartDate(updatedEducation.getStartDate());
                        education.setEndDate(updatedEducation.getEndDate());
                        education.setDescription(updatedEducation.getDescription());
                        break;
                    }
                }
                empEducationsModel.setEducation(educations);

                empEducationRepository.save(empEducationsModel);
            }

            return empEducationsModel;
        }
        throw new RuntimeException("Educations not found for employeeId: " + employeeId);
    }

    @Async
    public CompletableFuture<List<EmpEducationModel>> getEmpEducationByEmployeeIdAsync(String employeeId) {
        // Fetch employees asynchronously
        List<EmpEducationModel> empEducations = getEmpEducationByEmployeeId(employeeId);
        return CompletableFuture.completedFuture(empEducations);
    }
}
