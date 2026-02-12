package com.talentboozt.s_backend.domains.article.service;

import com.talentboozt.s_backend.domains.article.dto.*;
import com.talentboozt.s_backend.domains.article.model.*;
import com.talentboozt.s_backend.domains.article.repository.mongodb.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final MongoTemplate mongoTemplate;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    public ArticleResponse createArticle(ArticleRequest request, String authorId) {
        List<String> tagIds = getOrCreateTags(request.getTags());

        Article article = Article.builder()
                .title(request.getTitle())
                .slug(generateSlug(request.getTitle()))
                .content(request.getContent())
                .excerpt(request.getExcerpt())
                .authorId(authorId)
                .coverImage(request.getCoverImage())
                .tagIds(tagIds)
                .status(request.getStatus() != null ? request.getStatus() : ArticleStatus.DRAFT)
                .readTime(calculateReadTime(request.getContent()))
                .featured(request.isFeatured())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Article saved = articleRepository.save(article);

        if (saved.getStatus() == ArticleStatus.PUBLISHED) {
            eventPublisher.publishEvent(
                    new com.talentboozt.s_backend.domains.article.event.ArticlePublishedEvent(this, saved));
        }

        return mapToResponse(saved);
    }

    public ArticleResponse getBySlug(String slug) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        incrementViews(article.getId());
        return mapToResponse(article);
    }

    public Page<ArticleResponse> search(String q, Pageable pageable) {
        return articleRepository.searchArticles(q, pageable).map(this::mapToResponse);
    }

    public List<ArticleResponse> getFeatured() {
        return articleRepository.findFeaturedArticles().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void incrementViews(String articleId) {
        Query query = new Query(Criteria.where("id").is(articleId));
        Update update = new Update().inc("views", 1);
        mongoTemplate.updateFirst(query, update, Article.class);
    }

    private List<String> getOrCreateTags(List<String> tagNames) {
        if (tagNames == null)
            return Collections.emptyList();

        return tagNames.stream().map(name -> {
            return tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(Tag.builder()
                            .name(name)
                            .slug(generateSlug(name))
                            .build()));
        }).map(Tag::getId).collect(Collectors.toList());
    }

    private String generateSlug(String text) {
        return text.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private int calculateReadTime(String content) {
        if (content == null)
            return 0;
        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil(wordCount / 225.0); // Avg reading speed: 225 wpm
    }

    public ArticleResponse likeArticle(String articleId, String userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        Query query = new Query(Criteria.where("id").is(articleId));
        Update update = new Update().inc("likes", 1);
        mongoTemplate.updateFirst(query, update, Article.class);

        eventPublisher.publishEvent(com.talentboozt.s_backend.domains.article.event.ArticleLikedEvent.builder()
                .articleId(articleId)
                .userId(userId)
                .authorId(article.getAuthorId())
                .build());

        return getBySlug(article.getSlug());
    }

    public void bookmarkArticle(String articleId, String userId) {
        // In a real system, this would add to a bookmarks collection
        // For now, we just trigger the reputation event as requested
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        // Trigger reputation event
        eventPublisher.publishEvent(com.talentboozt.s_backend.domains.article.event.ArticleBookmarkedEvent.builder()
                .articleId(articleId)
                .userId(userId)
                .authorId(article.getAuthorId())
                .build());
    }

    private ArticleResponse mapToResponse(Article article) {
        ArticleResponse response = new ArticleResponse();
        response.setId(article.getId());
        response.setTitle(article.getTitle());
        response.setSlug(article.getSlug());
        response.setContent(article.getContent());
        response.setExcerpt(article.getExcerpt());
        response.setAuthorId(article.getAuthorId());
        response.setCoverImage(article.getCoverImage());
        response.setStatus(article.getStatus());
        response.setReadTime(article.getReadTime());
        response.setViews(article.getViews());
        response.setLikes(article.getLikes());
        response.setFeatured(article.isFeatured());
        response.setCreatedAt(article.getCreatedAt());
        response.setUpdatedAt(article.getUpdatedAt());

        // Map AI Generated Fields
        response.setAiSummary(article.getAiSummary());
        response.setAiHighlights(article.getAiHighlights());
        response.setAiSnippet(article.getAiSnippet());
        response.setAiSeoDescription(article.getAiSeoDescription());

        if (article.getTagIds() != null) {
            response.setTags(tagRepository.findAllById(article.getTagIds()).stream()
                    .map(tag -> new TagResponse(tag.getId(), tag.getName(), tag.getSlug()))
                    .collect(Collectors.toList()));
        }

        return response;
    }
}
