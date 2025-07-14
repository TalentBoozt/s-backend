package com.talentboozt.s_backend.domains.com_job_portal.service;

import com.talentboozt.s_backend.domains.com_job_portal.model.CompanyModel;
import com.talentboozt.s_backend.domains.com_job_portal.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CompanyService {
    @Autowired
    CompanyRepository companyRepository;

    public List<CompanyModel> getAllCompanies() {
        return companyRepository.findAll();
    }

    public List<CompanyModel> getCompaniesPaginated(int page, int size) {
        return companyRepository.findAll().stream().skip((long) page * size).limit(size).toList();
    }

    public CompanyModel getCompany(String id) {
        return companyRepository.findById(id).orElse(null);
    }

    public Optional<List<CompanyModel>> getCompanyByType(String type) {
        return companyRepository.findAllByCompanyType(type);
    }

    public CompanyModel addCompany(CompanyModel company) {
        return companyRepository.save(company);
    }

    public CompanyModel updateLogoPic(CompanyModel company) {
        Optional<CompanyModel> companyModel = companyRepository.findById(company.getId());
        if (companyModel.isPresent()) {
            CompanyModel existingCompany = companyModel.get();
            existingCompany.setLogo(company.getLogo());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingCompany.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>(); // Initialize if null
            }
            profileCompleted.put("logo", company.getLogo() != null && !company.getLogo().isEmpty());
            existingCompany.setProfileCompleted(profileCompleted);

            return companyRepository.save(existingCompany);
        }
        return null;
    }

    public CompanyModel updateCoverPic(CompanyModel company) {
        Optional<CompanyModel> companyModel = companyRepository.findById(company.getId());
        if (companyModel.isPresent()) {
            CompanyModel existingCompany = companyModel.get();
            existingCompany.setProfileBanner(company.getProfileBanner());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingCompany.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>(); // Initialize if null
            }
            profileCompleted.put("coverPic", company.getProfileBanner() != null && !company.getProfileBanner().isEmpty());
            existingCompany.setProfileCompleted(profileCompleted);

            return companyRepository.save(existingCompany);
        }
        return null;
    }

    public CompanyModel updateThumb1Pic(CompanyModel company) {
        Optional<CompanyModel> companyModel = companyRepository.findById(company.getId());
        if (companyModel.isPresent()) {
            CompanyModel existingCompany = companyModel.get();
            existingCompany.setImage1(company.getImage1());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingCompany.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>(); // Initialize if null
            }
            profileCompleted.put("image1", company.getImage1() != null && !company.getImage1().isEmpty());
            existingCompany.setProfileCompleted(profileCompleted);

            return companyRepository.save(existingCompany);
        }
        return null;
    }

    public CompanyModel updateThumb2Pic(CompanyModel company) {
        Optional<CompanyModel> companyModel = companyRepository.findById(company.getId());
        if (companyModel.isPresent()) {
            CompanyModel existingCompany = companyModel.get();
            existingCompany.setImage2(company.getImage2());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingCompany.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>(); // Initialize if null
            }
            profileCompleted.put("image2", company.getImage2() != null && !company.getImage2().isEmpty());
            existingCompany.setProfileCompleted(profileCompleted);

            return companyRepository.save(existingCompany);
        }
        return null;
    }

    public CompanyModel updateThumb3Pic(CompanyModel company) {
        Optional<CompanyModel> companyModel = companyRepository.findById(company.getId());
        if (companyModel.isPresent()) {
            CompanyModel existingCompany = companyModel.get();
            existingCompany.setImage3(company.getImage3());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingCompany.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>(); // Initialize if null
            }
            profileCompleted.put("image3", company.getImage3() != null && !company.getImage3().isEmpty());
            existingCompany.setProfileCompleted(profileCompleted);

            return companyRepository.save(existingCompany);
        }
        return null;
    }

    public CompanyModel updateCompany(CompanyModel company) {
        Optional<CompanyModel> companyModel = companyRepository.findById(company.getId());
        if (companyModel.isPresent()) {
            CompanyModel existingCompany = companyModel.get();
            existingCompany.setName(company.getName());
            existingCompany.setShortDescription(company.getShortDescription());
            existingCompany.setCompanyStory(company.getCompanyStory());
            existingCompany.setCompanyLevel(company.getCompanyLevel());
            existingCompany.setLocation(company.getLocation());
            existingCompany.setFoundedDate(company.getFoundedDate());
            existingCompany.setFounderName(company.getFounderName());
            existingCompany.setHeadquarters(company.getHeadquarters());
            existingCompany.setNumberOfEmployees(company.getNumberOfEmployees());
            existingCompany.setWebsite(company.getWebsite());
            existingCompany.setSocialLinks(company.getSocialLinks());
            existingCompany.setContactEmail(company.getContactEmail());
            existingCompany.setContactNumber(company.getContactNumber());
            existingCompany.setIsVerified(company.getIsVerified());
            existingCompany.setPostedJobs(company.getPostedJobs());
            existingCompany.setJoinedDate(company.getJoinedDate());
            existingCompany.setFollowers(company.getFollowers());
            existingCompany.setFollowing(company.getFollowing());
            existingCompany.setAccountNotifications(company.getAccountNotifications());
            existingCompany.setMarketingNotifications(company.getMarketingNotifications());
            existingCompany.setProfileCompleted(company.getProfileCompleted());
            existingCompany.setProfileStatus(company.getProfileStatus());
            existingCompany.setCompanyType(company.getCompanyType());

            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingCompany.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>();
            }
            if (company.getName() != null && !company.getName().isEmpty()) {
                profileCompleted.put("name", true);
            }
            if (company.getContactEmail() != null && !company.getContactEmail().isEmpty()) {
                profileCompleted.put("email", true);
            }
            if (company.getCompanyStory() != null && !company.getCompanyStory().isEmpty()) {
                profileCompleted.put("story", true);
            }
            if (company.getFounderName() != null && !company.getFounderName().isEmpty()) {
                profileCompleted.put("founderName", true);
            }
            if (company.getFoundedDate() != null && !company.getFoundedDate().isEmpty()) {
                profileCompleted.put("foundedDate", true);
            }
            if (company.getLocation() != null && !company.getLocation().isEmpty()) {
                profileCompleted.put("location", true);
            }
            if (company.getNumberOfEmployees() != null && !company.getNumberOfEmployees().isEmpty()) {
                profileCompleted.put("numberOfEmployees", true);
            }
            if (company.getWebsite() != null && !company.getWebsite().isEmpty()) {
                profileCompleted.put("website", true);
            }

            existingCompany.setProfileCompleted(profileCompleted);

            return companyRepository.save(existingCompany);
        }
        return null;
    }

    public CompanyModel updateNotifications(CompanyModel company) {
        Optional<CompanyModel> companyModel = companyRepository.findById(company.getId());
        if (companyModel.isPresent()) {
            CompanyModel existingCompany = companyModel.get();
            if (existingCompany.getAccountNotifications() == null) {
                existingCompany.setAccountNotifications(new HashMap<>());
            }
            if (existingCompany.getMarketingNotifications() == null) {
                existingCompany.setMarketingNotifications(new HashMap<>());
            }
            existingCompany.setAccountNotifications(company.getAccountNotifications());
            existingCompany.setMarketingNotifications(company.getMarketingNotifications());
            return companyRepository.save(existingCompany);
        }
        return null;
    }

    public void deleteCompany(String id) {
        companyRepository.deleteById(id);
    }

    @Async
    public CompletableFuture<List<CompanyModel>> getAllCompaniesAsync() {
        List<CompanyModel> companies = getAllCompanies();
        return CompletableFuture.completedFuture(companies);
    }

    @Async
    public CompletableFuture<CompanyModel> getCompanyByIdAsync(String id) {
        CompanyModel company = getCompany(id);
        return CompletableFuture.completedFuture(company);
    }

    public void findAndUpdateCompanyLevel(String id, String companyLevel) {
        Optional<CompanyModel> companyModel = companyRepository.findById(id);
        if (companyModel.isPresent()) {
            CompanyModel existingCompany = companyModel.get();
            existingCompany.setCompanyLevel(companyLevel);
            companyRepository.save(existingCompany);
        }
    }
}
