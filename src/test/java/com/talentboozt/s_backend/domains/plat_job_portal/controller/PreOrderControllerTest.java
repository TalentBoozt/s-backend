package com.talentboozt.s_backend.domains.plat_job_portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentboozt.s_backend.domains.plat_job_portal.model.PreOrderModel;
import com.talentboozt.s_backend.domains.plat_job_portal.service.PreOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PreOrderControllerTest {

    @Mock
    private PreOrderService preOrderService;

    @InjectMocks
    private PreOrderController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PreOrderModel preOrder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
        preOrder = new PreOrderModel("1", "john", "Nw0J3@example.com", "Product A", "2023-08-01");
    }

    @Test
    void addPreOrder_shouldReturnSavedPreOrder() throws Exception {
        when(preOrderService.addPreOrder(any(PreOrderModel.class))).thenReturn(preOrder);

        mockMvc.perform(post("/api/v2/preorder/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.product").value("Product A"));

        verify(preOrderService, times(1)).addPreOrder(any(PreOrderModel.class));
    }

    @Test
    void updatePreOrder_shouldReturnUpdatedPreOrder() throws Exception {
        when(preOrderService.updatePreOrder(any(PreOrderModel.class), eq("1"))).thenReturn(preOrder);

        mockMvc.perform(put("/api/v2/preorder/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(preOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(preOrderService, times(1)).updatePreOrder(any(PreOrderModel.class), eq("1"));
    }

    @Test
    void deletePreOrder_shouldReturnSuccessMessage() throws Exception {
        when(preOrderService.deletePreOrder("1")).thenReturn(ResponseEntity.ok("PreOrder deleted"));

        mockMvc.perform(delete("/api/v2/preorder/delete/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("PreOrder deleted"));

        verify(preOrderService, times(1)).deletePreOrder("1");
    }

    @Test
    void getPreOrders_shouldReturnPreOrderList() throws Exception {
        List<PreOrderModel> preOrders = Arrays.asList(preOrder);
        when(preOrderService.getPreOrders()).thenReturn(preOrders);

        mockMvc.perform(get("/api/v2/preorder/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));

        verify(preOrderService, times(1)).getPreOrders();
    }

    @Test
    void getPreOrder_shouldReturnSinglePreOrder() throws Exception {
        when(preOrderService.getPreOrder("1")).thenReturn(preOrder);

        mockMvc.perform(get("/api/v2/preorder/get/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));

        verify(preOrderService, times(1)).getPreOrder("1");
    }
}
