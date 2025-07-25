package com.talentboozt.s_backend.domains.user.model;

import com.talentboozt.s_backend.domains.user.dto.EmpProjectsDTO;
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
