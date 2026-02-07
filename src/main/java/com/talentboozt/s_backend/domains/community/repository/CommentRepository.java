package com.talentboozt.s_backend.domains.community.repository;

import com.talentboozt.s_backend.domains.community.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId);

    List<Comment> findByParentId(String parentId);
}
