package com.talentboozt.s_backend.domains.plat_job_portal.service;

import com.talentboozt.s_backend.domains.plat_job_portal.model.PreOrderModel;
import com.talentboozt.s_backend.domains.plat_job_portal.repository.PreOrderRepository;
import com.talentboozt.s_backend.shared.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PreOrderService {

    @Autowired
    private PreOrderRepository preOrderRepository;

    @Autowired
    private EmailService emailService;

    public PreOrderModel addPreOrder(PreOrderModel preOrderModel) throws IOException {
        this.preOrderRepository.save(Objects.requireNonNull(preOrderModel));
        emailService.sendPreOrderSuccess(preOrderModel.getEmail());
        return preOrderModel;
    }

    public PreOrderModel updatePreOrder(PreOrderModel preOrderModel, String id) {
        Optional<PreOrderModel> preOrder = this.preOrderRepository.findById(Objects.requireNonNull(id));
        if (preOrder.isPresent()) {
            PreOrderModel existingPreOrder = preOrder.get();
            existingPreOrder.setName(preOrderModel.getName());
            existingPreOrder.setEmail(preOrderModel.getEmail());
            existingPreOrder.setProduct(preOrderModel.getProduct());
            existingPreOrder.setDate(preOrderModel.getDate());
            return this.preOrderRepository.save(existingPreOrder);
        }
        return null;
    }

    public ResponseEntity<String> deletePreOrder(String id) {
        Optional<PreOrderModel> preOrder = this.preOrderRepository.findById(Objects.requireNonNull(id));
        if (preOrder.isPresent()) {
            this.preOrderRepository.delete(Objects.requireNonNull(preOrder.get()));
            return ResponseEntity.ok("PreOrder deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    public List<PreOrderModel> getPreOrders() {
        return this.preOrderRepository.findAll();
    }

    public PreOrderModel getPreOrder(String id) {
        Optional<PreOrderModel> preOrder = this.preOrderRepository.findById(Objects.requireNonNull(id));
        return preOrder.orElse(null);
    }
}
