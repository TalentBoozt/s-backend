package com.talentboozt.s_backend.domains.edu.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.talentboozt.s_backend.domains.edu.dto.coupon.CouponValidationResult;
import com.talentboozt.s_backend.domains.edu.enums.EPaymentMethod;
import com.talentboozt.s_backend.domains.edu.enums.EPaymentStatus;
import com.talentboozt.s_backend.domains.edu.exception.EduAccessDeniedException;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.talentboozt.s_backend.domains.edu.enums.EHoldingStatus;
import com.talentboozt.s_backend.domains.edu.model.EHoldingLedger;
import com.talentboozt.s_backend.domains.edu.model.EAffiliates;
import com.talentboozt.s_backend.domains.edu.model.EBundles;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EHoldingLedgerRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAffiliatesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EBundlesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EEnrollmentsRepository;

@Service
public class EduCoursePurchaseService {

    private static final Logger log = LoggerFactory.getLogger(EduCoursePurchaseService.class);

    public static final String CHECKOUT_METADATA_TYPE = "EDU_COURSE_PURCHASE";

    private final ECoursesRepository coursesRepository;
    private final ETransactionsRepository transactionsRepository;
    private final EduEnrollmentService enrollmentService;
    private final EduCommissionCalculator commissionCalculator;
    private final EHoldingLedgerRepository holdingLedgerRepository;
    private final EduFraudDetectionService fraudDetectionService;
    private final EAffiliatesRepository affiliatesRepository;
    private final EduCouponService couponService;
    private final EBundlesRepository bundlesRepository;
    private final EEnrollmentsRepository enrollmentsRepository;
    private final EduLedgerService ledgerService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public EduCoursePurchaseService(ECoursesRepository coursesRepository,
            ETransactionsRepository transactionsRepository,
            EduEnrollmentService enrollmentService,
            EduCommissionCalculator commissionCalculator,
            EHoldingLedgerRepository holdingLedgerRepository,
            EduFraudDetectionService fraudDetectionService,
            EAffiliatesRepository affiliatesRepository,
            EduCouponService couponService,
            EBundlesRepository bundlesRepository,
            EEnrollmentsRepository enrollmentsRepository,
            EduLedgerService ledgerService) {
        this.coursesRepository = coursesRepository;
        this.transactionsRepository = transactionsRepository;
        this.enrollmentService = enrollmentService;
        this.commissionCalculator = commissionCalculator;
        this.holdingLedgerRepository = holdingLedgerRepository;
        this.fraudDetectionService = fraudDetectionService;
        this.affiliatesRepository = affiliatesRepository;
        this.couponService = couponService;
        this.bundlesRepository = bundlesRepository;
        this.enrollmentsRepository = enrollmentsRepository;
        this.ledgerService = ledgerService;
    }

    /**
     * Starts Stripe Checkout for a paid marketplace course; persists a PENDING
     * transaction row.
     */
    public Map<String, String> createCourseCheckoutSession(String userId, String courseId, String affiliateId, String couponCode) throws StripeException {
        ECourses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new EduResourceNotFoundException("Course not found"));
        
        fraudDetectionService.validateBulkPurchases(userId, Map.of(course.getCreatorId(), 1L));
        
        if (!Boolean.TRUE.equals(course.getPublished())) {
            throw new EduBadRequestException("Course is not available for purchase");
        }
        if (Boolean.TRUE.equals(course.getIsPrivate())) {
            throw new EduBadRequestException("This course is not sold on the marketplace");
        }
        double originalPrice = course.getPrice() != null ? course.getPrice() : 0.0;
        if (originalPrice <= 0) {
            throw new EduBadRequestException("This course is free — enroll without payment.");
        }
        if (userId.equals(course.getCreatorId())) {
            throw new EduBadRequestException("You cannot buy your own course");
        }

        // Apply coupon if provided
        double discountAmount = 0.0;
        double finalPrice = originalPrice;
        if (couponCode != null && !couponCode.isBlank()) {
            CouponValidationResult couponResult = couponService.validateAndCalculate(couponCode, courseId, userId, originalPrice);
            discountAmount = couponResult.getDiscountAmount();
            finalPrice = couponResult.getFinalPrice();
            log.info("Coupon {} applied: original={}, discount={}, final={}", couponCode, originalPrice, discountAmount, finalPrice);
        }

        if (finalPrice <= 0) {
            throw new EduBadRequestException("Coupon makes this course free — use the free enrollment flow instead.");
        }

        String currency = (course.getCurrency() != null ? course.getCurrency() : "USD").toLowerCase();
        long amountCents = Math.max(50L, Math.round(finalPrice * 100));

        // Generate idempotency key to prevent duplicate charges on network retries
        String idempotencyKey = UUID.randomUUID().toString();

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/checkout/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/checkout/" + courseId)
                .putMetadata("type", CHECKOUT_METADATA_TYPE)
                .putMetadata("userId", userId)
                .putMetadata("courseId", courseId)
                .putMetadata("affiliateId", affiliateId != null ? affiliateId : "")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(currency)
                                .setUnitAmount(amountCents)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(course.getTitle() != null ? course.getTitle() : "Talnova course")
                                        .putMetadata("courseId", courseId)
                                        .build())
                                .build())
                        .build());

        // Store coupon code in session metadata for redemption on finalization
        if (couponCode != null && !couponCode.isBlank()) {
            paramsBuilder.putMetadata("couponCode", couponCode);
        }

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();
        Session session = Session.create(paramsBuilder.build(), requestOptions);

        EduCommissionCalculator.CommissionResult commData = commissionCalculator.calculateCommissionRate(course.getCreatorId());
        double rate = commData.rate;
        double fee = round2(finalPrice * rate);
        double creatorShare = round2(finalPrice - fee);

        ETransactions pending = ETransactions.builder()
                .buyerId(userId)
                .sellerId(course.getCreatorId())
                .courseId(courseId)
                .amount(finalPrice)
                .originalAmount(originalPrice)
                .discountAmount(discountAmount)
                .appliedCouponCode(couponCode)
                .currency(course.getCurrency() != null ? course.getCurrency() : "USD")
                .platformFee(fee)
                .commissionRate(rate)
                .creatorPlanAtPurchase(commData.plan)
                .creatorEarning(creatorShare)
                .paymentMethod(EPaymentMethod.STRIPE)
                .paymentStatus(EPaymentStatus.PENDING)
                .transactionId(session.getId())
                .stripeCheckoutSessionId(session.getId())
                .paymentGateway("stripe_checkout")
                .idempotencyKey(idempotencyKey)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .createdAt(Instant.now())
                .build();
        transactionsRepository.save(pending);

        return Map.of("url", session.getUrl(), "sessionId", session.getId());
    }

    /**
     * Starts Stripe Checkout for multiple courses (cart checkout); persists
     * individual PENDING transaction rows.
     */
    public Map<String, String> createMultiCourseCheckoutSession(String userId, List<String> courseIds, String couponCode)
            throws StripeException {
        List<ECourses> courses = (List<ECourses>) coursesRepository.findAllById(courseIds);
        if (courses.isEmpty()) {
            throw new EduBadRequestException("No valid courses found for checkout");
        }

        Map<String, Long> sellerCounts = courses.stream()
            .collect(java.util.stream.Collectors.groupingBy(ECourses::getCreatorId, java.util.stream.Collectors.counting()));
        fraudDetectionService.validateBulkPurchases(userId, sellerCounts);

        // Generate idempotency key for multi-course checkout
        String idempotencyKey = UUID.randomUUID().toString();

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/checkout/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cart")
                .putMetadata("type", "MULTI_COURSE_PURCHASE")
                .putMetadata("userId", userId)
                .putMetadata("courseIds", String.join(",", courseIds));

        // Store coupon code in session metadata for redemption on finalization
        if (couponCode != null && !couponCode.isBlank()) {
            paramsBuilder.putMetadata("couponCode", couponCode);
        }

        for (ECourses course : courses) {
            if (userId.equals(course.getCreatorId())) {
                throw new EduBadRequestException("You cannot buy your own course: " + course.getTitle());
            }
            double originalPrice = course.getPrice() != null ? course.getPrice() : 0.0;
            if (originalPrice <= 0)
                continue; // Skip free courses in Stripe session

            // Apply coupon per-course if applicable
            double finalPrice = originalPrice;
            if (couponCode != null && !couponCode.isBlank()) {
                try {
                    CouponValidationResult couponResult = couponService.validateAndCalculate(couponCode, course.getId(), userId, originalPrice);
                    finalPrice = couponResult.getFinalPrice();
                } catch (Exception e) {
                    // Coupon not valid for this specific course — use original price
                    log.debug("Coupon {} not applicable for course {}: {}", couponCode, course.getId(), e.getMessage());
                }
            }

            if (finalPrice <= 0) continue; // Skip if coupon makes the course free

            long amountCents = Math.round(finalPrice * 100);
            paramsBuilder.addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency((course.getCurrency() != null ? course.getCurrency() : "USD").toLowerCase())
                            .setUnitAmount(amountCents)
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(course.getTitle() != null ? course.getTitle() : "Talnova Course")
                                    .addImage(course.getThumbnail())
                                    .build())
                            .build())
                    .build());
        }

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();
        Session session = Session.create(paramsBuilder.build(), requestOptions);

        // Track individual transactions for each course in the checkout session
        for (ECourses course : courses) {
            double originalPrice = course.getPrice() != null ? course.getPrice() : 0.0;
            double discountAmount = 0.0;
            double finalPrice = originalPrice;

            if (couponCode != null && !couponCode.isBlank()) {
                try {
                    CouponValidationResult couponResult = couponService.validateAndCalculate(couponCode, course.getId(), userId, originalPrice);
                    discountAmount = couponResult.getDiscountAmount();
                    finalPrice = couponResult.getFinalPrice();
                } catch (Exception ignored) {
                    // Coupon not valid for this course — no discount
                }
            }

            EduCommissionCalculator.CommissionResult commData = commissionCalculator.calculateCommissionRate(course.getCreatorId());
            double rate = commData.rate;
            double fee = round2(finalPrice * rate);
            double creatorShare = round2(finalPrice - fee);

            ETransactions pending = ETransactions.builder()
                    .buyerId(userId)
                    .sellerId(course.getCreatorId())
                    .courseId(course.getId())
                    .amount(finalPrice)
                    .originalAmount(originalPrice)
                    .discountAmount(discountAmount)
                    .appliedCouponCode(couponCode)
                    .currency(course.getCurrency() != null ? course.getCurrency() : "USD")
                    .platformFee(fee)
                    .commissionRate(rate)
                    .creatorPlanAtPurchase(commData.plan)
                    .creatorEarning(creatorShare)
                    .paymentMethod(EPaymentMethod.STRIPE)
                    .paymentStatus(EPaymentStatus.PENDING)
                    .transactionId(session.getId() + "_" + course.getId())
                    .stripeCheckoutSessionId(session.getId())
                    .paymentGateway("stripe_multi_checkout")
                    .idempotencyKey(idempotencyKey)
                    .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                    .createdAt(Instant.now())
                    .build();
            transactionsRepository.save(pending);
        }

        return Map.of("url", session.getUrl(), "sessionId", session.getId());
    }

    /**
     * Idempotent: verifies Stripe session is paid, marks transaction SUCCESS,
     * creates enrollment if missing.
     * Used by webhooks and by the success page confirm call.
     */
    /**
     * Idempotent: verifies Stripe session is paid, marks transaction SUCCESS,
     * creates enrollment if missing.
     * Used by webhooks and by the success page confirm call.
     */
    @Transactional
    public void finalizePaidCourseIfReady(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            return;
        }
        Map<String, String> meta = session.getMetadata();
        if (meta == null)
            return;

        String type = meta.get("type");
        String userId = meta.get("userId");

        if (CHECKOUT_METADATA_TYPE.equals(type)) {
            String courseId = meta.get("courseId");
            if (userId == null || courseId == null)
                return;

            ETransactions tx = transactionsRepository.findByStripeCheckoutSessionId(sessionId)
                    .orElseThrow(() -> new EduResourceNotFoundException("Purchase record not found for session"));

            if (tx.getPaymentStatus() != EPaymentStatus.SUCCESS) {
                tx.setPaymentStatus(EPaymentStatus.SUCCESS);
                tx.setPaymentGatewayResponse("stripe_session:" + session.getId());
                
                String affiliateId = meta.get("affiliateId");
                if (affiliateId != null && !affiliateId.isEmpty()) {
                    EAffiliates affiliate = affiliatesRepository.findById(affiliateId).orElse(null);
                    if (affiliate != null) {
                        double price = tx.getAmount();
                        double affRate = affiliate.getCommissionRate() != null ? affiliate.getCommissionRate() : 0.20;
                        double affEarning = round2(price * affRate);
                        
                        tx.setAffiliateId(affiliateId);
                        tx.setAffiliateEarning(affEarning);
                        
                        // Recalculate Creator share
                        double newCreatorEarning = round2(tx.getCreatorEarning() - affEarning);
                        tx.setCreatorEarning(newCreatorEarning);
                        
                        // Affiliate Ledger
                        holdingLedgerRepository.save(EHoldingLedger.builder()
                                .beneficiaryId(affiliate.getUserId())
                                .beneficiaryType(com.talentboozt.s_backend.domains.edu.enums.EBeneficiaryType.AFFILIATE)
                                .transactionId(tx.getId())
                                .courseId(tx.getCourseId())
                                .amount(affEarning)
                                .currency(tx.getCurrency())
                                .status(EHoldingStatus.HELD)
                                .clearanceDate(Instant.now().plus(14, ChronoUnit.DAYS))
                                .createdAt(Instant.now())
                                .build());
                    }
                }
                
                tx.setUpdatedAt(Instant.now());
                transactionsRepository.save(tx);
                
                // Creator Ledger
                holdingLedgerRepository.save(EHoldingLedger.builder()
                        .beneficiaryId(tx.getSellerId())
                        .beneficiaryType(com.talentboozt.s_backend.domains.edu.enums.EBeneficiaryType.CREATOR)
                        .transactionId(tx.getId())
                        .courseId(tx.getCourseId())
                        .amount(tx.getCreatorEarning())
                        .currency(tx.getCurrency())
                        .status(EHoldingStatus.HELD)
                        .clearanceDate(Instant.now().plus(14, ChronoUnit.DAYS))
                        .createdAt(Instant.now())
                        .build());

                // Double-entry ledger
                ledgerService.recordPurchase(tx);

                // Affiliate ledger entry
                if (tx.getAffiliateId() != null && tx.getAffiliateEarning() != null && tx.getAffiliateEarning() > 0) {
                    EAffiliates aff = affiliatesRepository.findById(tx.getAffiliateId()).orElse(null);
                    if (aff != null) {
                        ledgerService.recordAffiliateCommission(tx.getId(), aff.getUserId(),
                                tx.getAffiliateEarning(), tx.getCurrency(), tx.getCourseId());
                    }
                }
            }
            enrollmentService.ensureEnrollmentAfterSuccessfulPurchase(userId, courseId);

            // Redeem coupon on successful payment
            String singleCoupon = meta.get("couponCode");
            if (singleCoupon != null && !singleCoupon.isBlank()) {
                couponService.redeemCoupon(singleCoupon, userId, tx.getId());
                log.info("Redeemed coupon {} for single-course purchase tx={}", singleCoupon, tx.getId());
            }

        } else if ("MULTI_COURSE_PURCHASE".equals(type)) {
            String courseIdsStr = meta.get("courseIds");
            if (userId == null || courseIdsStr == null)
                return;

            List<ETransactions> txs = transactionsRepository.findAllByStripeCheckoutSessionId(sessionId);
            for (ETransactions tx : txs) {
                if (tx.getPaymentStatus() != EPaymentStatus.SUCCESS) {
                    tx.setPaymentStatus(EPaymentStatus.SUCCESS);
                    tx.setPaymentGatewayResponse("stripe_multi_session:" + session.getId());
                    tx.setUpdatedAt(Instant.now());
                    transactionsRepository.save(tx);

                    EHoldingLedger hold = EHoldingLedger.builder()
                            .beneficiaryId(tx.getSellerId())
                            .beneficiaryType(com.talentboozt.s_backend.domains.edu.enums.EBeneficiaryType.CREATOR)
                            .transactionId(tx.getId())
                            .courseId(tx.getCourseId())
                            .amount(tx.getCreatorEarning())
                            .currency(tx.getCurrency())
                            .status(EHoldingStatus.HELD)
                            .clearanceDate(Instant.now().plus(14, ChronoUnit.DAYS))
                            .createdAt(Instant.now())
                            .build();
                    holdingLedgerRepository.save(hold);
                    
                    // Double-entry ledger
                    ledgerService.recordPurchase(tx);
                    
                    // Affiliate ledger entry (if applicable in future)
                    if (tx.getAffiliateId() != null && tx.getAffiliateEarning() != null && tx.getAffiliateEarning() > 0) {
                        EAffiliates aff = affiliatesRepository.findById(tx.getAffiliateId()).orElse(null);
                        if (aff != null) {
                            ledgerService.recordAffiliateCommission(tx.getId(), aff.getUserId(),
                                    tx.getAffiliateEarning(), tx.getCurrency(), tx.getCourseId());
                        }
                    }
                }
            }

            for (String cid : courseIdsStr.split(",")) {
                enrollmentService.ensureEnrollmentAfterSuccessfulPurchase(userId, cid);
            }

            // Redeem coupon once for the entire cart session
            String multiCoupon = meta.get("couponCode");
            if (multiCoupon != null && !multiCoupon.isBlank() && !txs.isEmpty()) {
                couponService.redeemCoupon(multiCoupon, userId, txs.get(0).getId());
                log.info("Redeemed coupon {} for multi-course purchase session={}", multiCoupon, sessionId);
            }

        } else if ("BUNDLE_PURCHASE".equals(type)) {
            String bundleId = meta.get("bundleId");
            String courseIdsStr = meta.get("courseIds");
            if (userId == null || courseIdsStr == null || bundleId == null)
                return;

            List<ETransactions> txs = transactionsRepository.findAllByStripeCheckoutSessionId(sessionId);
            for (ETransactions tx : txs) {
                if (tx.getPaymentStatus() != EPaymentStatus.SUCCESS) {
                    tx.setPaymentStatus(EPaymentStatus.SUCCESS);
                    tx.setPaymentGatewayResponse("stripe_bundle_session:" + session.getId());
                    tx.setUpdatedAt(Instant.now());
                    transactionsRepository.save(tx);

                    // Creator Ledger per-course
                    holdingLedgerRepository.save(EHoldingLedger.builder()
                            .beneficiaryId(tx.getSellerId())
                            .beneficiaryType(com.talentboozt.s_backend.domains.edu.enums.EBeneficiaryType.CREATOR)
                            .transactionId(tx.getId())
                            .courseId(tx.getCourseId())
                            .amount(tx.getCreatorEarning())
                            .currency(tx.getCurrency())
                            .status(EHoldingStatus.HELD)
                            .clearanceDate(Instant.now().plus(14, ChronoUnit.DAYS))
                            .createdAt(Instant.now())
                            .build());
                    
                    // Double-entry ledger
                    ledgerService.recordPurchase(tx);
                    
                    // Affiliate ledger entry (if applicable in future)
                    if (tx.getAffiliateId() != null && tx.getAffiliateEarning() != null && tx.getAffiliateEarning() > 0) {
                        EAffiliates aff = affiliatesRepository.findById(tx.getAffiliateId()).orElse(null);
                        if (aff != null) {
                            ledgerService.recordAffiliateCommission(tx.getId(), aff.getUserId(),
                                    tx.getAffiliateEarning(), tx.getCurrency(), tx.getCourseId());
                        }
                    }
                }
            }

            // Enroll in all bundle courses (including already-owned — idempotent)
            for (String cid : courseIdsStr.split(",")) {
                enrollmentService.ensureEnrollmentAfterSuccessfulPurchase(userId, cid.trim());
            }

            // Increment bundle sales counter
            bundlesRepository.findById(bundleId).ifPresent(bundle -> {
                bundle.setTotalSales((bundle.getTotalSales() != null ? bundle.getTotalSales() : 0) + 1);
                bundlesRepository.save(bundle);
            });

            // Redeem coupon once for bundle session
            String bundleCoupon = meta.get("couponCode");
            if (bundleCoupon != null && !bundleCoupon.isBlank() && !txs.isEmpty()) {
                couponService.redeemCoupon(bundleCoupon, userId, txs.get(0).getId());
                log.info("Redeemed coupon {} for bundle purchase session={}", bundleCoupon, sessionId);
            }

            log.info("Finalized bundle purchase: bundleId={}, user={}, courses={}, txCount={}",
                    bundleId, userId, courseIdsStr, txs.size());
        }
    }

    /**
     * Webhook/Success entry point with concurrency protection.
     */
    @Transactional
    public void secureFinalizePaidCourse(String sessionId) {
        try {
            finalizePaidCourseIfReady(sessionId);
        } catch (OptimisticLockingFailureException e) {
            // Concurrent process already handled this - safe to ignore
        } catch (Exception e) {
            throw new RuntimeException("Finalization failed", e);
        }
    }

    /** Confirms the caller owns the session metadata userId before finalizing. */
    @Transactional
    public Map<String, Object> confirmForUser(String sessionId, String expectedUserId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        Map<String, String> meta = session.getMetadata();
        if (meta == null) {
            throw new EduBadRequestException("Invalid checkout session");
        }
        String type = meta.get("type");
        if (!CHECKOUT_METADATA_TYPE.equals(type) && !"MULTI_COURSE_PURCHASE".equals(type)
                && !"BUNDLE_PURCHASE".equals(type)) {
            throw new EduBadRequestException("Invalid checkout session type");
        }
        if (!expectedUserId.equals(meta.get("userId"))) {
            throw new EduAccessDeniedException("Session does not belong to this user");
        }

        finalizePaidCourseIfReady(sessionId);

        // Verify if finalizing worked (Stripe status must be paid)
        if (!"paid".equalsIgnoreCase(session.getPaymentStatus())) {
            throw new EduBadRequestException(
                    "Payment is not complete yet. If you were charged, wait a few seconds and retry.");
        }

        return Map.of(
                "success", true,
                "type", type,
                "courseId", meta.get("courseId") != null ? meta.get("courseId") : "",
                "bundleId", meta.get("bundleId") != null ? meta.get("bundleId") : "");
    }

    /**
     * Marks all PENDING transactions for an expired Stripe session as EXPIRED.
     * Called from webhook handler when Stripe sends checkout.session.expired.
     * This prevents stale PENDING records from blocking future purchase attempts.
     */
    public void markSessionExpired(String sessionId) {
        // Handle single-course checkout
        transactionsRepository.findByStripeCheckoutSessionId(sessionId).ifPresent(tx -> {
            if (tx.getPaymentStatus() == EPaymentStatus.PENDING) {
                tx.setPaymentStatus(EPaymentStatus.EXPIRED);
                tx.setUpdatedAt(Instant.now());
                transactionsRepository.save(tx);
            }
        });

        // Handle multi-course checkout
        List<ETransactions> multiTxs = transactionsRepository.findAllByStripeCheckoutSessionId(sessionId);
        for (ETransactions tx : multiTxs) {
            if (tx.getPaymentStatus() == EPaymentStatus.PENDING) {
                tx.setPaymentStatus(EPaymentStatus.EXPIRED);
                tx.setUpdatedAt(Instant.now());
                transactionsRepository.save(tx);
            }
        }
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    // ── Bundle Purchase Flow ───────────────────────────────────────

    /**
     * Creates a Stripe Checkout session for a bundle purchase.
     *
     * Key behaviors:
     * - Deducts already-owned courses (user only pays for unenrolled courses)
     * - Charges proportional bundle price (bundle discount distributed across courses)
     * - Creates per-course PENDING transactions with bundleId
     * - Stores all metadata for webhook finalization
     *
     * @param userId    buyer
     * @param bundleId  the bundle being purchased
     * @param couponCode optional coupon
     * @return Map with "url" (Stripe Checkout URL) and "sessionId"
     */
    public Map<String, String> createBundleCheckoutSession(String userId, String bundleId, String couponCode)
            throws StripeException {
        EBundles bundle = bundlesRepository.findById(bundleId)
                .orElseThrow(() -> new EduResourceNotFoundException("Bundle not found: " + bundleId));

        if (!"ACTIVE".equals(bundle.getStatus())) {
            throw new EduBadRequestException("This bundle is not available for purchase.");
        }
        if (bundle.getCourseIds() == null || bundle.getCourseIds().length == 0) {
            throw new EduBadRequestException("Bundle contains no courses.");
        }

        // Fetch all courses in the bundle
        List<String> allCourseIds = List.of(bundle.getCourseIds());
        List<ECourses> allCourses = (List<ECourses>) coursesRepository.findAllById(allCourseIds);
        if (allCourses.isEmpty()) {
            throw new EduBadRequestException("No valid courses found in this bundle.");
        }

        // Self-purchase check
        for (ECourses course : allCourses) {
            if (userId.equals(course.getCreatorId())) {
                throw new EduBadRequestException("You cannot buy a bundle containing your own course: " + course.getTitle());
            }
        }

        // Ownership deduction: filter out courses the user already owns
        List<ECourses> unenrolledCourses = allCourses.stream()
                .filter(c -> enrollmentsRepository.findByUserIdAndCourseId(userId, c.getId()).isEmpty())
                .toList();

        if (unenrolledCourses.isEmpty()) {
            throw new EduBadRequestException("You already own all courses in this bundle.");
        }

        // Calculate effective bundle price after ownership deduction
        double originalTotal = bundle.getOriginalTotalPrice() != null ? bundle.getOriginalTotalPrice() : 0.0;
        double bundlePrice = bundle.getBundlePrice() != null ? bundle.getBundlePrice() : originalTotal;

        // Proportional pricing: distribute bundle discount across unenrolled courses
        double unenrolledOriginalTotal = unenrolledCourses.stream()
                .mapToDouble(c -> c.getPrice() != null ? c.getPrice() : 0.0)
                .sum();

        // If user owns some courses, adjust effective price proportionally
        double effectiveBundlePrice;
        if (allCourses.size() == unenrolledCourses.size()) {
            // User owns nothing — full bundle price
            effectiveBundlePrice = bundlePrice;
        } else {
            // User owns some — proportional price based on remaining courses
            double discountRatio = originalTotal > 0 ? bundlePrice / originalTotal : 1.0;
            effectiveBundlePrice = round2(unenrolledOriginalTotal * discountRatio);
        }

        // Apply coupon if provided
        double discountAmount = 0.0;
        double finalTotalPrice = effectiveBundlePrice;
        if (couponCode != null && !couponCode.isBlank()) {
            // Validate coupon against first course (bundle-level)
            try {
                CouponValidationResult couponResult = couponService.validateAndCalculate(
                        couponCode, unenrolledCourses.get(0).getId(), userId, effectiveBundlePrice);
                discountAmount = couponResult.getDiscountAmount();
                finalTotalPrice = couponResult.getFinalPrice();
                log.info("Coupon {} applied to bundle: effective={}, discount={}, final={}",
                        couponCode, effectiveBundlePrice, discountAmount, finalTotalPrice);
            } catch (Exception e) {
                log.debug("Coupon {} not applicable for bundle: {}", couponCode, e.getMessage());
            }
        }

        if (finalTotalPrice <= 0) {
            throw new EduBadRequestException("Bundle price after discounts is zero — contact support.");
        }

        String idempotencyKey = UUID.randomUUID().toString();
        String courseIdsJoined = unenrolledCourses.stream().map(ECourses::getId).reduce((a, b) -> a + "," + b).orElse("");

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/checkout/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/bundles/" + bundleId)
                .putMetadata("type", "BUNDLE_PURCHASE")
                .putMetadata("userId", userId)
                .putMetadata("bundleId", bundleId)
                .putMetadata("courseIds", courseIdsJoined);

        if (couponCode != null && !couponCode.isBlank()) {
            paramsBuilder.putMetadata("couponCode", couponCode);
        }

        // Add each unenrolled course as a line item with proportional pricing
        for (ECourses course : unenrolledCourses) {
            double courseOriginalPrice = course.getPrice() != null ? course.getPrice() : 0.0;
            if (courseOriginalPrice <= 0) continue;

            // Proportional share: (coursePrice / unenrolledTotal) * finalTotalPrice
            double proportionalPrice = unenrolledOriginalTotal > 0
                    ? round2((courseOriginalPrice / unenrolledOriginalTotal) * finalTotalPrice)
                    : 0.0;

            long amountCents = Math.max(50L, Math.round(proportionalPrice * 100));
            String currency = (course.getCurrency() != null ? course.getCurrency() : "USD").toLowerCase();

            paramsBuilder.addLineItem(SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(currency)
                            .setUnitAmount(amountCents)
                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(course.getTitle() != null ? course.getTitle() : "Talnova Course")
                                    .setDescription("Part of bundle: " + bundle.getName())
                                    .build())
                            .build())
                    .build());
        }

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();
        Session session = Session.create(paramsBuilder.build(), requestOptions);

        // Create per-course PENDING transactions
        for (ECourses course : unenrolledCourses) {
            double courseOriginalPrice = course.getPrice() != null ? course.getPrice() : 0.0;
            if (courseOriginalPrice <= 0) continue;

            double proportionalPrice = unenrolledOriginalTotal > 0
                    ? round2((courseOriginalPrice / unenrolledOriginalTotal) * finalTotalPrice)
                    : 0.0;
            double courseDiscount = unenrolledOriginalTotal > 0
                    ? round2((courseOriginalPrice / unenrolledOriginalTotal) * (effectiveBundlePrice - finalTotalPrice + (courseOriginalPrice - (courseOriginalPrice / unenrolledOriginalTotal) * effectiveBundlePrice)))
                    : 0.0;

            EduCommissionCalculator.CommissionResult commData = commissionCalculator.calculateCommissionRate(course.getCreatorId());
            double rate = commData.rate;
            double fee = round2(proportionalPrice * rate);
            double creatorShare = round2(proportionalPrice - fee);

            ETransactions pending = ETransactions.builder()
                    .buyerId(userId)
                    .sellerId(course.getCreatorId())
                    .courseId(course.getId())
                    .bundleId(bundleId)
                    .amount(proportionalPrice)
                    .originalAmount(courseOriginalPrice)
                    .discountAmount(round2(courseOriginalPrice - proportionalPrice))
                    .appliedCouponCode(couponCode)
                    .currency(course.getCurrency() != null ? course.getCurrency() : "USD")
                    .platformFee(fee)
                    .commissionRate(rate)
                    .creatorPlanAtPurchase(commData.plan)
                    .creatorEarning(creatorShare)
                    .paymentMethod(EPaymentMethod.STRIPE)
                    .paymentStatus(EPaymentStatus.PENDING)
                    .transactionId(session.getId() + "_" + course.getId())
                    .stripeCheckoutSessionId(session.getId())
                    .paymentGateway("stripe_bundle_checkout")
                    .idempotencyKey(idempotencyKey)
                    .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                    .createdAt(Instant.now())
                    .build();
            transactionsRepository.save(pending);
        }

        int ownedCount = allCourses.size() - unenrolledCourses.size();
        log.info("Bundle checkout created: bundle={}, user={}, totalCourses={}, owned={}, charging={}, price={}",
                bundleId, userId, allCourses.size(), ownedCount, unenrolledCourses.size(), finalTotalPrice);

        return Map.of("url", session.getUrl(), "sessionId", session.getId(),
                "coursesOwned", String.valueOf(ownedCount),
                "coursesCharging", String.valueOf(unenrolledCourses.size()));
    }
}
