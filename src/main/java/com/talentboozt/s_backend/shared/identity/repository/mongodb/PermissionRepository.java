package com.talentboozt.s_backend.shared.identity.repository.mongodb;

import com.talentboozt.s_backend.shared.identity.model.PermissionModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends MongoRepository<PermissionModel, String> {
    Optional<PermissionModel> findByName(String name);
    boolean existsByName(String name);

    long count();
}
