package com.talentboozt.s_backend.domains.user.controller;

import com.talentboozt.s_backend.domains.user.dto.EmpProjectsDTO;
import com.talentboozt.s_backend.domains.user.model.EmpProjectsModel;
import com.talentboozt.s_backend.domains.user.service.EmpProjectsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/emp_projects")
public class EmpProjectsController {

    @Autowired
    private EmpProjectsService empProjectsService;

    @GetMapping("/getByEmployeeId/{employeeId}")
    public List<EmpProjectsModel> getEmpProjectsByEmployeeId(@PathVariable String employeeId) {
        return empProjectsService.getEmpProjectsByEmployeeId(employeeId);
    }

    @PostMapping("/add")
    public EmpProjectsModel addEmpProjects(@RequestBody EmpProjectsModel empProjects) {
        return empProjectsService.addEmpProjects(empProjects);
    }

    @PutMapping("/update/{id}")
    public EmpProjectsModel updateEmpProjects(@PathVariable String id, @RequestBody EmpProjectsModel empProjects) {
        return empProjectsService.updateEmpProjects(id, empProjects);
    }

    @PutMapping("/edit-single/{employeeId}")
    public EmpProjectsModel updateEmpProject(@PathVariable String employeeId, @RequestBody EmpProjectsDTO empProjects) {
        return empProjectsService.editEmpProject(employeeId, empProjects);
    }

    @DeleteMapping("/delete/{employeeId}")
    public void deleteEmpProjects(@PathVariable String employeeId) {
        empProjectsService.deleteEmpProjects(employeeId);
    }

    @DeleteMapping("/delete-single/{employeeId}/{projectId}")
    public void deleteEmpProject(@PathVariable String employeeId, @PathVariable String projectId) {
        empProjectsService.deleteEmpProject(employeeId, projectId);
    }
}
