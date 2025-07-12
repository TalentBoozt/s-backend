package com.talentboozt.s_backend.domains.ambassador.controller;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorRewardModel;
import com.talentboozt.s_backend.domains.ambassador.service.AmbassadorRewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v2/ambassador/reward")
public class AmbassadorRewardController {

    @Autowired
    AmbassadorRewardService ambassadorRewardService;

    @PostMapping("/add/{ambassadorId}")
    public AmbassadorRewardModel addAmbassadorReward(@PathVariable String ambassadorId, @RequestBody AmbassadorRewardModel ambassadorRewardModel) {
        return ambassadorRewardService.addAmbassadorReward(ambassadorId, ambassadorRewardModel);
    }

    @GetMapping("/get/ambassador/{ambassadorId}")
    public Iterable<AmbassadorRewardModel> getAmbassadorRewards(@PathVariable String ambassadorId) {
        return ambassadorRewardService.getAmbassadorRewards(ambassadorId);
    }

    @GetMapping("/get/all")
    public Iterable<AmbassadorRewardModel> getAllAmbassadorRewards() {
        return ambassadorRewardService.getAllAmbassadorRewards();
    }

    @GetMapping("/get/{id}")
    public AmbassadorRewardModel getAmbassadorReward(@PathVariable String id) {
        return ambassadorRewardService.getAmbassadorReward(id);
    }

    @PutMapping("/update/status/{id}/{status}")
    public AmbassadorRewardModel updateAmbassadorRewardStatus(@PathVariable String id, @PathVariable String status) {
        return ambassadorRewardService.updateAmbassadorRewardStatus(id, status);
    }

    @PutMapping("/update/{id}")
    public AmbassadorRewardModel updateAmbassadorReward(@PathVariable String id, @RequestBody AmbassadorRewardModel ambassadorRewardModel) {
        return ambassadorRewardService.updateAmbassadorReward(id, ambassadorRewardModel);
    }

    @PutMapping("/{id}/reward")
    public AmbassadorRewardModel markAsShipped(@PathVariable String id) {
        return ambassadorRewardService.markAsShipped(id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAmbassadorReward(@PathVariable String id) {
        ambassadorRewardService.deleteAmbassadorReward(id);
        return ResponseEntity.ok().build();
    }
}
