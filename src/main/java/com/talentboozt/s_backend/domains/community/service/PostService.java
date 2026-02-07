package com.talentboozt.s_backend.domains.community.service;

import com.talentboozt.s_backend.domains.community.dto.PostDTO;
import com.talentboozt.s_backend.domains.community.dto.CommentDTO;
import java.util.List;

import org.springframework.data.domain.Pageable;

public interface PostService {
    List<PostDTO> getAllPosts(Pageable pageable);

    List<PostDTO> getPostsByCommunity(String communityId);

    List<PostDTO> getPostsByAuthor(String authorId);

    PostDTO getPostById(String id);

    PostDTO createPost(PostDTO postDTO);

    void deletePost(String id);

    PostDTO reactToPost(String id, String emoji, String userId);

    List<CommentDTO> getComments(String postId);

    CommentDTO addComment(String postId, CommentDTO commentDTO);
}
