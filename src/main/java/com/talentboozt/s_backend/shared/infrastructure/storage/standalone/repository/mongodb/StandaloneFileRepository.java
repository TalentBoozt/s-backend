package com.talentboozt.s_backend.shared.infrastructure.storage.standalone.repository.mongodb;

import com.talentboozt.s_backend.shared.infrastructure.storage.standalone.model.StandaloneFileModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandaloneFileRepository extends MongoRepository<StandaloneFileModel, String> {
    List<StandaloneFileModel> findByParentIdAndOwnerId(String parentId, String ownerId);

    List<StandaloneFileModel> findByOwnerId(String ownerId);

    void deleteByParentId(String parentId);
}
