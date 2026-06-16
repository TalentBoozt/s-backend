package com.talentboozt.s_backend.shared.subscription.exception;

public class SubscriptionNotFoundException extends SubscriptionException {
    public SubscriptionNotFoundException(String userId) {
        super("Subscription not found for user: " + userId);
    }
}
