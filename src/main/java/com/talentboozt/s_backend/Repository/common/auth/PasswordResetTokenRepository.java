package com.talentboozt.s_backend.Repository.common.auth;

import com.talentboozt.s_backend.Model.common.auth.PasswordResetTokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetTokenModel, String> {
    Optional<PasswordResetTokenModel> findByToken(String token);
}
