package com.talentboozt.s_backend.Service.EndUser;

import com.talentboozt.s_backend.DTO.EndUser.EmpContactDTO;
import com.talentboozt.s_backend.DTO.common.SocialLinksDTO;
import com.talentboozt.s_backend.Model.EndUser.EmpContactModel;
import com.talentboozt.s_backend.Model.EndUser.EmployeeModel;
import com.talentboozt.s_backend.Repository.EndUser.EmpContactRepository;
import com.talentboozt.s_backend.Repository.EndUser.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class EmpContactService {

    @Autowired
    private EmpContactRepository empContactRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<EmpContactModel> getEmpContactByEmployeeId(String employeeId) {
        return empContactRepository.findByEmployeeId(employeeId);
    }

    public EmpContactModel addEmpContact(EmpContactModel empContact) {
        List<EmpContactModel> empContactList = empContactRepository.findByEmployeeId(empContact.getEmployeeId());
        EmpContactModel empContactModel;

        if (!empContactList.isEmpty()) {
            empContactModel = empContactList.get(0);
            List<EmpContactDTO> contacts = empContactModel.getContact();
            if (contacts == null) {
                contacts = new ArrayList<>();
            }
            contacts.addAll(empContact.getContact());
            empContactModel.setContact(contacts);
        } else {
            empContactModel = empContactRepository.save(empContact);
        }

        empContactRepository.save(empContactModel);

        Optional<EmployeeModel> employeeModel = employeeRepository.findById(empContact.getEmployeeId());
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setContactInfo(empContactModel.getId());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>();
            }
            profileCompleted.put("contactInfo", true);
            existingEmployee.setProfileCompleted(profileCompleted);

            employeeRepository.save(existingEmployee);
        }
        return empContactModel;
    }

    public EmpContactModel editEmpContact(String employeeId, EmpContactDTO updatedContact) {
        List<EmpContactModel> empContactsList = empContactRepository.findByEmployeeId(employeeId);

        if (!empContactsList.isEmpty()) {
            EmpContactModel empContactsModel = empContactsList.get(0);

            List<EmpContactDTO> contacts = empContactsModel.getContact();
            if (contacts != null) {
                for (EmpContactDTO contact : contacts) {
                    if (contact.getId().equals(updatedContact.getId())) {
                        contact.setPhone(updatedContact.getPhone());
                        contact.setEmail(updatedContact.getEmail());
                        contact.setAddress(updatedContact.getAddress());
                        contact.setCity(updatedContact.getCity());
                        contact.setCountry(updatedContact.getCountry());
                        contact.setZipCode(updatedContact.getZipCode());
                        contact.setWebsite(updatedContact.getWebsite());
                        break;
                    }
                }
                empContactsModel.setContact(contacts);

                empContactRepository.save(empContactsModel);
            }

            return empContactsModel;
        }
        throw new RuntimeException("Contacts not found for employeeId: " + employeeId);
    }

    public EmpContactModel AddEmpSocialLinks(EmpContactModel empContact) {
        List<EmpContactModel> empContactList = empContactRepository.findByEmployeeId(empContact.getEmployeeId());
        EmpContactModel empContactModel;

        if (!empContactList.isEmpty()) {
            empContactModel = empContactList.get(0);
            List<SocialLinksDTO> socialLinks = empContactModel.getSocialLinks();
            if (socialLinks == null) {
                socialLinks = new ArrayList<>();
            }
            socialLinks.addAll(empContact.getSocialLinks());
            empContactModel.setSocialLinks(socialLinks);
        } else {
            empContactModel = empContactRepository.save(empContact);
        }

        empContactRepository.save(empContactModel);

        Optional<EmployeeModel> employeeModel = employeeRepository.findById(empContact.getEmployeeId());
        if (employeeModel.isPresent()) {
            EmployeeModel existingEmployee = employeeModel.get();
            existingEmployee.setContactInfo(empContactModel.getId());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingEmployee.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>();
            }

            profileCompleted.put("socialLinks", true);
            existingEmployee.setProfileCompleted(profileCompleted);

            employeeRepository.save(existingEmployee);
        }
        return empContactModel;
    }

    public EmpContactModel editEmpSocialLinks(String employeeId, SocialLinksDTO updatedSocialLinks) {
        List<EmpContactModel> empContactsList = empContactRepository.findByEmployeeId(employeeId);

        if (!empContactsList.isEmpty()) {
            EmpContactModel empContactsModel = empContactsList.get(0);

            List<SocialLinksDTO> links = empContactsModel.getSocialLinks();
            if (links != null) {
                for (SocialLinksDTO link : links) {
                    if (link.getId().equals(updatedSocialLinks.getId())) {
                        link.setFacebook(updatedSocialLinks.getFacebook());
                        link.setTwitter(updatedSocialLinks.getTwitter());
                        link.setLinkedin(updatedSocialLinks.getLinkedin());
                        link.setGithub(updatedSocialLinks.getGithub());
                        link.setInstagram(updatedSocialLinks.getInstagram());
                        break;
                    }
                }
                empContactsModel.setSocialLinks(links);

                empContactRepository.save(empContactsModel);
            }

            return empContactsModel;
        }
        throw new RuntimeException("Contacts not found for employeeId: " + employeeId);
    }

    public EmpContactModel updateEmpContact(String id, EmpContactModel empContact) {
        EmpContactModel existingEmpContact = empContactRepository.findById(id).orElse(null);
        if (existingEmpContact != null) {
            existingEmpContact.setEmployeeId(empContact.getEmployeeId());
            existingEmpContact.setContact(empContact.getContact());
            existingEmpContact.setSocialLinks(empContact.getSocialLinks());
            return empContactRepository.save(existingEmpContact);
        }
        return null;
    }

    public void deleteEmpContact(String employeeId) {
        empContactRepository.deleteByEmployeeId(employeeId);
    }

    public EmpContactModel updatePublicity(String id) {
        Optional<EmpContactModel> empContactModel = empContactRepository.findById(id);
        if (empContactModel.isPresent()) {
            EmpContactModel existingEmpContact = empContactModel.get();
            existingEmpContact.setPublicity(!existingEmpContact.isPublicity());
            return empContactRepository.save(existingEmpContact);
        }
        return null;
    }

    @Async
    public CompletableFuture<List<EmpContactModel>> getEmpContactByEmployeeIdAsync(String employeeId) {
        // Fetch employees asynchronously
        List<EmpContactModel> empContacts = getEmpContactByEmployeeId(employeeId);
        return CompletableFuture.completedFuture(empContacts);
    }
}
