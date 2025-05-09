package com.talentboozt.s_backend.Repository.common.auth;

import com.talentboozt.s_backend.Model.common.auth.RoleModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<RoleModel, String> {
    Optional<RoleModel> findByName(String name);
}
