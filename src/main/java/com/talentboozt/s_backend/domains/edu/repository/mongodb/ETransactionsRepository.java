package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import java.util.List;

@Repository
public interface ETransactionsRepository extends MongoRepository<ETransactions, String> {
    List<ETransactions> findBySellerId(String sellerId);
}
