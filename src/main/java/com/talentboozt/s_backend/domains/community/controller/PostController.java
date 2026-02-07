package com.talentboozt.s_backend.domains.community.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.talentboozt.s_backend.domains.community.dto.CommentDTO;
import com.talentboozt.s_backend.domains.community.dto.PostDTO;
import com.talentboozt.s_backend.domains.community.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/v2/posts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
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

    @PostMapping("/{id}/react")
    public ResponseEntity<PostDTO> react(
            @PathVariable String id,
            @RequestParam String emoji,
            @RequestParam String userId) {
        PostDTO post = postService.reactToPost(id, emoji, userId);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable String id) {
        return ResponseEntity.ok(postService.getComments(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable String id,
            @RequestBody CommentDTO commentDTO) {
        return new ResponseEntity<>(postService.addComment(id, commentDTO), HttpStatus.CREATED);
    }
}
