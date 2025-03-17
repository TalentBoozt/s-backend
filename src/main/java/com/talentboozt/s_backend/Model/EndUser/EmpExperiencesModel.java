package com.talentboozt.s_backend.Model.EndUser;

import com.talentboozt.s_backend.DTO.EndUser.EmpExperiencesDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@ToString
@Getter
@Setter

@Document(collection = "portal_emp_experiences")
public class EmpExperiencesModel {
    @Id
    private String id;
    private String employeeId;
    @Field("experiences")
    private List<EmpExperiencesDTO> experiences;
}

// {
//    "experiences":[
//      {
//        "id":"1",
//        "company":"Google",
//        "position":"Software Engineer",
//        "country":"USA",
//        "startDate":"01/01/2024",
//        "endDate":"Present",
//        "description":"lorem ipsum dolor sit amet consectetur adipisicing elit. Dignissimos, totam. \nlorem ipsum dolor sit amet consectetur adipisicing elit. Dignissimos, totam."
//      },
//      {
//        "id":"2",
//        "company":"Facebook",
//        "position":"Software Engineer",
//        "country":"USA",
//        "startDate":"15/10/2018",
//        "endDate":"11/02/2023",
//        "description":"lorem ipsum dolor sit amet consectetur adipisicing elit. Dignissimos, totam. \nlorem ipsum dolor sit amet consectetur adipisicing elit. Dignissimos, totam."
//      }
//    ]
//}
