package com.talentboozt.s_backend.domains.edu.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.talentboozt.s_backend.domains.edu.enums.EPaymentMethod;
import com.talentboozt.s_backend.domains.edu.enums.EPaymentStatus;
import com.talentboozt.s_backend.domains.edu.exception.EduAccessDeniedException;
import com.talentboozt.s_backend.domains.edu.exception.EduBadRequestException;
import com.talentboozt.s_backend.domains.edu.exception.EduResourceNotFoundException;
import com.talentboozt.s_backend.domains.edu.model.ETransactions;
import com.talentboozt.s_backend.domains.edu.model.ECourses;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECoursesRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ETransactionsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import com.talentboozt.s_backend.domains.edu.enums.EHoldingStatus;
import com.talentboozt.s_backend.domains.edu.model.EHoldingLedger;
import com.talentboozt.s_backend.domains.edu.model.EAffiliates;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EHoldingLedgerRepository;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EAffiliatesRepository;

@Service
public class EduCoursePurchaseService {

    public static final String CHECKOUT_METADATA_TYPE = "EDU_COURSE_PURCHASE";

    private final ECoursesRepository coursesRepository;
    private final ETransactionsRepository transactionsRepository;
    private final EduEnrollmentService enrollmentService;
    private final EduCommissionCalculator commissionCalculator;
    private final EHoldingLedgerRepository holdingLedgerRepository;
    private final EduFraudDetectionService fraudDetectionService;
    private final EAffiliatesRepository affiliatesRepository;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public EduCoursePurchaseService(ECoursesRepository coursesRepository,
            ETransactionsRepository transactionsRepository,
            EduEnrollmentService enrollmentService,
            EduCommissionCalculator commissionCalculator,
            EHoldingLedgerRepository holdingLedgerRepository,
            EduFraudDetectionService fraudDetectionService,
            EAffiliatesRepository affiliatesRepository) {
        this.coursesRepository = coursesRepository;
        this.transactionsRepository = transactionsRepository;
        this.enrollmentService = enrollmentService;
        this.commissionCalculator = commissionCalculator;
        this.holdingLedgerRepository = holdingLedgerRepository;
        this.fraudDetectionService = fraudDetectionService;
        this.affiliatesRepository = affiliatesRepository;
    }

    /**
     * Starts Stripe Checkout for a paid marketplace course; persists a PENDING
     * transaction row.
     */
    public Map<String, String> createCourseCheckoutSession(String userId, String courseId, String affiliateId) throws StripeException {
        ECourses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new EduResourceNotFoundException("Course not found"));
        
        fraudDetectionService.validateBulkPurchases(userId, Map.of(course.getCreatorId(), 1L));
        
        if (!Boolean.TRUE.equals(course.getPublished())) {
            throw new EduBadRequestException("Course is not available for purchase");
        }
        if (Boolean.TRUE.equals(course.getIsPrivate())) {
            throw new EduBadRequestException("This course is not sold on the marketplace");
        }
        double price = course.getPrice() != null ? course.getPrice() : 0.0;
        if (price <= 0) {
            throw new EduBadRequestException("This course is free — enroll without payment.");
        }
        if (userId.equals(course.getCreatorId())) {
            throw new EduBadRequestException("You cannot buy your own course");
        }

        String currency = (course.getCurrency() != null ? course.getCurrency() : "USD").toLowerCase();
        long amountCents = Math.max(50L, Math.round(price * 100));

        SessionCreateParams params = SessionCreateParams.builder()
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
                        .build())
                .build();

        Session session = Session.create(params);

        EduCommissionCalculator.CommissionResult commData = commissionCalculator.calculateCommissionRate(course.getCreatorId());
        double rate = commData.rate;
        double fee = round2(price * rate);
        double creatorShare = round2(price - fee);

        ETransactions pending = ETransactions.builder()
                .buyerId(userId)
                .sellerId(course.getCreatorId())
                .courseId(courseId)
                .amount(price)
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
                .createdAt(Instant.now())
                .build();
        transactionsRepository.save(pending);

        return Map.of("url", session.getUrl(), "sessionId", session.getId());
    }

    /**
     * Starts Stripe Checkout for multiple courses (cart checkout); persists
     * individual PENDING transaction rows.
     */
    public Map<String, String> createMultiCourseCheckoutSession(String userId, List<String> courseIds)
            throws StripeException {
        List<ECourses> courses = (List<ECourses>) coursesRepository.findAllById(courseIds);
        if (courses.isEmpty()) {
            throw new EduBadRequestException("No valid courses found for checkout");
        }

        Map<String, Long> sellerCounts = courses.stream()
            .collect(java.util.stream.Collectors.groupingBy(ECourses::getCreatorId, java.util.stream.Collectors.counting()));
        fraudDetectionService.validateBulkPurchases(userId, sellerCounts);

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(frontendUrl + "/checkout/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cart")
                .putMetadata("type", "MULTI_COURSE_PURCHASE")
                .putMetadata("userId", userId)
                .putMetadata("courseIds", String.join(",", courseIds));

        for (ECourses course : courses) {
            if (userId.equals(course.getCreatorId())) {
                throw new EduBadRequestException("You cannot buy your own course: " + course.getTitle());
            }
            double price = course.getPrice() != null ? course.getPrice() : 0.0;
            if (price <= 0)
                continue; // Skip free courses in Stripe session

            long amountCents = Math.round(price * 100);
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

        Session session = Session.create(paramsBuilder.build());

        // Track individual transactions for each course in the checkout session
        for (ECourses course : courses) {
            double price = course.getPrice() != null ? course.getPrice() : 0.0;
            EduCommissionCalculator.CommissionResult commData = commissionCalculator.calculateCommissionRate(course.getCreatorId());
            double rate = commData.rate;
            double fee = round2(price * rate);
            double creatorShare = round2(price - fee);

            ETransactions pending = ETransactions.builder()
                    .buyerId(userId)
                    .sellerId(course.getCreatorId())
                    .courseId(course.getId())
                    .amount(price)
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
            }
            enrollmentService.ensureEnrollmentAfterSuccessfulPurchase(userId, courseId);

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
                }
            }

            for (String cid : courseIdsStr.split(",")) {
                enrollmentService.ensureEnrollmentAfterSuccessfulPurchase(userId, cid);
            }
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
        if (!CHECKOUT_METADATA_TYPE.equals(type) && !"MULTI_COURSE_PURCHASE".equals(type)) {
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
                "courseId", meta.get("courseId") != null ? meta.get("courseId") : "");
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
