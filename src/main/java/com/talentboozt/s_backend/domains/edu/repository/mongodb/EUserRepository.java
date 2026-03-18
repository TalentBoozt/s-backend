package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EUser;

import java.util.Optional;

@Repository
public interface EUserRepository extends MongoRepository<EUser, String> {
    Optional<EUser> findByEmail(String email);
}
