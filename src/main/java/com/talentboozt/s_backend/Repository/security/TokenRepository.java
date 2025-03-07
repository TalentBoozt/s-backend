package com.talentboozt.s_backend.Repository.security;

import com.talentboozt.s_backend.Model.security.TokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<TokenModel, String> {
    Optional<TokenModel> findByToken(String token);
}
