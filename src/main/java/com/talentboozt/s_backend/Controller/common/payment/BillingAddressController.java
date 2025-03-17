package com.talentboozt.s_backend.Controller.common.payment;

import com.talentboozt.s_backend.Model.common.payment.BillingAddressModel;
import com.talentboozt.s_backend.Service.common.payment.BillingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/billing-address")
public class BillingAddressController {

    @Autowired
    BillingAddressService billingAddressService;

    @PutMapping("/update/{companyId}")
    public BillingAddressModel updateBillingAddress(@PathVariable String companyId, @RequestBody BillingAddressModel billingAddress) {
        return billingAddressService.updateBillingAddress(companyId, billingAddress);
    }
}
