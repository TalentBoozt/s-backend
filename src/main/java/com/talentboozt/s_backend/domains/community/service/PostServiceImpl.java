package com.talentboozt.s_backend.domains.community.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.talentboozt.s_backend.domains.community.dto.CommentDTO;
import com.talentboozt.s_backend.domains.community.dto.PostDTO;
import com.talentboozt.s_backend.domains.community.model.Comment;
import com.talentboozt.s_backend.domains.community.model.Post;
import com.talentboozt.s_backend.domains.community.repository.CommentRepository;
import com.talentboozt.s_backend.domains.community.repository.PostRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getPostsByCommunity(String communityId) {
        return postRepository.findByCommunityId(communityId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDTO> getPostsByAuthor(String authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PostDTO getPostById(String id) {
        return postRepository.findById(Objects.requireNonNull(id))
                .map(this::mapToDTO)
                .orElse(null);
    }

    @Override
    public PostDTO createPost(PostDTO postDTO) {
        Post post = mapToEntity(postDTO);
        post.setTimestamp(LocalDateTime.now());
        post.setMetrics(Post.PostMetrics.builder()
                .upvotes(0)
                .downvotes(0)
                .comments(0)
                .shares(0)
                .build());
        post.setReactions(new ArrayList<>());

        Post savedPost = postRepository.save(post);
        return mapToDTO(savedPost);
    }

    @Override
    public void deletePost(String id) {
        postRepository.deleteById(Objects.requireNonNull(id));
    }

    @Override
    public PostDTO reactToPost(String id, String emoji, String userId) {
        Post post = postRepository.findById(Objects.requireNonNull(id)).orElse(null);
        if (post == null)
            return null;

        List<Post.Reaction> reactions = post.getReactions();
        if (reactions == null)
            reactions = new ArrayList<>();

        boolean found = false;
        for (Post.Reaction r : reactions) {
            if (r.getEmoji().equals(emoji)) {
                r.setCount(r.getCount() + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            reactions.add(Post.Reaction.builder().emoji(emoji).count(1).build());
        }

        post.setReactions(reactions);
        return mapToDTO(postRepository.save(post));
    }

    @Override
    public List<CommentDTO> getComments(String postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::mapToCommentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO addComment(String postId, CommentDTO commentDTO) {
        Comment comment = Comment.builder()
                .postId(postId)
                .parentId(commentDTO.getParentId())
                .authorId(commentDTO.getAuthorId())
                .text(commentDTO.getText())
                .timestamp(LocalDateTime.now())
                .reactions(new ArrayList<>())
                .build();

        return mapToCommentDTO(commentRepository.save(Objects.requireNonNull(comment)));
    }

    private PostDTO mapToDTO(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .communityId(post.getCommunityId())
                .type(post.getType())
                .content(PostDTO.PostContentDTO.builder()
                        .title(post.getContent().getTitle())
                        .text(post.getContent().getText())
                        .url(post.getContent().getUrl())
                        .media(post.getContent().getMedia())
                        .tags(post.getContent().getTags())
                        .build())
                .metrics(PostDTO.PostMetricsDTO.builder()
                        .upvotes(post.getMetrics().getUpvotes())
                        .downvotes(post.getMetrics().getDownvotes())
                        .comments(post.getMetrics().getComments())
                        .shares(post.getMetrics().getShares())
                        .build())
                .reactions(post.getReactions() != null ? post.getReactions().stream()
                        .map(r -> PostDTO.ReactionDTO.builder()
                                .emoji(r.getEmoji())
                                .count(r.getCount())
                                .userReacted(false)
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .timestamp(post.getTimestamp() != null ? post.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME)
                        : null)
                .build();
    }

    private CommentDTO mapToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .parentId(comment.getParentId())
                .authorId(comment.getAuthorId())
                .text(comment.getText())
                .reactions(comment.getReactions() != null ? comment.getReactions().stream()
                        .map(r -> PostDTO.ReactionDTO.builder()
                                .emoji(r.getEmoji())
                                .count(r.getCount())
                                .userReacted(false)
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .timestamp(
                        comment.getTimestamp() != null ? comment.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME)
                                : null)
                .build();
    }

    private Post mapToEntity(PostDTO dto) {
        return Post.builder()
                .authorId(dto.getAuthorId())
                .communityId(dto.getCommunityId())
                .type(dto.getType())
                .content(Post.PostContent.builder()
                        .title(dto.getContent().getTitle())
                        .text(dto.getContent().getText())
                        .url(dto.getContent().getUrl())
                        .media(dto.getContent().getMedia())
                        .tags(dto.getContent().getTags())
                        .build())
                .build();
    }
}
