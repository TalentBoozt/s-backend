package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.EWorkspaces;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface EWorkspacesRepository extends MongoRepository<EWorkspaces, String> {
    List<EWorkspaces> findByOwnerId(String ownerId);
    Page<EWorkspaces> findAllByNameContainingIgnoreCaseOrDomainContainingIgnoreCase(String name, String domain, Pageable pageable);
}
