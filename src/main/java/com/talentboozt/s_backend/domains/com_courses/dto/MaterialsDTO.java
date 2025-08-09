package com.talentboozt.s_backend.domains.com_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialsDTO {
    private String id;
    private String courseId;
    private String moduleId;
    private String name; // file name
    private String type; // pdf, video
    private String url; // file url
    private String category; // book, assignment, recording
    private String visibility; // public, participant, only-me
    private String uploadDate; // upload date (timestamp)
    private String updateDate; // update date (timestamp) if updated
    private int viewCount;

    // mock for test
    public MaterialsDTO(String id) {
        this.id = id;
    }
}
