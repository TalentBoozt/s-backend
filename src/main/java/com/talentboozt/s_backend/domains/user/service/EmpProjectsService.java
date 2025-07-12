package com.talentboozt.s_backend.domains.user.service;

import com.talentboozt.s_backend.domains.user.dto.EmpProjectsDTO;
import com.talentboozt.s_backend.domains.user.model.EmpProjectsModel;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.repository.EmpProjectsRepository;
import com.talentboozt.s_backend.domains.user.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class EmpProjectsService {

    @Autowired
    private EmpProjectsRepository empProjectsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmpProjectsModel> getEmpProjectsByEmployeeId(String employeeId) {
        return empProjectsRepository.findByEmployeeId(employeeId);
    }

    public EmpProjectsModel addEmpProjects(EmpProjectsModel empProjects) {
        List<EmpProjectsModel> empProjectsList = getEmpProjectsByEmployeeId(empProjects.getEmployeeId());
        EmpProjectsModel empProjectsModel;

        if (!empProjectsList.isEmpty()) {
            empProjectsModel = empProjectsList.get(0);
            List<EmpProjectsDTO> projects = empProjectsModel.getProjects();
            if (projects == null) {
                projects = new java.util.ArrayList<>(); // Initialize the projects list if it's null
            }
            projects.addAll(empProjects.getProjects());
            empProjectsModel.setProjects(projects);
        } else {
            empProjectsModel = empProjectsRepository.save(empProjects);
        }

        empProjectsRepository.save(empProjectsModel);

        Optional<EmployeeModel> employeeModel = employeeRepository.findById(empProjects.getEmployeeId());
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setProjects(empProjectsModel.getId());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>();
            }
            profileCompleted.put("projects", true);
            existingEmployee.setProfileCompleted(profileCompleted);

            employeeRepository.save(existingEmployee);
        }

        return empProjectsModel;
    }

    public EmpProjectsModel updateEmpProjects(String id, EmpProjectsModel empProjects) {
        EmpProjectsModel existingEmpProjects = empProjectsRepository.findById(id).orElse(null);
        if (existingEmpProjects != null) {
            existingEmpProjects.setEmployeeId(empProjects.getEmployeeId());
            existingEmpProjects.setProjects(empProjects.getProjects());
            return empProjectsRepository.save(existingEmpProjects);
        }
        return null;
    }

    public void deleteEmpProjects(String id) {
        empProjectsRepository.deleteById(id);
    }

    public EmpProjectsModel deleteEmpProject(String employeeId, String projectId) {
        List<EmpProjectsModel> empProjectsList = getEmpProjectsByEmployeeId(employeeId);
        if (!empProjectsList.isEmpty()) {
            EmpProjectsModel empProjectsModel = empProjectsList.get(0);
            List<EmpProjectsDTO> projects = empProjectsModel.getProjects();
            if (projects != null) {
                projects.removeIf(project -> project.getId().equals(projectId));
                empProjectsModel.setProjects(projects);
                empProjectsRepository.save(empProjectsModel);
            }
            return empProjectsModel;
        }
        throw new RuntimeException("Projects not found for employeeId: " + employeeId);
    }

    public EmpProjectsModel editEmpProject(String employeeId, EmpProjectsDTO updatedProject) {
        List<EmpProjectsModel> empProjectsList = getEmpProjectsByEmployeeId(employeeId);
        if (!empProjectsList.isEmpty()) {
            EmpProjectsModel empProjectsModel = empProjectsList.get(0);
            List<EmpProjectsDTO> projects = empProjectsModel.getProjects();
            if (projects != null) {
                for (EmpProjectsDTO project : projects) {
                    if (project.getId().equals(updatedProject.getId())) {
                        project.setTitle(updatedProject.getTitle());
                        project.setCompany(updatedProject.getCompany());
                        project.setRole(updatedProject.getRole());
                        project.setStartDate(updatedProject.getStartDate());
                        project.setEndDate(updatedProject.getEndDate());
                        project.setDemo(updatedProject.getDemo());
                        project.setSource(updatedProject.getSource());
                        project.setDescription(updatedProject.getDescription());
                        break;
                    }
                }
                empProjectsModel.setProjects(projects);
                empProjectsRepository.save(empProjectsModel);
            }
            return empProjectsModel;
        }
        throw new RuntimeException("Projects not found for employeeId: " + employeeId);
    }

    @Async
    public CompletableFuture<List<EmpProjectsModel>> getEmpProjectsByEmployeeIdAsync(String employeeId) {
        List<EmpProjectsModel> empProjects = getEmpProjectsByEmployeeId(employeeId);
        return CompletableFuture.completedFuture(empProjects);
    }
}
