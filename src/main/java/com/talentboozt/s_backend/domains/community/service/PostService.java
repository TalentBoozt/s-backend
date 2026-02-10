package com.talentboozt.s_backend.domains.community.service;

import com.talentboozt.s_backend.domains.community.dto.PostDTO;
import com.talentboozt.s_backend.domains.community.dto.CommentDTO;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface PostService {
    List<PostDTO> getAllPosts(Pageable pageable, String sort);

    List<PostDTO> searchPosts(String query, Pageable pageable);

    List<PostDTO> getPostsByCommunity(String communityId);

    List<PostDTO> getPostsByAuthor(String authorId);

    PostDTO getPostById(String id);

    PostDTO createPost(PostDTO postDTO);

    void deletePost(String id);

    PostDTO updatePost(String id, PostDTO postDTO);

    PostDTO reactToPost(String id, String emoji, String userId);

    Page<CommentDTO> getComments(String postId, Pageable pageable);

    CommentDTO addComment(String postId, CommentDTO commentDTO);

    void deleteComment(String postId, String commentId);

    CommentDTO updateComment(String postId, String commentId, CommentDTO commentDTO);

    CommentDTO reactToComment(String commentId, String emoji, String userId);
}
