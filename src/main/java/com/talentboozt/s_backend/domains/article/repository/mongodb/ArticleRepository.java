package com.talentboozt.s_backend.domains.article.repository.mongodb;

import com.talentboozt.s_backend.domains.article.model.Article;
import com.talentboozt.s_backend.domains.article.model.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
    Optional<Article> findBySlug(String slug);

    Page<Article> findByStatus(ArticleStatus status, Pageable pageable);

    @Query("{ 'status': 'PUBLISHED', 'featured': true }")
    List<Article> findFeaturedArticles();

    Page<Article> findByTagIdsContainingAndStatus(String tagId, ArticleStatus status, Pageable pageable);

    @Query("{ $text: { $search: ?0 }, 'status': 'PUBLISHED' }")
    Page<Article> searchArticles(String query, Pageable pageable);

    Page<Article> findByAuthorId(String authorId, Pageable pageable);

    @Query("{ 'status': 'PUBLISHED' }")
    List<Article> findTopArticles();
}
