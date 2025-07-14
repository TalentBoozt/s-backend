package com.talentboozt.s_backend.domains.plat_job_portal.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString

@Document(collection = "portal_interviewQuestion")
public class InterviewQuestionModel {
    @Id
    private String id;
    private String category;
    private List<Question> questions;


    @Getter
    @Setter
    public static class Question {
        private String id;
        private String question;
        private String overview;
        private int viewCount;
        private List<Answer> answers;
    }

    @Getter
    @Setter
    public static class Answer {
        private String id;
        private String by;
        private String position;
        private String date;
        private String answer;
        private String video;
        private int viewCount;
    }
}
