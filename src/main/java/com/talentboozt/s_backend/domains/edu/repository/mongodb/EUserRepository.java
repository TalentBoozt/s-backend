package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EUser;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EUserRepository extends MongoRepository<EUser, String> {
    Optional<EUser> findByEmail(String email);
    Optional<EUser> findByEmailVerificationToken(String token);
    Optional<EUser> findByPasswordResetToken(String token);

    Page<EUser> findAllByEmailContainingIgnoreCaseOrDisplayNameContainingIgnoreCase(
            String email, String displayName, Pageable pageable);
}
