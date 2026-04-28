package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.EWalletTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EWalletTransactionRepository extends MongoRepository<EWalletTransaction, String> {
    List<EWalletTransaction> findByUserId(String userId);
}
