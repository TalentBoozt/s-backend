package com.talentboozt.s_backend.Service.PLAT_JOB_PORTAL;

import com.talentboozt.s_backend.Model.PLAT_JOB_PORTAL.PreOrderModel;
import com.talentboozt.s_backend.Repository.PLAT_JOB_PORTAL.PreOrderRepository;
import com.talentboozt.s_backend.Service.common.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreOrderService {

    @Autowired
    private PreOrderRepository preOrderRepository;

    @Autowired
    private EmailService emailService;

    public PreOrderModel addPreOrder(PreOrderModel preOrderModel) {
        this.preOrderRepository.save(preOrderModel);
        emailService.sendPreOrderSuccess(preOrderModel.getEmail());
        return preOrderModel;
    }

    public PreOrderModel updatePreOrder(PreOrderModel preOrderModel, String id) {
        Optional<PreOrderModel> preOrder = this.preOrderRepository.findById(id);
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
        Optional<PreOrderModel> preOrder = this.preOrderRepository.findById(id);
        if (preOrder.isPresent()) {
            this.preOrderRepository.delete(preOrder.get());
            return ResponseEntity.ok("PreOrder deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }

    public List<PreOrderModel> getPreOrders() {
        return this.preOrderRepository.findAll();
    }

    public PreOrderModel getPreOrder(String id) {
        Optional<PreOrderModel> preOrder = this.preOrderRepository.findById(id);
        return preOrder.orElse(null);
    }
}
