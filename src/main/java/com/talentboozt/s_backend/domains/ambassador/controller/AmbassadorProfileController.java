package com.talentboozt.s_backend.domains.ambassador.controller;

import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.service.AmbassadorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/ambassador/profile")
public class AmbassadorProfileController {

    @Autowired
    private AmbassadorProfileService ambassadorProfileService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyAsAmbassador(@RequestBody AmbassadorProfileModel request) {
        AmbassadorProfileModel ambassadorProfileModel = ambassadorProfileService.applyAmbassador(request);

        if (ambassadorProfileModel.getId() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse("Something went wrong"));
        }
        return ResponseEntity.ok(new ApiResponse("Successfully applied as ambassador"));
    }

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

    @PutMapping("/approve/{id}")
    public AmbassadorProfileModel approveAmbassadorProfile(@PathVariable String id) {
        return ambassadorProfileService.approveAmbassadorProfile(id);
    }

    @PutMapping("/reject/{id}")
    public AmbassadorProfileModel rejectAmbassadorProfile(@PathVariable String id) {
        return ambassadorProfileService.rejectAmbassadorProfile(id);
    }

    @PutMapping("/suspend/{id}")
    public AmbassadorProfileModel suspendAmbassadorProfile(@PathVariable String id) {
        return ambassadorProfileService.suspendAmbassadorProfile(id);
    }

    @PutMapping("/update/application")
    public AmbassadorProfileModel applicationAcceptance(@RequestBody AmbassadorProfileModel ambassadorProfileModel) {
        return ambassadorProfileService.applicationAcceptance(ambassadorProfileModel);
    }

    @PutMapping("/promote/{id}")
    public AmbassadorProfileModel promoteAmbassador(@PathVariable String id) {
        return ambassadorProfileService.promoteAmbassador(id);
    }

    @PutMapping("/demote/{id}")
    public AmbassadorProfileModel demoteAmbassador(@PathVariable String id) {
        return ambassadorProfileService.demoteAmbassador(id);
    }
}
