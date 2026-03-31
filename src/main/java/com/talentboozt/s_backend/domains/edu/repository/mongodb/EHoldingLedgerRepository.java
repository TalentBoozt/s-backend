package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.talentboozt.s_backend.domains.edu.model.EHoldingLedger;
import com.talentboozt.s_backend.domains.edu.enums.EHoldingStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EHoldingLedgerRepository extends MongoRepository<EHoldingLedger, String> {
    List<EHoldingLedger> findByBeneficiaryIdOrderByCreatedAtDesc(String beneficiaryId);
    List<EHoldingLedger> findByBeneficiaryIdAndStatus(String beneficiaryId, EHoldingStatus status);
    Optional<EHoldingLedger> findByTransactionId(String transactionId);
    List<EHoldingLedger> findByStatusAndClearanceDateBefore(EHoldingStatus status, Instant cutoff);
}
