package com.talentboozt.s_backend.domains.com_courses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecMaterialDTO {
    private String id;
    private String name;
    private String type;  // Type could be image, document, etc.
    private String url;   // URL of the material
}
