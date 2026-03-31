package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.edu.model.ECouponRedemption;
import java.util.List;
import java.util.Optional;

@Repository
public interface ECouponRedemptionRepository extends MongoRepository<ECouponRedemption, String> {
    List<ECouponRedemption> findByUserId(String userId);
    List<ECouponRedemption> findByCouponId(String couponId);
    long countByUserIdAndCouponId(String userId, String couponId);
    Optional<ECouponRedemption> findByUserIdAndCouponId(String userId, String couponId);
}
