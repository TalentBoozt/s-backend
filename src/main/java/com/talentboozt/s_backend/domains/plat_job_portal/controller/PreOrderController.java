package com.talentboozt.s_backend.domains.plat_job_portal.controller;

import com.talentboozt.s_backend.domains.plat_job_portal.model.PreOrderModel;
import com.talentboozt.s_backend.domains.plat_job_portal.service.PreOrderService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v2/preorder")
public class PreOrderController {

    @Autowired
    private PreOrderService preOrderService;

    @PostMapping("/add")
    public PreOrderModel addPreOrder(@RequestBody PreOrderModel preOrderModel) throws MessagingException, IOException {
        return preOrderService.addPreOrder(preOrderModel);
    }

    @PutMapping("/update/{id}")
    public PreOrderModel updatePreOrder(@RequestBody PreOrderModel preOrderModel, @PathVariable String id) {
        return preOrderService.updatePreOrder(preOrderModel, id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePreOrder(@PathVariable String id) {
        return preOrderService.deletePreOrder(id);
    }

    @GetMapping("/all")
    public List<PreOrderModel> getPreOrders() {
        return preOrderService.getPreOrders();
    }

    @GetMapping("/get/{id}")
    public PreOrderModel getPreOrder(@PathVariable String id) {
        return preOrderService.getPreOrder(id);
    }
}
