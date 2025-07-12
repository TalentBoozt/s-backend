package com.talentboozt.s_backend.domains.com_courses.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
public class QuizDTO {
    @Id
    private String id;
    private String courseId;
    private String moduleId;
    private String title;
    private String description;
    private String visibility; // public, participant, only-me
    private String creationDate;
    private String updateDate;
    private int attemptLimit;
    private List<QuestionDTO> questions;
}

