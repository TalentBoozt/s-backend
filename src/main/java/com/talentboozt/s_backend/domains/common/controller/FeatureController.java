package com.talentboozt.s_backend.domains.common.controller;

import com.talentboozt.s_backend.domains.common.model.FeatureModel;
import com.talentboozt.s_backend.domains.common.service.FeatureService;
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

    @DeleteMapping("/delete/{id}")
    public void deleteFeature(@PathVariable String id) {
        featureService.deleteFeature(id);
    }
}
