package com.talentboozt.s_backend.domains.edu.dto.course;

import lombok.Data;
import java.util.List;

@Data
public class OrderUpdateRequest {
    private List<String> orderedIds;
}
