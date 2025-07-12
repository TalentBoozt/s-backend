package com.talentboozt.s_backend.domains.user.model;

import com.talentboozt.s_backend.domains.user.dto.EmpSkillsDTO;
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

@Document(collection = "portal_emp_skills")
public class EmpSkillsModel {
    @Id
    private String id;
    private String employeeId;
    @Field("skills")
    private List<EmpSkillsDTO> skills;
}

// {
//    "employeeId":"1",
//    "skills":[
//      {
//        "id":"1",
//        "skill":"HTML",
//        "percentage":"90"
//      },
//      {
//        "id":"2",
//        "skill":"CSS",
//        "percentage":"80"
//      },
//      {
//        "id":"3",
//        "skill":"Javascript",
//        "percentage":"70"
//      },
//      {
//        "id":"4",
//        "skill":"Angular",
//        "percentage":"60"
//      }
//    ]
//}
