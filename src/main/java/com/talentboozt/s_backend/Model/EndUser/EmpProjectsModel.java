package com.talentboozt.s_backend.Model.EndUser;

import com.talentboozt.s_backend.DTO.EndUser.EmpProjectsDTO;
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

@Document(collection = "portal_emp_projects")
public class EmpProjectsModel {
    @Id
    private String id;
    private String employeeId;
    @Field("projects")
    private List<EmpProjectsDTO> projects;
}
