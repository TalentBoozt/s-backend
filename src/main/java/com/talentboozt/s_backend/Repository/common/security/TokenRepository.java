package com.talentboozt.s_backend.Repository.common.security;

import com.talentboozt.s_backend.Model.common.security.TokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<TokenModel, String> {
    Optional<TokenModel> findByToken(String token);
}
