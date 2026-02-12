package com.talentboozt.s_backend.shared.mail.repository.mongodb;

import com.talentboozt.s_backend.shared.mail.model.NewsLatterModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NewsLetterRepository extends MongoRepository<NewsLatterModel, String> {
    Optional<NewsLatterModel> findByEmail(String email);
}
