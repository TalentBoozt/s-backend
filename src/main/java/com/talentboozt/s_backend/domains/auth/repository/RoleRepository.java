package com.talentboozt.s_backend.domains.auth.repository;

import com.talentboozt.s_backend.domains.auth.model.RoleModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<RoleModel, String> {
    Optional<RoleModel> findByName(String name);
    boolean existsByName(String name);

    long count();
}
