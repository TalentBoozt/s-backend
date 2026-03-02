package com.talentboozt.s_backend.domains.article.repository.mongodb;

import com.talentboozt.s_backend.domains.article.model.ArticleEvaluationLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleEvaluationLogRepository extends MongoRepository<ArticleEvaluationLog, String> {
    Optional<ArticleEvaluationLog> findFirstByArticleIdOrderByEvaluatedAtDesc(String articleId);
}
