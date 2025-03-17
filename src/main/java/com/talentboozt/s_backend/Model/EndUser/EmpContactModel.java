package com.talentboozt.s_backend.Model.EndUser;

import com.talentboozt.s_backend.DTO.EndUser.EmpContactDTO;
import com.talentboozt.s_backend.DTO.common.SocialLinksDTO;
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

@Document(collection = "portal_emp_contact")
public class EmpContactModel {
    @Id
    private String id;
    private String employeeId;
    @Field("contact")
    private List<EmpContactDTO> contact;
    @Field("social_links")
    private List<SocialLinksDTO> socialLinks;
    private boolean publicity;
}

// {
//    "employeeId": "1",
//    "contact": [
//        {
//            "phone": "1234567890",
//            "email": "john.doe@example.com",
//            "address": "123 Main St, Anytown USA",
//            "city": "Anytown",
//            "country": "USA",
//            "zipCode": "12345",
//            "website": "johndoe.com"
//        }
//    ],
//    "socialLinks": [
//        {
//            "twitter": "johndoe",
//            "facebook": "johndoe",
//            "linkedin": "johndoe",
//            "instagram": "johndoe",
//            "github": "johndoe"
//        }
//    ]
//}