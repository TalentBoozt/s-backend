package com.talentboozt.s_backend.domains.article.controller;

import com.talentboozt.s_backend.domains.article.dto.ArticleRequest;
import com.talentboozt.s_backend.domains.article.dto.ArticleResponse;
import com.talentboozt.s_backend.domains.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleResponse> create(@RequestBody ArticleRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(articleService.createArticle(request, userId));
    }

    @GetMapping("/top")
    public ResponseEntity<List<ArticleResponse>> getTopArticles() {
        return ResponseEntity.ok(articleService.getTopArticles());
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ArticleResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.getBySlug(slug));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ArticleResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(articleService.getById(id));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ArticleResponse>> getFeatured() {
        return ResponseEntity.ok(articleService.getFeatured());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArticleResponse>> search(@RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(articleService.search(q, pageable));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ArticleResponse> like(@PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(articleService.likeArticle(id, userId));
    }

    @PostMapping("/{id}/bookmark")
    public ResponseEntity<Void> bookmark(@PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        articleService.bookmarkArticle(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ArticleResponse>> getMyArticles(@RequestHeader("X-User-Id") String userId,
            Pageable pageable) {
        return ResponseEntity.ok(articleService.getMyArticles(userId, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> update(@PathVariable String id,
            @RequestBody ArticleRequest request,
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(articleService.updateArticle(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        articleService.deleteArticle(id, userId);
        return ResponseEntity.noContent().build();
    }
}
