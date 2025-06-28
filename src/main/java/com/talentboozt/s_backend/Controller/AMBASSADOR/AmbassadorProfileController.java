package com.talentboozt.s_backend.Controller.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorProfileModel;
import com.talentboozt.s_backend.Service.AMBASSADOR.AmbassadorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/ambassador/profile")
public class AmbassadorProfileController {

    @Autowired
    private AmbassadorProfileService ambassadorProfileService;

    @PostMapping("/ambassador")
    public AmbassadorProfileModel addAmbassadorProfile(@RequestBody AmbassadorProfileModel ambassadorProfileModel) {
        return ambassadorProfileService.addAmbassadorProfile(ambassadorProfileModel);
    }

    @GetMapping("/get/{id}")
    public AmbassadorProfileModel getAmbassadorProfile(@PathVariable String id) {
        return ambassadorProfileService.getAmbassadorProfile(id);
    }

    @GetMapping("/get/all")
    public Iterable<AmbassadorProfileModel> getAllAmbassadorProfiles() {
        return ambassadorProfileService.getAllAmbassadorProfiles();
    }

    @PutMapping("/update/{id}")
    public AmbassadorProfileModel updateAmbassadorProfile(@PathVariable String id, @RequestBody AmbassadorProfileModel ambassadorProfileModel) {
        return ambassadorProfileService.updateAmbassadorProfile(id, ambassadorProfileModel);
    }
}
