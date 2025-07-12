package com.talentboozt.s_backend.domains.common.repository;

import com.talentboozt.s_backend.domains.common.model.SystemNotificationsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SystemNotificationsRepository extends MongoRepository<SystemNotificationsModel, String> {
    List<SystemNotificationsModel> findByActive(boolean active);
}
