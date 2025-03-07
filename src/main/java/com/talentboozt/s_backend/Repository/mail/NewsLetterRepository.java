package com.talentboozt.s_backend.Repository.mail;

import com.talentboozt.s_backend.Model.mail.NewsLatterModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NewsLetterRepository extends MongoRepository<NewsLatterModel, String> {
    Optional<NewsLatterModel> findByEmail(String email);
}
