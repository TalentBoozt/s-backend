package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EGifts;
import java.util.Optional;

@Repository
public interface EGiftsRepository extends MongoRepository<EGifts, String> {
    Optional<EGifts> findByRedeemCode(String redeemCode);
}
