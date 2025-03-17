package com.talentboozt.s_backend.Controller.EndUser;

import com.talentboozt.s_backend.DTO.EndUser.EmpContactDTO;
import com.talentboozt.s_backend.DTO.common.SocialLinksDTO;
import com.talentboozt.s_backend.Model.EndUser.EmpContactModel;
import com.talentboozt.s_backend.Service.EndUser.EmpContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/emp_contact")
public class EmpContactController {

    @Autowired
    private EmpContactService empContactService;

    @GetMapping("/getByEmployeeId/{employeeId}")
    public List<EmpContactModel> getEmpContactByEmployeeId(@PathVariable String employeeId) {
        return empContactService.getEmpContactByEmployeeId(employeeId);
    }

    @PostMapping("/add")
    public EmpContactModel addEmpContact(@RequestBody EmpContactModel empContact) {
        return empContactService.addEmpContact(empContact);
    }

    @PostMapping("/add-social")
    public EmpContactModel addSocialLinks(@RequestBody EmpContactModel socialLinks) {
        return empContactService.AddEmpSocialLinks(socialLinks);
    }

    @PutMapping("/update-contact/{employeeId}")
    public EmpContactModel updateEmpContact(@PathVariable String employeeId, @RequestBody EmpContactDTO empContact) {
        return empContactService.editEmpContact(employeeId, empContact);
    }

    @PutMapping("/update-social/{employeeId}")
    public EmpContactModel updateSocialLinks(@PathVariable String employeeId, @RequestBody SocialLinksDTO socialLinks) {
        return empContactService.editEmpSocialLinks(employeeId, socialLinks);
    }

    @PutMapping("/update/{id}")
    public EmpContactModel updateEmpContact(@PathVariable String id, @RequestBody EmpContactModel empContact) {
        return empContactService.updateEmpContact(id, empContact);
    }

    @DeleteMapping("/delete/{employeeId}")
    public void deleteEmpContact(@PathVariable String employeeId) {
        empContactService.deleteEmpContact(employeeId);
    }

    @PutMapping("/publicity/{id}")
    public EmpContactModel updatePublicity(@PathVariable String id) {
        return empContactService.updatePublicity(id);
    }
}
