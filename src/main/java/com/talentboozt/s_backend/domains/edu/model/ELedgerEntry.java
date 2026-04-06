package com.talentboozt.s_backend.domains.edu.model;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Double-entry ledger entry. Every financial event produces TWO entries:
 * one DEBIT and one CREDIT, which must always balance.
 *
 * Account types:
 * - BUYER_PAYMENT:     Money received from buyer
 * - PLATFORM_REVENUE:  Platform's commission/fee
 * - CREATOR_BALANCE:   Creator's earned balance (pre-payout)
 * - PAYOUT:            Money paid out to creator
 * - REFUND_RESERVE:    Hold for potential refunds
 * - AFFILIATE_BALANCE: Affiliate earnings
 *
 * For every purchase:
 *   DEBIT  BUYER_PAYMENT     $100
 *   CREDIT PLATFORM_REVENUE  $7
 *   CREDIT CREATOR_BALANCE   $93
 *
 * For every refund:
 *   DEBIT  CREATOR_BALANCE   $93
 *   DEBIT  PLATFORM_REVENUE  $7
 *   CREDIT BUYER_PAYMENT     $100
 *
 * For every payout:
 *   DEBIT  CREATOR_BALANCE   $93
 *   CREDIT PAYOUT            $93
 *
 * Rule: SUM(debits) == SUM(credits) for every eventReference.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "edu_ledger_entries")
@CompoundIndexes({
    @CompoundIndex(name = "idx_account", def = "{'accountType': 1, 'accountId': 1}"),
    @CompoundIndex(name = "idx_event", def = "{'eventReference': 1, 'entryType': 1}")
})
public class ELedgerEntry {
    @Id
    private String id;

    /** Groups related debit/credit pairs (e.g., transaction ID, refund ID, payout ID) */
    @Indexed
    private String eventReference;

    /** What triggered this entry */
    @Indexed
    private EventType eventType;

    /** DEBIT or CREDIT */
    private EntryType entryType;

    /** Which account this entry belongs to */
    @Indexed
    private AccountType accountType;

    /** Owner of the account (userId, "PLATFORM", etc.) */
    @Indexed
    private String accountId;

    /** Positive amount (direction is determined by entryType) */
    private Double amount;

    /** Currency */
    private String currency;

    /** Related course ID (for reporting) */
    private String courseId;

    /** Related bundle ID (for reporting) */
    private String bundleId;

    /** Human-readable description */
    private String description;

    @CreatedDate
    private Instant createdAt;

    public enum EntryType {
        DEBIT,
        CREDIT
    }

    public enum AccountType {
        BUYER_PAYMENT,
        PLATFORM_REVENUE,
        CREATOR_BALANCE,
        PAYOUT,
        REFUND_RESERVE,
        AFFILIATE_BALANCE
    }

    public enum EventType {
        COURSE_PURCHASE,
        BUNDLE_PURCHASE,
        SUBSCRIPTION_PAYMENT,
        REFUND_FULL,
        REFUND_PARTIAL,
        PAYOUT_COMPLETED,
        AFFILIATE_COMMISSION
    }
}
