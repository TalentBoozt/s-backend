package com.talentboozt.s_backend.domains.user.model;

import com.talentboozt.s_backend.domains.plat_job_portal.dto.FavJobDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@ToString

@Document(collection = "portal_employees")
public class EmployeeModel {
    @Id
    private String id;
    private String firstname;
    private String lastname;
    @Indexed
    private String occupation;
    private String image;
    private String coverImage;
    private String dob;
    @Indexed(unique = true)
    private String email;
    private String resume;
    private String intro;
    private String skills; //id
    private String experiences; //id
    private String education; //id
    private String projects; //id
    private String certificates; //id
    private String contactInfo; //id
    private String courses; //id
    private String followings;
    private String followers;
    @Field("savedJobs")
    private List<FavJobDTO> savedJobs;
    private Object accountNotifications;
    private Object marketingNotifications;
    private Object profileCompleted;
    private String profileStatus;
    private String companyId;
    private String expectedSalaryRange;
    private String currentExperience;
    private String keywords;
}

// {
//    "firstname":"john",
//    "lastname":"Doe",
//    "occupation":"Web Developer",
//    "image":"",
//    "coverImage":"",
//    "dob":"09/11/1999",
//    "email":"johndoe@gmail.com",
//    "resume":"",
//    "intro":"lorem ipsum dolor sit amet consectetur adipisicing elit. Dignissimos, totam. \nlorem ipsum dolor sit amet consectetur adipisicing elit. Dignissimos, totam.",
//    "skills":"",
//    "experiences":"",
//    "education":"",
//    "contactInfo":"",
//    "accountNotifications":{
//      "mention": true,
//      "follow": true,
//      "shareActivity": true,
//      "message": true
//    },
//    "marketingNotifications":{
//      "promotion": true,
//      "companyNews": true,
//      "weeklyJobs": true,
//      "unsubscribe": true
//    },
//    "profileCompleted":{
//      "name": true,
//      "email": true,
//      "resume": true,
//      "occupation": true,
//      "profilePic": true,
//      "coverPic": true,
//      "intro": true,
//      "skills": true,
//      "experiences": true,
//      "education": true,
//      "contactInfo": true,
//      "socialLinks": true
//    },
//    "profileStatus":""
//}
