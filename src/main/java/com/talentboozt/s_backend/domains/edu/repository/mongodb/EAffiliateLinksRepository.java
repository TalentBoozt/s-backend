package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.EAffiliateLinks;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EAffiliateLinksRepository extends MongoRepository<EAffiliateLinks, String> {
    Optional<EAffiliateLinks> findByTrackingCode(String trackingCode);
    List<EAffiliateLinks> findByAffiliateId(String affiliateId);
    Optional<EAffiliateLinks> findByAffiliateIdAndCourseId(String affiliateId, String courseId);
}
