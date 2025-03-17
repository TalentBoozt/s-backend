package com.talentboozt.s_backend.Repository.common;

import com.talentboozt.s_backend.Model.common.SystemNotificationsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SystemNotificationsRepository extends MongoRepository<SystemNotificationsModel, String> {
    List<SystemNotificationsModel> findByActive(boolean active);
}
