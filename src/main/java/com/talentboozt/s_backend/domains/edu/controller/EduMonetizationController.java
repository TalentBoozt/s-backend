package com.talentboozt.s_backend.domains.edu.controller;

import com.stripe.exception.StripeException;
import com.talentboozt.s_backend.domains.edu.dto.monetization.CheckoutRequest;
import com.talentboozt.s_backend.domains.edu.dto.monetization.CourseCheckoutRequest;
import com.talentboozt.s_backend.domains.edu.dto.monetization.CoursePurchaseConfirmRequest;
import com.talentboozt.s_backend.domains.edu.dto.monetization.PortalRequest;
import com.talentboozt.s_backend.domains.edu.dto.monetization.MultiCourseCheckoutRequest;
import com.talentboozt.s_backend.domains.edu.model.ESubscriptions;
import com.talentboozt.s_backend.domains.edu.service.EduCoursePurchaseService;
import com.talentboozt.s_backend.domains.edu.service.EduMonetizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/monetization")
public class EduMonetizationController {

    private final EduMonetizationService monetizationService;
    private final EduCoursePurchaseService coursePurchaseService;

    public EduMonetizationController(EduMonetizationService monetizationService,
            EduCoursePurchaseService coursePurchaseService) {
        this.monetizationService = monetizationService;
        this.coursePurchaseService = coursePurchaseService;
    }

    @PostMapping("/stripe/checkout")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody CheckoutRequest request) throws Exception {
        return ResponseEntity.ok(monetizationService.createCheckoutSession(request));
    }

    @PostMapping("/stripe/portal")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> createPortalSession(@RequestBody PortalRequest request) throws Exception {
        return ResponseEntity.ok(monetizationService.createPortalSession(request.getUserId()));
    }

    @GetMapping("/subscriptions/user/{userId}")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<ESubscriptions> getSubscriptionStatus(@PathVariable String userId) {
        return ResponseEntity.ok(monetizationService.getSubscriptionStatus(userId));
    }

    /** One-time Stripe Checkout for a marketplace course (payment mode). */
    @PostMapping("/course-checkout")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> createCourseCheckout(@RequestBody CourseCheckoutRequest request)
            throws StripeException {
        return ResponseEntity.ok(coursePurchaseService.createCourseCheckoutSession(
                request.getUserId(), request.getCourseId()));
    }

    @PostMapping("/multi-course-checkout")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, String>> createMultiCourseCheckout(@RequestBody MultiCourseCheckoutRequest request)
            throws StripeException {
        return ResponseEntity.ok(coursePurchaseService.createMultiCourseCheckoutSession(
                request.getUserId(), request.getCourseIds()));
    }

    /** Browser return: confirms session belongs to user and completes enrollment if webhook was delayed. */
    @PostMapping("/course-purchase/confirm")
    @PreAuthorize("hasAuthority('LEARNER') or hasAuthority('INSTRUCTOR') or hasAuthority('CREATOR')")
    public ResponseEntity<Map<String, Object>> confirmCoursePurchase(@RequestBody CoursePurchaseConfirmRequest request)
            throws StripeException {
        return ResponseEntity.ok(coursePurchaseService.confirmForUser(
                request.getSessionId(), request.getUserId()));
    }
}
