package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.ECertificates;
import java.util.List;
import java.util.Optional;

@Repository
public interface ECertificatesRepository extends MongoRepository<ECertificates, String> {
    Optional<ECertificates> findByCertificateId(String certificateId);
    List<ECertificates> findByUserId(String userId);
}
