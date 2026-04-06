package com.talentboozt.s_backend.domains.edu.repository.mongodb;

import com.talentboozt.s_backend.domains.edu.model.ELedgerEntry;
import com.talentboozt.s_backend.domains.edu.model.ELedgerEntry.AccountType;
import com.talentboozt.s_backend.domains.edu.model.ELedgerEntry.EntryType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ELedgerEntryRepository extends MongoRepository<ELedgerEntry, String> {

    /** All entries for a specific event (should contain balanced debit/credit pairs) */
    List<ELedgerEntry> findByEventReference(String eventReference);

    /** Check if entries already exist for an event (idempotency) */
    boolean existsByEventReference(String eventReference);

    /** All entries for an account (for balance calculation) */
    List<ELedgerEntry> findByAccountTypeAndAccountId(AccountType accountType, String accountId);

    /** All debits for an account */
    List<ELedgerEntry> findByAccountTypeAndAccountIdAndEntryType(
            AccountType accountType, String accountId, EntryType entryType);

    /** All entries for a course (for course-level P&L) */
    List<ELedgerEntry> findByCourseId(String courseId);

    /** All entries for a bundle */
    List<ELedgerEntry> findByBundleId(String bundleId);
}
