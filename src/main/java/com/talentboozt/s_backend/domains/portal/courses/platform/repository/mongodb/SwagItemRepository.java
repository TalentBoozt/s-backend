package com.talentboozt.s_backend.domains.portal.courses.platform.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.courses.platform.model.SwagItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SwagItemRepository extends MongoRepository<SwagItem, String> {
    Iterable<SwagItem> findByEnabled(boolean b);
}
