package com.talentboozt.s_backend.Repository.common;

import com.talentboozt.s_backend.Model.common.Login;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface LoginRepository extends MongoRepository<Login, String> {
    Optional<Login> findByUserId(String userId);
}
