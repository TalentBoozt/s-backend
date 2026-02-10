package com.talentboozt.s_backend.domains.community.repository;

import com.talentboozt.s_backend.domains.community.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByCommunityId(String communityId);

    List<Post> findByAuthorId(String authorId);

    List<Post> findByTimestampAfter(java.time.LocalDateTime timestamp);

    @org.springframework.data.mongodb.repository.Query("{ '$or': [ { 'content.title': { '$regex': ?0, '$options': 'i' } }, { 'content.text': { '$regex': ?0, '$options': 'i' } } ] }")
    org.springframework.data.domain.Page<Post> searchPosts(String regex,
            org.springframework.data.domain.Pageable pageable);
}
