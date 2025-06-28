package com.talentboozt.s_backend.Controller.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorRewardModel;
import com.talentboozt.s_backend.Service.AMBASSADOR.AmbassadorRewardService;
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAmbassadorReward(@PathVariable String id) {
        ambassadorRewardService.deleteAmbassadorReward(id);
        return ResponseEntity.ok().build();
    }
}
