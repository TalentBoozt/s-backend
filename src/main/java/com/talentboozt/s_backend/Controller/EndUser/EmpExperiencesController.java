package com.talentboozt.s_backend.Controller.EndUser;

import com.talentboozt.s_backend.DTO.EndUser.EmpExperiencesDTO;
import com.talentboozt.s_backend.Model.EndUser.EmpExperiencesModel;
import com.talentboozt.s_backend.Service.EndUser.EmpExperiencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/emp_experiences")
public class EmpExperiencesController {

    @Autowired
    private EmpExperiencesService empExperiencesService;

    @GetMapping("/getByEmployeeId/{employeeId}")
    public List<EmpExperiencesModel> getEmpExperiencesByEmployeeId(@PathVariable String employeeId) {
        return empExperiencesService.getEmpExperiencesByEmployeeId(employeeId);
    }

    @PostMapping("/add")
    public EmpExperiencesModel addEmpExperiences(@RequestBody EmpExperiencesModel empExperiences) {
        return empExperiencesService.addEmpExperiences(empExperiences);
    }

    @PutMapping("/update/{id}")
    public EmpExperiencesModel updateEmpExperiences(@PathVariable String id, @RequestBody EmpExperiencesModel empExperiences) {
        return empExperiencesService.updateEmpExperiences(id, empExperiences);
    }

    @PutMapping("/edit-single/{employeeId}")
    public EmpExperiencesModel updateEmpExperience(@PathVariable String employeeId, @RequestBody EmpExperiencesDTO empExperiences) {
        return empExperiencesService.editEmpExperience(employeeId, empExperiences);
    }

    @DeleteMapping("/delete/{employeeId}")
    public void deleteEmpExperiences(@PathVariable String employeeId) {
        empExperiencesService.deleteEmpExperiences(employeeId);
    }

    @DeleteMapping("/delete-single/{employeeId}/{experienceId}")
    public void deleteEmpExperiences(@PathVariable String employeeId, @PathVariable String experienceId) {
        empExperiencesService.deleteEmpExperience(employeeId, experienceId);
    }
}
