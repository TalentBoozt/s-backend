package com.talentboozt.s_backend.domains.article.event;

import com.talentboozt.s_backend.domains.article.model.Article;
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
