package com.talentboozt.s_backend.shared.identity.repository.mongodb;

import com.talentboozt.s_backend.shared.identity.model.PasswordResetTokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetTokenModel, String> {
    Optional<PasswordResetTokenModel> findByToken(String token);
}
