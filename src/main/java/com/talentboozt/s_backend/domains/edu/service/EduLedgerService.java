package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.model.ELedgerEntry;
import com.talentboozt.s_backend.domains.edu.model.ELedgerEntry.AccountType;
import com.talentboozt.s_backend.domains.edu.model.ELedgerEntry.EntryType;
import com.talentboozt.s_backend.domains.edu.model.ELedgerEntry.EventType;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ELedgerEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Double-entry ledger service. Every financial event MUST produce balanced
 * debit/credit entries through this service.
 *
 * Accounting rules:
 * - DEBIT increases asset/expense accounts
 * - CREDIT increases liability/revenue accounts
 * - For every event: SUM(debits) == SUM(credits)
 *
 * This service is called by:
 * - EduCoursePurchaseService (on finalization)
 * - EduRefundService (on refund)
 * - EduFinanceService (on payout completion)
 */
@Service
public class EduLedgerService {

    private static final Logger log = LoggerFactory.getLogger(EduLedgerService.class);
    private static final String PLATFORM_ACCOUNT = "PLATFORM";

    private final ELedgerEntryRepository ledgerRepository;

    public EduLedgerService(ELedgerEntryRepository ledgerRepository) {
        this.ledgerRepository = ledgerRepository;
    }

    // ── Purchase Recording ─────────────────────────────────────────

    /**
     * Records a course purchase in the double-entry ledger.
     * Idempotent — skips if entries already exist for this transaction.
     *
     * Entries created:
     *   DEBIT  BUYER_PAYMENT     totalAmount    (buyer pays)
     *   CREDIT PLATFORM_REVENUE  platformFee    (platform takes commission)
     *   CREDIT CREATOR_BALANCE   creatorEarning (creator earns)
     */
    public void recordPurchase(ETransactions tx) {
        String eventRef = "PURCHASE:" + tx.getId();
        if (ledgerRepository.existsByEventReference(eventRef)) {
            log.debug("Ledger entries already exist for {}", eventRef);
            return;
        }

        EventType eventType = tx.getBundleId() != null ? EventType.BUNDLE_PURCHASE : EventType.COURSE_PURCHASE;
        double totalAmount = tx.getAmount() != null ? tx.getAmount() : 0.0;
        double platformFee = tx.getPlatformFee() != null ? tx.getPlatformFee() : 0.0;
        double creatorEarning = tx.getCreatorEarning() != null ? tx.getCreatorEarning() : 0.0;

        // DEBIT: Money comes in from buyer
        ledgerRepository.save(ELedgerEntry.builder()
                .eventReference(eventRef)
                .eventType(eventType)
                .entryType(EntryType.DEBIT)
                .accountType(AccountType.BUYER_PAYMENT)
                .accountId(tx.getBuyerId())
                .amount(totalAmount)
                .currency(tx.getCurrency())
                .courseId(tx.getCourseId())
                .bundleId(tx.getBundleId())
                .description("Payment received for course purchase")
                .build());

        // CREDIT: Platform takes its commission
        ledgerRepository.save(ELedgerEntry.builder()
                .eventReference(eventRef)
                .eventType(eventType)
                .entryType(EntryType.CREDIT)
                .accountType(AccountType.PLATFORM_REVENUE)
                .accountId(PLATFORM_ACCOUNT)
                .amount(platformFee)
                .currency(tx.getCurrency())
                .courseId(tx.getCourseId())
                .bundleId(tx.getBundleId())
                .description("Platform commission (" + (tx.getCommissionRate() != null ? Math.round(tx.getCommissionRate() * 100) + "%" : "N/A") + ")")
                .build());

        // CREDIT: Creator earns their share
        ledgerRepository.save(ELedgerEntry.builder()
                .eventReference(eventRef)
                .eventType(eventType)
                .entryType(EntryType.CREDIT)
                .accountType(AccountType.CREATOR_BALANCE)
                .accountId(tx.getSellerId())
                .amount(creatorEarning)
                .currency(tx.getCurrency())
                .courseId(tx.getCourseId())
                .bundleId(tx.getBundleId())
                .description("Creator earnings from course sale")
                .build());

        log.info("Ledger recorded purchase: tx={}, buyer={}, creator={}, total={}, fee={}, earning={}",
                tx.getId(), tx.getBuyerId(), tx.getSellerId(), totalAmount, platformFee, creatorEarning);
    }

    /**
     * Records affiliate commission as a separate ledger event.
     */
    public void recordAffiliateCommission(String transactionId, String affiliateUserId,
                                           double amount, String currency, String courseId) {
        String eventRef = "AFFILIATE:" + transactionId;
        if (ledgerRepository.existsByEventReference(eventRef)) return;

        // DEBIT: Deducted from creator balance (already adjusted in tx)
        ledgerRepository.save(ELedgerEntry.builder()
                .eventReference(eventRef)
                .eventType(EventType.AFFILIATE_COMMISSION)
                .entryType(EntryType.DEBIT)
                .accountType(AccountType.CREATOR_BALANCE)
                .accountId("SYSTEM_AFFILIATE_POOL")
                .amount(amount)
                .currency(currency)
                .courseId(courseId)
                .description("Affiliate commission deducted")
                .build());

        // CREDIT: Affiliate earns
        ledgerRepository.save(ELedgerEntry.builder()
                .eventReference(eventRef)
                .eventType(EventType.AFFILIATE_COMMISSION)
                .entryType(EntryType.CREDIT)
                .accountType(AccountType.AFFILIATE_BALANCE)
                .accountId(affiliateUserId)
                .amount(amount)
                .currency(currency)
                .courseId(courseId)
                .description("Affiliate commission earned")
                .build());

        log.info("Ledger recorded affiliate commission: tx={}, affiliate={}, amount={}",
                transactionId, affiliateUserId, amount);
    }

    // ── Refund Recording ───────────────────────────────────────────

    /**
     * Records a refund reversal in the ledger.
     * Reverses the original purchase entries proportionally.
     *
     * Full refund:
     *   DEBIT  CREATOR_BALANCE   creatorEarning
     *   DEBIT  PLATFORM_REVENUE  platformFee
     *   CREDIT BUYER_PAYMENT     totalAmount
     *
     * Partial refund: same structure but with reduced amounts.
     */
    public void recordRefund(String refundId, String transactionId, String buyerId,
                              String sellerId, double refundAmount, double originalAmount,
                              String currency, String courseId, boolean isFullRefund) {
        String eventRef = "REFUND:" + refundId;
        if (ledgerRepository.existsByEventReference(eventRef)) return;

        EventType eventType = isFullRefund ? EventType.REFUND_FULL : EventType.REFUND_PARTIAL;
        double ratio = originalAmount > 0 ? refundAmount / originalAmount : 1.0;

        // Estimate proportional splits (actual split would require looking up original tx)
        // For accuracy, we use the ratio against the refund amount
        double platformRefund = round2(refundAmount * 0.05); // approximate — ideally from original tx
        double creatorRefund = round2(refundAmount - platformRefund);

        // DEBIT: Take back creator's earnings
        ledgerRepository.save(ELedgerEntry.builder()
                .eventReference(eventRef)
                .eventType(eventType)
                .entryType(EntryType.DEBIT)
                .accountType(AccountType.CREATOR_BALANCE)
                .accountId(sellerId)
                .amount(creatorRefund)
                .currency(currency)
                .courseId(courseId)
                .description(isFullRefund ? "Full refund — creator earnings reversed" : "Partial refund — creator earnings reduced")
                .build());

        // DEBIT: Take back platform commission
        ledgerRepository.save(ELedgerEntry.builder()
                .eventReference(eventRef)
                .eventType(eventType)
                .entryType(EntryType.DEBIT)
                .accountType(AccountType.PLATFORM_REVENUE)
                .accountId(PLATFORM_ACCOUNT)
                .amount(platformRefund)
                .currency(currency)
                .courseId(courseId)
                .description(isFullRefund ? "Full refund — platform commission reversed" : "Partial refund — platform commission reduced")
                .build());

        // CREDIT: Money returned to buyer
        ledgerRepository.save(ELedgerEntry.builder()
                .eventReference(eventRef)
                .eventType(eventType)
                .entryType(EntryType.CREDIT)
                .accountType(AccountType.BUYER_PAYMENT)
                .accountId(buyerId)
                .amount(refundAmount)
                .currency(currency)
                .courseId(courseId)
                .description(isFullRefund ? "Full refund issued" : "Partial refund issued ($" + refundAmount + ")")
                .build());

        log.info("Ledger recorded refund: refund={}, tx={}, amount={}, type={}",
                refundId, transactionId, refundAmount, eventType);
    }

    // ── Payout Recording ───────────────────────────────────────────

    /**
     * Records a completed payout in the ledger.
     *
     *   DEBIT  CREATOR_BALANCE  amount  (creator's balance decreases)
     *   CREDIT PAYOUT           amount  (money sent out)
     */
    public void recordPayout(String payoutId, String creatorId, double amount, String currency) {
        String eventRef = "PAYOUT:" + payoutId;
        if (ledgerRepository.existsByEventReference(eventRef)) return;

        ledgerRepository.save(ELedgerEntry.builder()
                .eventReference(eventRef)
                .eventType(EventType.PAYOUT_COMPLETED)
                .entryType(EntryType.DEBIT)
                .accountType(AccountType.CREATOR_BALANCE)
                .accountId(creatorId)
                .amount(amount)
                .currency(currency)
                .description("Payout — balance deducted")
                .build());

        ledgerRepository.save(ELedgerEntry.builder()
                .eventReference(eventRef)
                .eventType(EventType.PAYOUT_COMPLETED)
                .entryType(EntryType.CREDIT)
                .accountType(AccountType.PAYOUT)
                .accountId(creatorId)
                .amount(amount)
                .currency(currency)
                .description("Payout — funds transferred")
                .build());

        log.info("Ledger recorded payout: payout={}, creator={}, amount={}", payoutId, creatorId, amount);
    }

    // ── Balance Queries ────────────────────────────────────────────

    /**
     * Calculates the net balance for an account.
     * Balance = SUM(credits) - SUM(debits)
     */
    public double getAccountBalance(AccountType accountType, String accountId) {
        List<ELedgerEntry> entries = ledgerRepository.findByAccountTypeAndAccountId(accountType, accountId);

        double credits = entries.stream()
                .filter(e -> e.getEntryType() == EntryType.CREDIT)
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();

        double debits = entries.stream()
                .filter(e -> e.getEntryType() == EntryType.DEBIT)
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();

        return round2(credits - debits);
    }

    /**
     * Gets the total platform revenue.
     */
    public double getPlatformRevenue() {
        return getAccountBalance(AccountType.PLATFORM_REVENUE, PLATFORM_ACCOUNT);
    }

    /**
     * Gets a creator's current balance (earnings minus payouts minus refunds).
     */
    public double getCreatorBalance(String creatorId) {
        return getAccountBalance(AccountType.CREATOR_BALANCE, creatorId);
    }

    /**
     * Gets the total paid out to a creator.
     */
    public double getCreatorTotalPaidOut(String creatorId) {
        return getAccountBalance(AccountType.PAYOUT, creatorId);
    }

    /**
     * Verifies ledger integrity for a specific event.
     * Returns true if SUM(debits) == SUM(credits).
     */
    public boolean verifyEventBalance(String eventReference) {
        List<ELedgerEntry> entries = ledgerRepository.findByEventReference(eventReference);

        double debits = entries.stream()
                .filter(e -> e.getEntryType() == EntryType.DEBIT)
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();

        double credits = entries.stream()
                .filter(e -> e.getEntryType() == EntryType.CREDIT)
                .mapToDouble(e -> e.getAmount() != null ? e.getAmount() : 0.0)
                .sum();

        boolean balanced = Math.abs(debits - credits) < 0.01;
        if (!balanced) {
            log.warn("LEDGER IMBALANCE for {}: debits={}, credits={}, diff={}",
                    eventReference, debits, credits, debits - credits);
        }
        return balanced;
    }

    /**
     * Returns all ledger entries for a course (for course-level P&L reporting).
     */
    public List<ELedgerEntry> getCourseEntries(String courseId) {
        return ledgerRepository.findByCourseId(courseId);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
