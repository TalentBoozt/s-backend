package com.talentboozt.s_backend.domains.payment.service;

import com.talentboozt.s_backend.domains.com_job_portal.model.CompanyModel;
import com.talentboozt.s_backend.domains.com_job_portal.repository.mongodb.CompanyRepository;
import com.talentboozt.s_backend.domains.payment.model.BillingHistoryModel;
import com.talentboozt.s_backend.domains.payment.model.SubscriptionsModel;
import com.talentboozt.s_backend.domains.payment.repository.mongodb.BillingHistoryRepository;
import com.talentboozt.s_backend.domains.payment.repository.mongodb.SubscriptionRepository;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private BillingHistoryRepository billingHistoryRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public boolean isExempt(String companyId) {
        if (companyId == null)
            return false;
        return companyRepository.findById(companyId)
                .map(CompanyModel::isSystemOwner)
                .orElse(false);
    }

    public SubscriptionsModel getSubscription(String companyId) {
        if (isExempt(companyId)) {
            SubscriptionsModel systemOwnerSub = new SubscriptionsModel();
            systemOwnerSub.setPlan_name("System Owner (Root)");
            systemOwnerSub.set_active(true);
            systemOwnerSub.setCost("0");
            systemOwnerSub.setBilling_cycle("PERPETUAL");
            systemOwnerSub.setCompanyId(companyId);
            return systemOwnerSub;
        }
        return subscriptionRepository.findByCompanyId(companyId);
    }

    public SubscriptionsModel updateSubscription(String companyId, SubscriptionsModel subscription) {
        SubscriptionsModel existingSubscription = subscriptionRepository.findByCompanyId(companyId);
        if (existingSubscription != null) {
            existingSubscription.setPlan_name(subscription.getPlan_name());
            existingSubscription.setCost(subscription.getCost());
            existingSubscription.setBilling_cycle(subscription.getBilling_cycle());
            existingSubscription.setStart_date(subscription.getStart_date());
            existingSubscription.setEnd_date(subscription.getEnd_date());
            existingSubscription.set_active(subscription.is_active());
            return subscriptionRepository.save(existingSubscription);
        } else {
            if (companyId != null) {
                subscription.setCompanyId(companyId);
                return subscriptionRepository.save(subscription);
            }
            return null;
        }
    }

    public void updateBillingHistory(String subscriptionId, String amountPaid, String status) {
        SubscriptionsModel subscription = subscriptionRepository.findBySubscriptionId(subscriptionId).orElse(null);
        if (subscription != null) {
            BillingHistoryModel billingHistory = new BillingHistoryModel();
            billingHistory.setCompanyId(subscription.getCompanyId());
            billingHistory.setAmount(amountPaid);
            billingHistory.setDate(new Date().toString());
            billingHistory.setInvoice_id(UUID.randomUUID().toString());
            billingHistory.setStatus(status);
            billingHistoryRepository.save(billingHistory);
        }
    }

    public void updateSubscriptionDetails(Subscription subscription) {
        SubscriptionsModel existing = subscriptionRepository.findBySubscriptionId(subscription.getId()).orElse(null);
        if (existing != null) {
            List<SubscriptionItem> items = subscription.getItems().getData();
            if (!items.isEmpty()) {
                existing.setPlan_name(items.get(0).getPlan().getNickname());
                existing.setCost(String.valueOf(items.get(0).getPlan().getAmount() / 100.0));
            }
            existing.setBilling_cycle(subscription.getBillingCycleAnchor().toString());
            existing.setStart_date(subscription.getCurrentPeriodStart().toString());
            existing.setEnd_date(subscription.getCurrentPeriodEnd().toString());
            subscriptionRepository.save(existing);
        }
    }

    public void markAsInactive(String subscriptionId) {
        SubscriptionsModel subscription = subscriptionRepository.findById(Objects.requireNonNull(subscriptionId))
                .orElse(null);
        if (subscription != null) {
            subscription.set_active(false);
            subscriptionRepository.save(subscription);
        }
    }
}
