package com.talentboozt.s_backend.domains.lifeplanner.credits.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.talentboozt.s_backend.domains.lifeplanner.credits.model.SubscriptionTier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LPStripeService {

    @Value("${lifeplanner.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${STRIPE_TEST_PRO_PRICE_ID:price_1QbqWDAcO7lOddOXD2ZYc1QI}")
    private String proPriceId;

    @Value("${STRIPE_TEST_PREMIUM_PRICE_ID:price_1QaJdzAcO7lOddOXGZLxj0Gm}")
    private String premiumPriceId;

    public String createCheckoutSession(String userId, SubscriptionTier targetTier) throws StripeException {
        String priceId = targetTier == SubscriptionTier.PREMIUM ? premiumPriceId : proPriceId;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(frontendUrl + "/dashboard?payment=success")
                .setCancelUrl(frontendUrl + "/dashboard?payment=cancelled")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPrice(priceId)
                                .build()
                )
                .putMetadata("userId", userId)
                .putMetadata("tier", targetTier.name())
                .setClientReferenceId(userId)
                .build();

        Session session = Session.create(params);
        return session.getUrl();
    }
}
