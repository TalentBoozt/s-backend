package com.talentboozt.s_backend.Repository;

import com.talentboozt.s_backend.Model.PasswordResetTokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetTokenModel, String> {
    Optional<PasswordResetTokenModel> findByToken(String token);
}
