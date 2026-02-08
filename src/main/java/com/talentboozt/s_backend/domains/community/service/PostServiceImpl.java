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
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<PostDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(Objects.requireNonNull(pageable)).stream()
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

        Post.Reaction targetReaction = null;
        for (Post.Reaction r : reactions) {
            if (r.getEmoji().equals(emoji)) {
                targetReaction = r;
                break;
            }
        }

        if (targetReaction == null) {
            targetReaction = Post.Reaction.builder()
                    .emoji(emoji)
                    .count(0)
                    .userIds(new ArrayList<>())
                    .build();
            reactions.add(targetReaction);
        }

        List<String> userIds = targetReaction.getUserIds();
        if (userIds == null)
            userIds = new ArrayList<>();

        if (userIds.contains(userId)) {
            userIds.remove(userId);
            targetReaction.setCount(userIds.size());
            // Update metrics
            if (emoji.equals("👍"))
                post.getMetrics().setUpvotes(Math.max(0, post.getMetrics().getUpvotes() - 1));
            if (emoji.equals("👎"))
                post.getMetrics().setDownvotes(Math.max(0, post.getMetrics().getDownvotes() - 1));
        } else {
            // Remove previous vote if it's an up/down vote
            if (emoji.equals("👍") || emoji.equals("👎")) {
                String opposite = emoji.equals("👍") ? "👎" : "👍";
                for (Post.Reaction r : reactions) {
                    if (r.getEmoji().equals(opposite) && r.getUserIds() != null
                            && r.getUserIds().contains(userId)) {
                        r.getUserIds().remove(userId);
                        r.setCount(r.getUserIds().size());
                        if (opposite.equals("👍"))
                            post.getMetrics().setUpvotes(Math.max(0,
                                    post.getMetrics().getUpvotes() - 1));
                        if (opposite.equals("👎"))
                            post.getMetrics().setDownvotes(Math.max(0,
                                    post.getMetrics().getDownvotes() - 1));
                        break;
                    }
                }
            }

            userIds.add(userId);
            targetReaction.setCount(userIds.size());
            // Update metrics
            if (emoji.equals("👍"))
                post.getMetrics().setUpvotes(post.getMetrics().getUpvotes() + 1);
            if (emoji.equals("👎"))
                post.getMetrics().setDownvotes(post.getMetrics().getDownvotes() + 1);
        }

        targetReaction.setUserIds(userIds);
        // Remove empty reactions to keep it clean
        reactions.removeIf(r -> r.getCount() <= 0 && !r.getEmoji().equals("👍") && !r.getEmoji().equals("👎"));

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
                .upvotes(0)
                .downvotes(0)
                .build();

        Comment savedComment = commentRepository.save(Objects.requireNonNull(comment));

        // Increment post comment count
        Post post = postRepository.findById(Objects.requireNonNull(postId)).orElse(null);
        if (post != null) {
            post.getMetrics().setComments(post.getMetrics().getComments() + 1);
            postRepository.save(post);
        }

        return mapToCommentDTO(savedComment);
    }

    @Override
    public CommentDTO reactToComment(String commentId, String emoji, String userId) {
        Comment comment = commentRepository.findById(Objects.requireNonNull(commentId)).orElse(null);
        if (comment == null)
            return null;

        List<Post.Reaction> reactions = comment.getReactions();
        if (reactions == null)
            reactions = new ArrayList<>();

        Post.Reaction targetReaction = null;
        for (Post.Reaction r : reactions) {
            if (r.getEmoji().equals(emoji)) {
                targetReaction = r;
                break;
            }
        }

        if (targetReaction == null) {
            targetReaction = Post.Reaction.builder()
                    .emoji(emoji)
                    .count(0)
                    .userIds(new ArrayList<>())
                    .build();
            reactions.add(targetReaction);
        }

        List<String> userIds = targetReaction.getUserIds();
        if (userIds == null)
            userIds = new ArrayList<>();

        if (userIds.contains(userId)) {
            userIds.remove(userId);
            targetReaction.setCount(userIds.size());
            if (emoji.equals("👍"))
                comment.setUpvotes(Math.max(0, comment.getUpvotes() - 1));
            if (emoji.equals("👎"))
                comment.setDownvotes(Math.max(0, comment.getDownvotes() - 1));
        } else {
            if (emoji.equals("👍") || emoji.equals("👎")) {
                String opposite = emoji.equals("👍") ? "👎" : "👍";
                for (Post.Reaction r : reactions) {
                    if (r.getEmoji().equals(opposite) && r.getUserIds() != null
                            && r.getUserIds().contains(userId)) {
                        r.getUserIds().remove(userId);
                        r.setCount(r.getUserIds().size());
                        if (opposite.equals("👍"))
                            comment.setUpvotes(Math.max(0, comment.getUpvotes() - 1));
                        if (opposite.equals("👎"))
                            comment.setDownvotes(Math.max(0, comment.getDownvotes() - 1));
                        break;
                    }
                }
            }
            userIds.add(userId);
            targetReaction.setCount(userIds.size());
            if (emoji.equals("👍"))
                comment.setUpvotes(comment.getUpvotes() + 1);
            if (emoji.equals("👎"))
                comment.setDownvotes(comment.getDownvotes() + 1);
        }

        targetReaction.setUserIds(userIds);
        reactions.removeIf(r -> r.getCount() <= 0 && !r.getEmoji().equals("👍") && !r.getEmoji().equals("👎"));
        comment.setReactions(reactions);
        return mapToCommentDTO(commentRepository.save(comment));
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
                        .linkPreview(post.getContent().getLinkPreview() != null
                                ? PostDTO.LinkPreviewDTO.builder()
                                        .title(post.getContent()
                                                .getLinkPreview()
                                                .getTitle())
                                        .description(post.getContent()
                                                .getLinkPreview()
                                                .getDescription())
                                        .image(post.getContent()
                                                .getLinkPreview()
                                                .getImage())
                                        .siteName(post.getContent()
                                                .getLinkPreview()
                                                .getSiteName())
                                        .build()
                                : null)
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
                                .userIds(r.getUserIds() != null
                                        ? new ArrayList<>(r.getUserIds())
                                        : new ArrayList<>())
                                .userReacted(false)
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .timestamp(post.getTimestamp() != null
                        ? post.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME)
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
                .upvotes(comment.getUpvotes())
                .downvotes(comment.getDownvotes())
                .reactions(comment.getReactions() != null ? comment.getReactions().stream()
                        .map(r -> PostDTO.ReactionDTO.builder()
                                .emoji(r.getEmoji())
                                .count(r.getCount())
                                .userIds(r.getUserIds() != null
                                        ? new ArrayList<>(r.getUserIds())
                                        : new ArrayList<>())
                                .userReacted(false)
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .timestamp(
                        comment.getTimestamp() != null
                                ? comment.getTimestamp()
                                        .format(DateTimeFormatter.ISO_DATE_TIME)
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
                        .linkPreview(dto.getContent().getLinkPreview() != null
                                ? Post.LinkPreview.builder()
                                        .title(dto.getContent().getLinkPreview()
                                                .getTitle())
                                        .description(dto.getContent()
                                                .getLinkPreview()
                                                .getDescription())
                                        .image(dto.getContent().getLinkPreview()
                                                .getImage())
                                        .siteName(dto.getContent()
                                                .getLinkPreview()
                                                .getSiteName())
                                        .build()
                                : null)
                        .media(dto.getContent().getMedia())
                        .tags(dto.getContent().getTags())
                        .build())
                .build();
    }
}
