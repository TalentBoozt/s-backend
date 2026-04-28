package com.talentboozt.s_backend.domains.ambassador.controller;

import com.talentboozt.s_backend.domains.ambassador.model.AmbReferralModel;
import com.talentboozt.s_backend.domains.ambassador.service.AmbReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/ambassador/referral")
public class AmbReferralController {

    @Autowired
    AmbReferralService referralService;

    @PostMapping("/add")
    public AmbReferralModel addReferral(@RequestBody AmbReferralModel referralModel) {
        return referralService.addReferral(referralModel);
    }

    @GetMapping("/get/ref/{referralCode}")
    public List<AmbReferralModel> getReferral(@PathVariable String referralCode) {
        return referralService.getReferral(referralCode);
    }

    @GetMapping("/get/ambassador/{ambassadorId}")
    public List<AmbReferralModel> getReferralByAmbassador(@PathVariable String ambassadorId) {
        return referralService.getReferralByAmbassador(ambassadorId);
    }

    @PutMapping("/update/{id}")
    public AmbReferralModel updateReferral(@PathVariable String id, @RequestBody AmbReferralModel referralModel) {
        return referralService.updateReferral(id, referralModel);
    }
}
