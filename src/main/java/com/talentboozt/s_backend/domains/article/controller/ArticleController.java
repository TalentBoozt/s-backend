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

    @GetMapping("/{slug}")
    public ResponseEntity<ArticleResponse> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(articleService.getBySlug(slug));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ArticleResponse>> getFeatured() {
        return ResponseEntity.ok(articleService.getFeatured());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ArticleResponse>> search(@RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(articleService.search(q, pageable));
    }
}
