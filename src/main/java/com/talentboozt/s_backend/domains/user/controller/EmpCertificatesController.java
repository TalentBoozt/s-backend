package com.talentboozt.s_backend.domains.user.controller;

import com.talentboozt.s_backend.domains.user.dto.EmpCertificatesDTO;
import com.talentboozt.s_backend.domains.user.model.EmpCertificatesModel;
import com.talentboozt.s_backend.domains.user.service.EmpCertificatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/emp_certificates")
public class EmpCertificatesController {

    @Autowired
    private EmpCertificatesService empCertificatesService;

    @GetMapping("/getByEmployeeId/{employeeId}")
    public List<EmpCertificatesModel> getEmpCertificatesByEmployeeId(@PathVariable String employeeId) {
        return empCertificatesService.getEmpCertificatesByEmployeeId(employeeId);
    }

    @PostMapping("/add")
    public EmpCertificatesModel addEmpCertificates(@RequestBody EmpCertificatesModel empCertificates) {
        return empCertificatesService.addEmpCertificates(empCertificates);
    }

    @PutMapping("/update/{id}")
    public EmpCertificatesModel updateEmpCertificates(@PathVariable String id, @RequestBody EmpCertificatesModel empCertificates) {
        return empCertificatesService.updateEmpCertificates(id, empCertificates);
    }

    @PutMapping("/edit-single/{employeeId}")
    public EmpCertificatesModel editEmpCertificate(@PathVariable String employeeId, @RequestBody EmpCertificatesDTO certificate) {
        return empCertificatesService.editEmpCertificate(employeeId, certificate);
    }

    @DeleteMapping("/delete/{employeeId}")
    public void deleteEmpCertificates(@PathVariable String employeeId) {
        empCertificatesService.deleteEmpCertificates(employeeId);
    }

    @DeleteMapping("/delete-single/{employeeId}/{certificateId}")
    public EmpCertificatesModel deleteEmpCertificate(@PathVariable String employeeId, @PathVariable String certificateId) {
        return empCertificatesService.deleteEmpCertificate(employeeId, certificateId);
    }
}
