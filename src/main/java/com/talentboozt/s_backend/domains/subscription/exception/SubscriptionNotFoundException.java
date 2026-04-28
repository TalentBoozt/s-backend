package com.talentboozt.s_backend.domains.subscription.exception;

public class SubscriptionNotFoundException extends SubscriptionException {
    public SubscriptionNotFoundException(String userId) {
        super("Subscription not found for user: " + userId);
    }
}
