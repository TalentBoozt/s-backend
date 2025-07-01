package com.talentboozt.s_backend.Repository.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.SwagItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SwagItemRepository extends MongoRepository<SwagItem, String> {
    Iterable<SwagItem> findByEnabled(boolean b);
}
