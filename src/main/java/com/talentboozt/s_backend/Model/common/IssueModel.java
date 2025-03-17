package com.talentboozt.s_backend.Model.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "portal_report-issues")
public class IssueModel {
    @Id
    private String id;
    private String issueType;
    private String description;
    private String attachment;
}
