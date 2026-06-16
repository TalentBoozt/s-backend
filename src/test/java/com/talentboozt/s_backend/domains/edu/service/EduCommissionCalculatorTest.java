package com.talentboozt.s_backend.domains.edu.service;

import com.talentboozt.s_backend.domains.edu.enums.ESubscriptionPlan;
import com.talentboozt.s_backend.domains.edu.model.EUser;
import com.talentboozt.s_backend.domains.edu.repository.mongodb.EUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EduCommissionCalculatorTest {

    private EUserRepository userRepository;
    private PlanConfigService planConfigService;
    private EduCommissionCalculator calculator;

    @BeforeEach
    void setUp() {
        userRepository = mock(EUserRepository.class);
        planConfigService = new PlanConfigService(); // real instance
        calculator = new EduCommissionCalculator(userRepository, planConfigService);
    }

    @Test
    void calculateCommissionRate_shouldReturnFreeRateWhenSellerIdIsNull() {
        // Act
        EduCommissionCalculator.CommissionResult result = calculator.calculateCommissionRate(null);

        // Assert
        assertThat(result.rate).isEqualTo(0.07);
        assertThat(result.plan).isEqualTo("FREE");
    }

    @Test
    void calculateCommissionRate_shouldReturnFreeRateWhenUserNotFound() {
        // Arrange
        String sellerId = "seller-123";
        when(userRepository.findById(sellerId)).thenReturn(Optional.empty());

        // Act
        EduCommissionCalculator.CommissionResult result = calculator.calculateCommissionRate(sellerId);

        // Assert
        assertThat(result.rate).isEqualTo(0.07);
        assertThat(result.plan).isEqualTo("FREE");
    }

    @Test
    void calculateCommissionRate_shouldReturnFreeRateWhenUserHasNullPlan() {
        // Arrange
        String sellerId = "seller-123";
        EUser user = EUser.builder().id(sellerId).plan(null).build();
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));

        // Act
        EduCommissionCalculator.CommissionResult result = calculator.calculateCommissionRate(sellerId);

        // Assert
        assertThat(result.rate).isEqualTo(0.07);
        assertThat(result.plan).isEqualTo("FREE");
    }

    @Test
    void calculateCommissionRate_shouldReturnProRateWhenUserHasProPlan() {
        // Arrange
        String sellerId = "seller-123";
        EUser user = EUser.builder().id(sellerId).plan(ESubscriptionPlan.PRO).build();
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));

        // Act
        EduCommissionCalculator.CommissionResult result = calculator.calculateCommissionRate(sellerId);

        // Assert
        assertThat(result.rate).isEqualTo(0.05);
        assertThat(result.plan).isEqualTo("PRO");
    }

    @Test
    void calculateCommissionRate_shouldReturnPremiumRateWhenUserHasPremiumPlan() {
        // Arrange
        String sellerId = "seller-123";
        EUser user = EUser.builder().id(sellerId).plan(ESubscriptionPlan.PREMIUM).build();
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));

        // Act
        EduCommissionCalculator.CommissionResult result = calculator.calculateCommissionRate(sellerId);

        // Assert
        assertThat(result.rate).isEqualTo(0.03);
        assertThat(result.plan).isEqualTo("PREMIUM");
    }

    @Test
    void calculateCommissionRate_shouldReturnEnterpriseRateWhenUserHasEnterprisePlan() {
        // Arrange
        String sellerId = "seller-123";
        EUser user = EUser.builder().id(sellerId).plan(ESubscriptionPlan.ENTERPRISE).build();
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(user));

        // Act
        EduCommissionCalculator.CommissionResult result = calculator.calculateCommissionRate(sellerId);

        // Assert
        assertThat(result.rate).isEqualTo(0.01);
        assertThat(result.plan).isEqualTo("ENTERPRISE");
    }
}
