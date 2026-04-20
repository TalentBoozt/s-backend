package com.talentboozt.s_backend.domains.edu.service;

import java.util.HashMap;
import java.util.Map;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.model.Transfer;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.TransferCreateParams;
import com.talentboozt.s_backend.domains.edu.model.ECreatorFinanceSettings;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.ECreatorFinanceSettingsRepository;

@Service
public class StripeConnectService {
    // private static final Logger log =
    // LoggerFactory.getLogger(StripeConnectService.class);

    private final ECreatorFinanceSettingsRepository financeSettingsRepository;

    @Value("${app.frontend.url:https://edu.talnova.io}")
    private String frontendUrl;

    public StripeConnectService(ECreatorFinanceSettingsRepository financeSettingsRepository) {
        this.financeSettingsRepository = financeSettingsRepository;
    }

    /**
     * Creates a Stripe Connect Express account for a creator.
     */
    public String createStripeAccount(String creatorId, String email) throws StripeException {
        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setEmail(email)
                .build();

        Account account = Account.create(params);

        // Save account ID
        ECreatorFinanceSettings settings = financeSettingsRepository.findByUserId(creatorId)
                .orElseGet(() -> ECreatorFinanceSettings.builder().userId(creatorId).build());

        settings.setStripeAccountId(account.getId());
        financeSettingsRepository.save(settings);

        return account.getId();
    }

    /**
     * Creates an onboarding link for the creator.
     */
    public String createOnboardingLink(String stripeAccountId) throws StripeException {
        AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                .setAccount(stripeAccountId)
                .setRefreshUrl(frontendUrl + "/settings/payouts?refresh=true")
                .setReturnUrl(frontendUrl + "/settings/payouts?success=true")
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        AccountLink accountLink = AccountLink.create(params);
        return accountLink.getUrl();
    }

    /**
     * Initiates a transfer from platform to connected account.
     */
    public String transferFunds(String stripeAccountId, double amount, String currency, String payoutId)
            throws StripeException {
        long amountCents = Math.round(amount * 100);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("payoutId", payoutId);

        TransferCreateParams params = TransferCreateParams.builder()
                .setAmount(amountCents)
                .setCurrency(currency.toLowerCase())
                .setDestination(stripeAccountId)
                .putAllMetadata(metadata)
                .build();

        Transfer transfer = Transfer.create(params);
        return transfer.getId();
    }
}
