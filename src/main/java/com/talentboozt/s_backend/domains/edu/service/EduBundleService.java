package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.EBundles;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EBundlesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EduBundleService {

    private final EBundlesRepository bundlesRepository;
    private final ECoursesRepository coursesRepository;

    public EBundles createBundle(String creatorId, EBundles request) {
        request.setCreatorId(creatorId);
        request.setCreatedAt(Instant.now());
        if (request.getStatus() == null) {
            request.setStatus("ACTIVE");
        }
        if (request.getTotalSales() == null) {
            request.setTotalSales(0);
        }
        
        calculatePricing(request);
        return bundlesRepository.save(request);
    }

    public List<EBundles> getBundlesByCreator(String creatorId) {
        return bundlesRepository.findByCreatorId(creatorId);
    }

    public EBundles updateBundle(String bundleId, String creatorId, EBundles request) {
        EBundles existing = bundlesRepository.findById(bundleId)
                .orElseThrow(() -> new RuntimeException("Bundle not found."));

        if (!existing.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Not authorized to edit this bundle.");
        }

        if (request.getName() != null) existing.setName(request.getName());
        if (request.getCourseIds() != null) {
            existing.setCourseIds(request.getCourseIds());
        }
        if (request.getBundlePrice() != null) existing.setBundlePrice(request.getBundlePrice());
        if (request.getStatus() != null) existing.setStatus(request.getStatus());

        calculatePricing(existing);
        return bundlesRepository.save(existing);
    }

    public void deleteBundle(String bundleId, String creatorId) {
        EBundles existing = bundlesRepository.findById(bundleId)
                .orElseThrow(() -> new RuntimeException("Bundle not found."));
        if (!existing.getCreatorId().equals(creatorId)) {
            throw new RuntimeException("Not authorized to delete this bundle.");
        }
        bundlesRepository.deleteById(bundleId);
    }

    private void calculatePricing(EBundles bundle) {
        if (bundle.getCourseIds() != null && bundle.getCourseIds().length > 0) {
            double originalTotal = 0.0;
            for (String cid : bundle.getCourseIds()) {
                ECourses course = coursesRepository.findById(cid).orElse(null);
                if (course != null && course.getPrice() != null) {
                    originalTotal += course.getPrice();
                }
            }
            bundle.setOriginalTotalPrice(originalTotal);

            if (bundle.getBundlePrice() != null && originalTotal > 0) {
                double diff = originalTotal - bundle.getBundlePrice();
                double percent = (diff / originalTotal) * 100;
                bundle.setSavingsPercent(Math.max(0, percent));
            } else {
                bundle.setSavingsPercent(0.0);
            }
        } else {
            bundle.setOriginalTotalPrice(0.0);
            bundle.setSavingsPercent(0.0);
        }
    }
}
