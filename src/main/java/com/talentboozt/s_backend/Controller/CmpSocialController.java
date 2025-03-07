package com.talentboozt.s_backend.Controller;

import com.talentboozt.s_backend.DTO.SocialLinksDTO;
import com.talentboozt.s_backend.Model.CmpSocialModel;
import com.talentboozt.s_backend.Service.CmpSocialService;
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
