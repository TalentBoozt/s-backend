package com.talentboozt.s_backend.domains.portal.content.article.repository.mongodb;

import com.talentboozt.s_backend.domains.portal.content.article.model.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends MongoRepository<Tag, String> {
    Optional<Tag> findByName(String name);

    Optional<Tag> findBySlug(String slug);
}
