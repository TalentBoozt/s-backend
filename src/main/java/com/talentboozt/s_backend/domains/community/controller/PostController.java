package com.talentboozt.s_backend.domains.community.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.talentboozt.s_backend.domains.community.dto.CommentDTO;
import com.talentboozt.s_backend.domains.community.dto.PostDTO;
import com.talentboozt.s_backend.domains.community.service.LinkPreviewService;
import com.talentboozt.s_backend.domains.community.service.PostService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final LinkPreviewService linkPreviewService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<PostDTO>> getByCommunity(@PathVariable String communityId) {
        return ResponseEntity.ok(postService.getPostsByCommunity(communityId));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<PostDTO>> getByAuthor(@PathVariable String authorId) {
        return ResponseEntity.ok(postService.getPostsByAuthor(authorId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getById(@PathVariable String id) {
        PostDTO post = postService.getPostById(id);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PostDTO> create(@RequestBody PostDTO postDTO) {
        return new ResponseEntity<>(postService.createPost(postDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> update(
            @PathVariable String id,
            @RequestBody PostDTO postDTO) {
        PostDTO post = postService.updatePost(id, postDTO);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/react")
    public ResponseEntity<PostDTO> react(
            @PathVariable String id,
            @RequestParam String emoji,
            @RequestParam String userId) {
        PostDTO post = postService.reactToPost(id, emoji, userId);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<CommentDTO>> getComments(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(postService.getComments(id, pageable));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable String id,
            @RequestBody CommentDTO commentDTO) {
        return new ResponseEntity<>(postService.addComment(id, commentDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String id, @PathVariable String commentId) {
        postService.deleteComment(id, commentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable String id,
            @PathVariable String commentId,
            @RequestBody CommentDTO commentDTO) {
        CommentDTO comment = postService.updateComment(id, commentId, commentDTO);
        return comment != null ? ResponseEntity.ok(comment) : ResponseEntity.notFound().build();
    }

    @PostMapping("/comments/{commentId}/react")
    public ResponseEntity<CommentDTO> reactToComment(
            @PathVariable String commentId,
            @RequestParam String emoji,
            @RequestParam String userId) {
        CommentDTO comment = postService.reactToComment(commentId, emoji, userId);
        return comment != null ? ResponseEntity.ok(comment) : ResponseEntity.notFound().build();
    }

    @PostMapping("/link/preview")
    public ResponseEntity<PostDTO.LinkPreviewDTO> getLinkPreview(@RequestBody Map<String, String> body) {
        String url = body.get("url");
        if (url == null || url.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        PostDTO.LinkPreviewDTO preview = linkPreviewService.getPreview(url);
        return preview != null ? ResponseEntity.ok(preview) : ResponseEntity.ok().build();
    }
}
