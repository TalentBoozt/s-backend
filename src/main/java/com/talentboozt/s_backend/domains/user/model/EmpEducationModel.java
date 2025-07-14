package com.talentboozt.s_backend.domains.user.model;

import com.talentboozt.s_backend.domains.user.dto.EmpEducationDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Getter
@Setter
@ToString

@Document(collection = "portal_emp_education")
public class EmpEducationModel {
    @Id
    private String id;
    private String employeeId;
    @Field("education")
    private List<EmpEducationDTO> education;
}

// {
//    "education":[
//      {
//        "id":"1",
//        "school":"University of California",
//        "degree":"Bachelor of Science",
//        "country":"USA",
//        "startDate":"01/01/2024",
//        "endDate":"Present",
//        "description":"lorem ipsum dolor sit amet consectetur adipisicing elit. Dignissimos, totam. \nlorem ipsum dolor sit amet consectetur adipisicing elit. Dignissimos, totam."
//      },
//      {
//        "id":"2",
//        "school":"University of California",
//        "degree":"Bachelor of Science",
//        "country":"USA",
//        "startDate":"01/01/2024",
//        "endDate":"Present",
//        "description":"lorem ipsum dolor sit amet consectetur adipisicing elit. Dignissimos, totam. \nlorem ipsum dolor sit amet consectetur adipisicing elit. Dignissimos, totam."
//      }
//    ]
//}
