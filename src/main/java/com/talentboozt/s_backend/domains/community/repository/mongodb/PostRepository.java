package com.talentboozt.s_backend.domains.community.repository.mongodb;

import com.talentboozt.s_backend.domains.community.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByCommunityId(String communityId);

    Page<Post> findByCommunityId(String communityId, Pageable pageable);

    List<Post> findByAuthorId(String authorId);

    Page<Post> findByAuthorId(String authorId, Pageable pageable);

    List<Post> findByTimestampAfter(java.time.LocalDateTime timestamp);

    @Query("{ $text: { $search: ?0 } }")
    Page<Post> searchPosts(String query, Pageable pageable);
}
