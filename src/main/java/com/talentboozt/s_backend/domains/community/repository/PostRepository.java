package com.talentboozt.s_backend.domains.community.repository;

import com.talentboozt.s_backend.domains.community.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByCommunityId(String communityId);

    List<Post> findByAuthorId(String authorId);
}
