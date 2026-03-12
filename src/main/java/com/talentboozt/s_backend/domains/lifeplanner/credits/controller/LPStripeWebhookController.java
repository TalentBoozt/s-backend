package com.talentboozt.s_backend.domains.lifeplanner.credits.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.talentboozt.s_backend.domains.lifeplanner.credits.model.SubscriptionTier;
import com.talentboozt.s_backend.domains.lifeplanner.credits.service.LifePlannerCreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/lifeplanner/stripe")
@RequiredArgsConstructor
public class LPStripeWebhookController {

    private final LifePlannerCreditService lifePlannerCreditService;

    @Value("${STRIPE_TEST_WEBHOOK_SECRET}")
    private String endpointSecret;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            log.error("Stripe webhook signature verification failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Signature Verification Failed");
        } catch (Exception e) {
            log.error("Stripe webhook processing failed", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook Error");
        }

        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) Objects.requireNonNull(event.getDataObjectDeserializer().getObject().orElse(null));
            String userId = session.getClientReferenceId();
            if (userId != null) {
                try {
                    String tierString = session.getMetadata().get("tier");
                    if (tierString != null) {
                        SubscriptionTier tier = SubscriptionTier.valueOf(tierString);
                        lifePlannerCreditService.upgradeTier(userId, tier);
                        log.info("Successfully upgraded user {} to tier {}", userId, tier);
                    }
                } catch (Exception e) {
                    log.error("Failed to extract tier info from checkout session for user {}", userId, e);
                }
            }
        }

        return ResponseEntity.ok("Success");
    }
}
