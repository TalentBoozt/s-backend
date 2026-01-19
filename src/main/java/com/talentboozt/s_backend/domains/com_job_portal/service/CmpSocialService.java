package com.talentboozt.s_backend.domains.com_job_portal.service;

import com.talentboozt.s_backend.domains.common.dto.SocialLinksDTO;
import com.talentboozt.s_backend.domains.com_job_portal.model.CmpSocialModel;
import com.talentboozt.s_backend.domains.com_job_portal.model.CompanyModel;
import com.talentboozt.s_backend.domains.com_job_portal.repository.CmpSocialRepository;
import com.talentboozt.s_backend.domains.com_job_portal.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CmpSocialService {

    @Autowired
    CmpSocialRepository cmpSocialRepository;

    @Autowired
    CompanyRepository companyRepository;

    public List<CmpSocialModel> getCmpSocialsByCompanyId(String companyId) {
        return cmpSocialRepository.findByCompanyId(companyId);
    }

    public CmpSocialModel addCmpSocials(CmpSocialModel cmpSocials) {
        List<CmpSocialModel> cmpSocialsList = cmpSocialRepository.findByCompanyId(cmpSocials.getCompanyId());

        CmpSocialModel cmpSocialsModel;

        if (!cmpSocialsList.isEmpty()) {
            // Use the first CmpSocialModel if it exists (assuming only one entry should exist)
            cmpSocialsModel = cmpSocialsList.get(0);

            // Append the new cmpSocials from the incoming cmpSocials object
            cmpSocialsModel.setSocialLinks(cmpSocials.getSocialLinks());
        } else {
            // If no cmpSocials exist, create a new CmpSocialModel entry
            cmpSocialsModel = cmpSocialRepository.save(cmpSocials);
        }

        // Save the updated CmpSocialModel to the repository
        cmpSocialRepository.save(cmpSocialsModel);

        Optional<CompanyModel> companyModel = companyRepository.findById(Objects.requireNonNull(cmpSocials.getCompanyId()));

        if (companyModel.isPresent()) {
            CompanyModel existingCompany = companyModel.get();
            existingCompany.setSocialLinks(cmpSocialsModel.getId());
            Map<String, Boolean> profileCompleted = (Map<String, Boolean>) existingCompany.getProfileCompleted();
            if (profileCompleted == null) {
                profileCompleted = new HashMap<>();
            }
            profileCompleted.put("cmpSocial", true);
            existingCompany.setProfileCompleted(profileCompleted);
            companyRepository.save(existingCompany);
        }
        return cmpSocialsModel;
    }

    public CmpSocialModel updateCmpSocials(String id, CmpSocialModel cmpSocials) {
        CmpSocialModel cmpSocialsModel = cmpSocialRepository.findById(Objects.requireNonNull(id)).orElse(null);
        if (cmpSocialsModel != null) {
            cmpSocialsModel.setCompanyId(cmpSocials.getCompanyId());
            cmpSocialsModel.setSocialLinks(cmpSocials.getSocialLinks());
            return cmpSocialRepository.save(cmpSocialsModel);
        }
        return null;
    }

    public CmpSocialModel editCmpSocial(String companyId, SocialLinksDTO updatedCmpSocials) {
        List<CmpSocialModel> cmpSocialsList = cmpSocialRepository.findByCompanyId(companyId);

        if (!cmpSocialsList.isEmpty()) {
            CmpSocialModel cmpSocialsModel = cmpSocialsList.get(0);

            List<SocialLinksDTO> links = cmpSocialsModel.getSocialLinks();
            if (links != null) {
                for (SocialLinksDTO link : links) {
                    if (link.getId().equals(updatedCmpSocials.getId())) {
                        link.setFacebook(updatedCmpSocials.getFacebook());
                        link.setTwitter(updatedCmpSocials.getTwitter());
                        link.setLinkedin(updatedCmpSocials.getLinkedin());
                        link.setGithub(updatedCmpSocials.getGithub());
                        link.setInstagram(updatedCmpSocials.getInstagram());
                        break;
                    }
                }
                cmpSocialsModel.setSocialLinks(links);

                return cmpSocialRepository.save(cmpSocialsModel);
            }
            return cmpSocialsModel;
        }
        throw new RuntimeException("CmpSocials not found for companyId: " + companyId);
    }

    public void deleteCmpSocials(String companyId) {
        cmpSocialRepository.deleteByCompanyId(companyId);
    }

    @Async
    public CompletableFuture<List<CmpSocialModel>> getCmpSocialsByCompanyIdAsync(String companyId) {
        List<CmpSocialModel> cmpSocialsList = cmpSocialRepository.findByCompanyId(companyId);

        return CompletableFuture.completedFuture(cmpSocialsList);
    }
}
