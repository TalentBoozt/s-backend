package com.talentboozt.s_backend.Controller.AMBASSADOR;

import com.talentboozt.s_backend.Model.AMBASSADOR.AmbassadorSessionModel;
import com.talentboozt.s_backend.Service.AMBASSADOR.AmbassadorSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/ambassador/session")
public class AmbassadorSessionController {

    @Autowired
    private AmbassadorSessionService ambassadorSessionService;

    @PostMapping("/ambassador/{id}/session")
    public AmbassadorSessionModel addAmbassadorSession(@PathVariable String id, AmbassadorSessionModel ambassadorSessionModel) {
        return ambassadorSessionService.addAmbassadorSession(id, ambassadorSessionModel);
    }

    @GetMapping("/get/ambassador/{id}/session")
    public Iterable<AmbassadorSessionModel> getAmbassadorSessions(@PathVariable String id) {
        return ambassadorSessionService.getAmbassadorSessions(id);
    }

    @GetMapping("/get/session/{id}")
    public AmbassadorSessionModel getAmbassadorSession(@PathVariable String id) {
        return ambassadorSessionService.getAmbassadorSession(id);
    }

    @PutMapping("/update/session/{id}")
    public AmbassadorSessionModel updateAmbassadorSession(@PathVariable String id, @RequestBody AmbassadorSessionModel ambassadorSessionModel) {
        return ambassadorSessionService.updateAmbassadorSession(id, ambassadorSessionModel);
    }

    @DeleteMapping("/delete/session/{id}")
    public void deleteAmbassadorSession(@PathVariable String id) {
        ambassadorSessionService.deleteAmbassadorSession(id);
    }
}
