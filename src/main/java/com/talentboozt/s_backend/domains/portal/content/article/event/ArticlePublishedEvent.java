package com.talentboozt.s_backend.domains.portal.content.article.event;

import com.talentboozt.s_backend.domains.portal.content.article.model.Article;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ArticlePublishedEvent extends ApplicationEvent {
    private final Article article;

    public ArticlePublishedEvent(Object source, Article article) {
        super(source);
        this.article = article;
    }
}
