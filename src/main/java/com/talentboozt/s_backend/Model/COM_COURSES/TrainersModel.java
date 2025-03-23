package com.talentboozt.s_backend.Model.COM_COURSES;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString

@Document(collection = "trainers")
public class TrainersModel {
    @Id
    private String id;
    private String trainerName;
    private String trainerEmail;
    private String trainerPhone;
    private String trainerImage;
    private String trainerDescription;
    private String trainerExperience;
    private String trainerQualification;
    private String trainerStatus;
    private String trainerDateJoined;
    private String trainerDateUpdated;
    private String trainerRole; //admin, manager, trainer
}
