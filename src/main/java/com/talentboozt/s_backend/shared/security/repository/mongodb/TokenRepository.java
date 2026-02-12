package com.talentboozt.s_backend.shared.security.repository.mongodb;

import com.talentboozt.s_backend.shared.security.model.TokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<TokenModel, String> {
    Optional<TokenModel> findByToken(String token);
}
