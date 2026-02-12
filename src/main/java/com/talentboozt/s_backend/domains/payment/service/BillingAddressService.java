package com.talentboozt.s_backend.domains.payment.service;

import com.talentboozt.s_backend.domains.payment.model.BillingAddressModel;
import com.talentboozt.s_backend.domains.payment.repository.mongodb.BillingAddressRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BillingAddressService {

    @Autowired
    BillingAddressRepository billingAddressRepository;

    public BillingAddressModel updateBillingAddress(String companyId, BillingAddressModel address) {
        List<BillingAddressModel> addresses = billingAddressRepository.findByCompanyId(companyId);
        if (addresses.size() > 0) {
            addresses.get(0).setStreet(address.getStreet());
            addresses.get(0).setCity(address.getCity());
            addresses.get(0).setState(address.getState());
            addresses.get(0).setPostal_code(address.getPostal_code());
            addresses.get(0).setCountry(address.getCountry());
            return billingAddressRepository.save(Objects.requireNonNull(addresses.get(0)));
        }
        return billingAddressRepository.save(Objects.requireNonNull(address));
    }
}
