package com.talentboozt.s_backend.domains.edu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EduValidationResponseDTO {
    private Double plagiarismScore; // 0 to 100, where 100 is fully plagiarized
    private Double aiScore; // 0 to 100, where 100 is fully AI generated
    private Double qualityScore; // 0 to 100
    private List<String> findings;
    private String status; // PASSED, WARNING, REJECTED
}
