package com.talentboozt.s_backend.domains.edu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_creator_finance_settings")
public class ECreatorFinanceSettings {
    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private List<PayoutMethod> payoutMethods;
    private String taxVerificationStatus; // VERIFIED, PENDING, UNVERIFIED
    private String profileVerificationStatus; // VERIFIED, PENDING, UNVERIFIED
    private List<TaxForm> taxForms;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PayoutMethod {
        private String id;
        private String type; // PAYPAL, BANK_TRANSFER, STRIPE, WISE
        private String identifier; // Primary ID like email or masked account
        private String accountHolderName;
        private String bankName;
        private String swiftBic;
        private String routingNumber;
        private String iban;
        private String countryCode;
        private String currency;
        private boolean isDefault;
        private boolean isVerified;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaxForm {
        private String id;
        private String label;
        private String status;
        private String url;
        private Instant uploadedAt;
    }
}
