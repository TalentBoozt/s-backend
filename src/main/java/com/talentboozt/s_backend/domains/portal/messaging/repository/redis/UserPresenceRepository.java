package com.talentboozt.s_backend.domains.portal.messaging.repository.redis;

import com.talentboozt.s_backend.domains.portal.messaging.model.UserPresence;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPresenceRepository extends CrudRepository<UserPresence, String> {
}
