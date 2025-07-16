package com.talentboozt.s_backend.domains.user.service;

import com.talentboozt.s_backend.domains.plat_courses.dto.CertificateDTO;
import com.talentboozt.s_backend.domains.user.dto.EmpCertificatesDTO;
import com.talentboozt.s_backend.domains.user.model.EmpCertificatesModel;
import com.talentboozt.s_backend.domains.user.model.EmployeeModel;
import com.talentboozt.s_backend.domains.user.repository.EmpCertificatesRepository;
import com.talentboozt.s_backend.domains.user.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class EmpCertificatesService {

    @Autowired
    private EmpCertificatesRepository empCertificatesRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmpCertificatesModel> getEmpCertificatesByEmployeeId(String employeeId) { return empCertificatesRepository.findAllByEmployeeId(employeeId); }

    public EmpCertificatesModel addEmpCertificates(EmpCertificatesModel empCertificates) {
        List<EmpCertificatesModel> empCertificatesList = getEmpCertificatesByEmployeeId(empCertificates.getEmployeeId());
        EmpCertificatesModel certificatesModel;

        if (!empCertificatesList.isEmpty()) {
            certificatesModel = empCertificatesList.get(0);
            List<EmpCertificatesDTO> certificates = certificatesModel.getCertificates();
            if (certificates == null) {
                certificates = new ArrayList<>();
            }
            certificates.addAll(empCertificates.getCertificates());
            certificatesModel.setCertificates(certificates);
        } else {
            certificatesModel = empCertificatesRepository.save(empCertificates);
        }

        empCertificatesRepository.save(certificatesModel);

        Optional<EmployeeModel> employeeModel = employeeRepository.findById(empCertificates.getEmployeeId());
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setCertificates(certificatesModel.getId());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>();
            }
            if (profileCompleted.get("certificates")) {
                return certificatesModel;
            }
            profileCompleted.put("certificates", true);
            existingEmployee.setProfileCompleted(profileCompleted);

            employeeRepository.save(existingEmployee);
        }

        return certificatesModel;
    }

    public EmpCertificatesModel updateEmpCertificates(String id, EmpCertificatesModel empCertificates) {
        EmpCertificatesModel certificatesModel = empCertificatesRepository.findById(id).orElse(null);
        if (certificatesModel != null) {
            certificatesModel.setEmployeeId(empCertificates.getEmployeeId());
            certificatesModel.setCertificates(empCertificates.getCertificates());
            return empCertificatesRepository.save(certificatesModel);
        }
        return null;
    }

    public void deleteEmpCertificates(String employeeId) {
        empCertificatesRepository.deleteByEmployeeId(employeeId);
    }

    public EmpCertificatesModel deleteEmpCertificate(String employeeId, String certificateId) {
        List<EmpCertificatesModel> empCertificatesList = getEmpCertificatesByEmployeeId(employeeId);
        if (!empCertificatesList.isEmpty()) {
            EmpCertificatesModel certificatesModel = empCertificatesList.get(0);
            List<EmpCertificatesDTO> certificates = certificatesModel.getCertificates();
            if (certificates != null) {
                certificates.removeIf(cert -> cert.getId().equals(certificateId));
                certificatesModel.setCertificates(certificates);
                empCertificatesRepository.save(certificatesModel);
            }
            return certificatesModel;
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public EmpCertificatesModel editEmpCertificate(String employeeId, EmpCertificatesDTO certificate) {
        List<EmpCertificatesModel> empCertificatesList = getEmpCertificatesByEmployeeId(employeeId);
        if (!empCertificatesList.isEmpty()) {
            EmpCertificatesModel certificatesModel = empCertificatesList.get(0);
            List<EmpCertificatesDTO> certificates = certificatesModel.getCertificates();
            if (certificates != null) {
                for (EmpCertificatesDTO cert : certificates) {
                    if (cert.getId().equals(certificate.getId())) {
                        cert.setName(certificate.getName());
                        cert.setOrganization(certificate.getOrganization());
                        cert.setDate(certificate.getDate());
                        cert.setCertificateId(certificate.getCertificateId());
                        cert.setCertificateUrl(certificate.getCertificateUrl());
                        break;
                    }
                }
                certificatesModel.setCertificates(certificates);
                empCertificatesRepository.save(certificatesModel);
            }
            return certificatesModel;
        }
        throw new RuntimeException("Employee not found for id: " + employeeId);
    }

    public void updateCertificate(String employeeId, CertificateDTO certificateDTO) {
        EmpCertificatesModel model = getByEmployeeId(employeeId);

        if (model == null) {
            model = new EmpCertificatesModel();
            model.setEmployeeId(employeeId);
            model.setCertificates(new ArrayList<>(
                    model.getCertificates().stream()
                            .collect(Collectors.toMap(
                                    EmpCertificatesDTO::getCertificateId,
                                    cert -> cert,
                                    (existing, replacement) -> existing // or replacement
                            ))
                            .values()
            ));
        }

        Map<String, EmpCertificatesDTO> certMap = new HashMap<>();
        for (EmpCertificatesDTO cert : model.getCertificates()) {
            certMap.put(cert.getCertificateId(), cert);
        }

        EmpCertificatesDTO existing = certMap.get(certificateDTO.getCertificateId());
        if (existing != null) {
            existing.setCertificateUrl(certificateDTO.getUrl());
        } else {
            EmpCertificatesDTO newCert = new EmpCertificatesDTO();
            newCert.setCertificateId(certificateDTO.getCertificateId());
            newCert.setCertificateUrl(certificateDTO.getUrl());
            certMap.put(certificateDTO.getCertificateId(), newCert);
        }

        model.setCertificates(new ArrayList<>(certMap.values()));

        empCertificatesRepository.save(model);
    }

    @Async
    public CompletableFuture<List<EmpCertificatesModel>> getEmpCertificatesByEmployeeIdAsync(String employeeId) {
        List<EmpCertificatesModel> empCertificates = getEmpCertificatesByEmployeeId(employeeId);
        return CompletableFuture.completedFuture(empCertificates);
    }

    public EmpCertificatesModel getByEmployeeId(String employeeId) {
        return empCertificatesRepository.findByEmployeeId(employeeId).orElse(null);
    }
}
