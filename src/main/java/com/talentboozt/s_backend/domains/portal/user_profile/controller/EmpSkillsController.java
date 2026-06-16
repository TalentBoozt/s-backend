package com.talentboozt.s_backend.domains.portal.user_profile.controller;

import com.talentboozt.s_backend.domains.portal.user_profile.dto.EmpSkillsDTO;
import com.talentboozt.s_backend.domains.portal.user_profile.model.EmpSkillsModel;
import com.talentboozt.s_backend.domains.portal.user_profile.service.EmpSkillsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/emp_skills")
public class EmpSkillsController {

    @Autowired
    private EmpSkillsService empSkillsService;

    @GetMapping("/getByEmployeeId/{employeeId}")
    public List<EmpSkillsModel> getEmpSkillsByEmployeeId(@PathVariable String employeeId) {
        return empSkillsService.getEmpSkillsByEmployeeId(employeeId);
    }

    @PostMapping("/add")
    public EmpSkillsModel addEmpSkills(@RequestBody EmpSkillsModel empSkills) {
        return empSkillsService.addEmpSkills(empSkills);
    }

    @PutMapping("/update/{id}")
    public EmpSkillsModel updateEmpSkills(@PathVariable String id, @RequestBody EmpSkillsModel empSkills) {
        return empSkillsService.updateEmpSkills(id, empSkills);
    }

    @PutMapping("/edit-single/{employeeId}")
    public EmpSkillsModel updateEmpSkill(@PathVariable String employeeId, @RequestBody EmpSkillsDTO empSkills) {
        return empSkillsService.editEmpSkill(employeeId, empSkills);
    }

    @DeleteMapping("/delete/{employeeId}")
    public void deleteEmpSkills(@PathVariable String employeeId) {
        empSkillsService.deleteEmpSkills(employeeId);
    }

    @DeleteMapping("/delete-single/{employeeId}/{skillId}")
    public void deleteEmpSkills(@PathVariable String employeeId, @PathVariable String skillId) {
        empSkillsService.deleteEmpSkill(employeeId, skillId);
    }
}
