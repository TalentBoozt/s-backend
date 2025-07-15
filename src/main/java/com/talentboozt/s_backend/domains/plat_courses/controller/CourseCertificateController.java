package com.talentboozt.s_backend.domains.plat_courses.controller;

import com.talentboozt.s_backend.domains.plat_courses.model.CourseCertificateModel;
import com.talentboozt.s_backend.domains.plat_courses.service.CourseCertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v2/course-certificates")
public class CourseCertificateController {

    @Autowired
    private CourseCertificateService courseCertificateService;

    @GetMapping("/all")
    public Iterable<CourseCertificateModel> getAllCertificates() {
        return courseCertificateService.getAllCertificates();
    }

    @GetMapping("/get/{id}")
    public CourseCertificateModel getCertificate(@PathVariable String id) {
        return courseCertificateService.getCertificate(id);
    }

    @GetMapping("/get/course/{id}")
    public Iterable<CourseCertificateModel> getCertificatesByCourseId(@PathVariable String id) {
        return courseCertificateService.getCertificatesByCourseId(id);
    }

    @GetMapping("/get/employee/{id}")
    public Iterable<CourseCertificateModel> getCertificatesByEmployeeId(@PathVariable String id) {
        return courseCertificateService.getCertificatesByEmployeeId(id);
    }

    @GetMapping("/get/type/{type}")
    public Iterable<CourseCertificateModel> getCertificatesByType(@PathVariable String type) {
        return courseCertificateService.getCertificatesByType(type);
    }

    @GetMapping("/get/delivered/{delivered}")
    public Iterable<CourseCertificateModel> getCertificatesByDelivered(@PathVariable boolean delivered) {
        return courseCertificateService.getCertificatesByDelivered(delivered);
    }

    @GetMapping("/get/certificate/{certificateId}")
    public CourseCertificateModel getCertificatesByCertificateId(@PathVariable String certificateId) {
        return courseCertificateService.getCertificatesByCertificateId(certificateId);
    }

    @PostMapping("/add")
    public CourseCertificateModel addCertificate(@RequestBody CourseCertificateModel certificate) {
        return courseCertificateService.addCertificate(certificate);
    }

    @PutMapping("/update/{id}")
    public CourseCertificateModel updateSystemCertificate(@PathVariable String id, @RequestBody CourseCertificateModel certificate) {
        return courseCertificateService.updateSystemCertificate(id, certificate);
    }

    @PutMapping("/update/certificate/{id}")
    public CourseCertificateModel updateSystemCertificateByCertificateId(@PathVariable String id, @RequestBody CourseCertificateModel certificate) {
        return courseCertificateService.updateSystemCertificateByCertificateId(id, certificate);
    }
}
