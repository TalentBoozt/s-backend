package com.talentboozt.s_backend.domains.com_job_portal.controller;

import com.talentboozt.s_backend.domains.common.dto.SocialLinksDTO;
import com.talentboozt.s_backend.domains.com_job_portal.model.CmpSocialModel;
import com.talentboozt.s_backend.domains.com_job_portal.service.CmpSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/cmp_socials")
public class CmpSocialController {

    @Autowired
    CmpSocialService cmpSocialService;

    @GetMapping("/getByCompanyId/{companyId}")
    public List<CmpSocialModel> getCmpSocialsByCompanyId(@PathVariable String companyId) {
        return cmpSocialService.getCmpSocialsByCompanyId(companyId);
    }

    @PostMapping("/add")
    public CmpSocialModel addCmpSocials(@RequestBody CmpSocialModel cmpSocials) {
        return cmpSocialService.addCmpSocials(cmpSocials);
    }

    @PutMapping("/update/{id}")
    public CmpSocialModel updateCmpSocials(@PathVariable String id, @RequestBody CmpSocialModel cmpSocials) {
        return cmpSocialService.updateCmpSocials(id, cmpSocials);
    }

    @PutMapping("/edit-single/{companyId}")
    public CmpSocialModel updateCmpSocial(@PathVariable String companyId, @RequestBody SocialLinksDTO cmpSocials) {
        return cmpSocialService.editCmpSocial(companyId, cmpSocials);
    }

    @DeleteMapping("/delete/{companyId}")
    public void deleteCmpSocials(@PathVariable String companyId) {
        cmpSocialService.deleteCmpSocials(companyId);
    }
}
