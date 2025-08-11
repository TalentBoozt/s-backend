package com.talentboozt.s_backend.domains.ai_tool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "course_ai_credit_record")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditRecord {
    @Id
    private String id;
    private int creditsRemaining;
    private LocalDate lastReset;
}
