package com.talentboozt.s_backend.domains.reputation.service;

import com.talentboozt.s_backend.domains.article.event.ArticleBookmarkedEvent;
import com.talentboozt.s_backend.domains.article.event.ArticleLikedEvent;
import com.talentboozt.s_backend.domains.article.event.ArticlePublishedEvent;
import com.talentboozt.s_backend.domains.community.event.CommentUpvotedEvent;
import com.talentboozt.s_backend.domains.community.event.ContentCreatedEvent;
import com.talentboozt.s_backend.domains.community.event.PostUpvotedEvent;
import com.talentboozt.s_backend.domains.reputation.model.ReputationSourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReputationEventListener {
    private final ReputationService reputationService;

    @EventListener
    public void handleArticlePublished(ArticlePublishedEvent event) {
        reputationService.applyEvent(
                event.getArticle().getAuthorId(),
                ReputationSourceType.ARTICLE_PUBLISH,
                event.getArticle().getId());
    }

    @EventListener
    public void handleArticleLiked(ArticleLikedEvent event) {
        reputationService.applyEvent(
                event.getAuthorId(),
                ReputationSourceType.ARTICLE_LIKE,
                event.getArticleId());
    }

    @EventListener
    public void handleContentCreated(ContentCreatedEvent event) {
        ReputationSourceType type = "POST".equals(event.getType()) ? ReputationSourceType.POST_CREATE
                : ReputationSourceType.COMMENT_CREATE;

        reputationService.applyEvent(
                event.getAuthorId(),
                type,
                event.getTargetId());
    }

    @EventListener
    public void handlePostUpvoted(PostUpvotedEvent event) {
        reputationService.applyEvent(
                event.getAuthorId(),
                ReputationSourceType.POST_UPVOTE,
                event.getPostId());
    }

    @EventListener
    public void handleCommentUpvoted(CommentUpvotedEvent event) {
        reputationService.applyEvent(
                event.getAuthorId(),
                ReputationSourceType.COMMENT_UPVOTE,
                event.getCommentId());
    }

    @EventListener
    public void handleArticleBookmarked(ArticleBookmarkedEvent event) {
        reputationService.applyEvent(
                event.getAuthorId(),
                ReputationSourceType.ARTICLE_BOOKMARK,
                event.getArticleId());
    }
}
