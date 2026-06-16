package com.talentboozt.s_backend.shared.infrastructure.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriveFileDTO {
    private String id;
    private String name;
    private String mimeType;
    private String size;
    private String modifiedTime;
    private List<String> parents;
}
