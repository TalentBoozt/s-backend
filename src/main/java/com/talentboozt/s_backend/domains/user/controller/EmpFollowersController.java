package com.talentboozt.s_backend.domains.user.controller;

import com.talentboozt.s_backend.domains.user.dto.EmpFollowersDTO;
import com.talentboozt.s_backend.domains.user.model.EmpFollowersModel;
import com.talentboozt.s_backend.domains.user.service.EmpFollowersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/emp_followers")
public class EmpFollowersController {

    @Autowired
    private EmpFollowersService empFollowersService;

    @GetMapping("/getByEmployeeId/{employeeId}")
    public List<EmpFollowersModel> getEmpFollowersByEmployeeId(@PathVariable String employeeId) {
        return empFollowersService.getEmpFollowersByEmployeeId(employeeId);
    }

    @PostMapping("/add")
    public EmpFollowersModel addEmpFollowers(@RequestBody EmpFollowersModel empFollowers) {
        return empFollowersService.addEmpFollowers(empFollowers);
    }

    @PutMapping("/edit-single/{employeeId}")
    public EmpFollowersModel editEmpFollowers(@PathVariable String employeeId, @RequestBody EmpFollowersDTO empFollowers) {
        return empFollowersService.editFollower(employeeId, empFollowers);
    }

    @PutMapping("/update/{id}")
    public EmpFollowersModel updateEmpFollowers(@PathVariable String id, @RequestBody EmpFollowersModel empFollowers) {
        return empFollowersService.updateEmpFollowers(id, empFollowers);
    }

    @DeleteMapping("/delete/{employeeId}")
    public void deleteEmpFollowers(@PathVariable String employeeId) {
        empFollowersService.deleteEmpFollowers(employeeId);
    }

    @DeleteMapping("/delete-single/{employeeId}/{followerId}")
    public EmpFollowersModel deleteEmpFollower(@PathVariable String employeeId, @PathVariable String followerId) {
        return empFollowersService.deleteEmpFollower(employeeId, followerId);
    }
}
