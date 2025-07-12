package com.talentboozt.s_backend.shared.mail.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "portal_news_letter")
public class NewsLatterModel {
    @Id
    private String id;
    private String email;
}
