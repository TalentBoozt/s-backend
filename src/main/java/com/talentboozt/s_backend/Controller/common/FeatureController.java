package com.talentboozt.s_backend.Controller.common;

import com.talentboozt.s_backend.Model.common.FeatureModel;
import com.talentboozt.s_backend.Service.common.FeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/portal_report-features")
public class FeatureController {
    @Autowired
    private FeatureService featureService;

    @PostMapping("/add")
    public FeatureModel addFeature(@RequestBody FeatureModel feature) {
        return featureService.addFeature(feature);
    }

    @GetMapping("/all")
    public List<FeatureModel> getAllFeatures() {
        return featureService.getAllFeatures();
    }

    @PutMapping("/update/updateAttachment")
    public FeatureModel updateAttachment(@RequestBody FeatureModel feature) {
        return featureService.updateAttachment(feature);
    }
}
