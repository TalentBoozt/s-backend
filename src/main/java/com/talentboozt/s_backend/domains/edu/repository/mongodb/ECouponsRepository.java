package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.ECoupons;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ECouponsRepository extends MongoRepository<ECoupons, String> {
    List<ECoupons> findByCreatorId(String creatorId);
    List<ECoupons> findByCreatorIdAndIsActiveTrue(String creatorId);
    Optional<ECoupons> findByCode(String code);
}
