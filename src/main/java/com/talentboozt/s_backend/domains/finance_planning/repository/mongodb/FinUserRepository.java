package com.talentboozt.s_backend.domains.finance_planning.repository.mongodb;

import com.talentboozt.s_backend.domains.finance_planning.models.FinUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface FinUserRepository extends MongoRepository<FinUser, String> {
    Optional<FinUser> findByEmail(String email);
}
