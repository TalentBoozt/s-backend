package com.talentboozt.s_backend.Controller.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.ReferralModel;
import com.talentboozt.s_backend.Service.AMBASSADOR.ReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/ambassador/referral")
public class ReferralController {

    @Autowired
    ReferralService referralService;

    @PostMapping("/add")
    public ReferralModel addReferral(@RequestBody ReferralModel referralModel) {
        return referralService.addReferral(referralModel);
    }

    @GetMapping("/get/ref/{referralCode}")
    public List<ReferralModel> getReferral(@PathVariable String referralCode) {
        return referralService.getReferral(referralCode);
    }

    @GetMapping("/get/ambassador/{ambassadorId}")
    public List<ReferralModel> getReferralByAmbassador(@PathVariable String ambassadorId) {
        return referralService.getReferralByAmbassador(ambassadorId);
    }

    @PutMapping("/update/{id}")
    public ReferralModel updateReferral(@PathVariable String id, @RequestBody ReferralModel referralModel) {
        return referralService.updateReferral(id, referralModel);
    }
}
