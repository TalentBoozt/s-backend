package com.talentboozt.s_backend.domains.plat_courses.repository;

import com.talentboozt.s_backend.domains.plat_courses.model.SwagItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SwagItemRepository extends MongoRepository<SwagItem, String> {
    Iterable<SwagItem> findByEnabled(boolean b);
}
