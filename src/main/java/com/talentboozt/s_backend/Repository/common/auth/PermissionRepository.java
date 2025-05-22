package com.talentboozt.s_backend.Repository.common.auth;

import com.talentboozt.s_backend.Model.common.auth.PermissionModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends MongoRepository<PermissionModel, String> {
    Optional<PermissionModel> findByName(String name);
    boolean existsByName(String name);

    long count();
}
