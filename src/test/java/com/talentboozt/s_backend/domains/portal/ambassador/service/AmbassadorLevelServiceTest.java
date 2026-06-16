package com.talentboozt.s_backend.domains.portal.ambassador.service;

import com.talentboozt.s_backend.domains.portal.ambassador.model.AmbassadorProfileModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AmbassadorLevelServiceTest {

    private AmbassadorLevelService service;

    @BeforeEach
    void setUp() {
        service = new AmbassadorLevelService();
    }

    @Test
    void evaluateLevel_shouldReturnBronzeWhenMetricsAreBelowSilver() {
        // Arrange
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setTotalReferrals(10);
        profile.setCoursePurchasesByReferrals(5);
        profile.setHostedSessions(2);

        // Act
        String level = service.evaluateLevel(profile);

        // Assert
        assertThat(level).isEqualTo("BRONZE");
    }

    @Test
    void evaluateLevel_shouldReturnBronzeWhenSomeMetricsAreSilverButOthersAreBronze() {
        // Arrange
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setTotalReferrals(25);
        profile.setCoursePurchasesByReferrals(10);
        profile.setHostedSessions(4); // Under Silver threshold (5)

        // Act
        String level = service.evaluateLevel(profile);

        // Assert
        assertThat(level).isEqualTo("BRONZE");
    }

    @Test
    void evaluateLevel_shouldReturnSilverWhenMetricsMeetSilverThresholds() {
        // Arrange
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setTotalReferrals(25);
        profile.setCoursePurchasesByReferrals(10);
        profile.setHostedSessions(5);

        // Act
        String level = service.evaluateLevel(profile);

        // Assert
        assertThat(level).isEqualTo("SILVER");
    }

    @Test
    void evaluateLevel_shouldReturnGoldWhenMetricsMeetGoldThresholds() {
        // Arrange
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setTotalReferrals(50);
        profile.setCoursePurchasesByReferrals(20);
        profile.setHostedSessions(15);

        // Act
        String level = service.evaluateLevel(profile);

        // Assert
        assertThat(level).isEqualTo("GOLD");
    }

    @Test
    void evaluateLevel_shouldReturnDiamondWhenMetricsMeetDiamondThresholds() {
        // Arrange
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setTotalReferrals(75);
        profile.setCoursePurchasesByReferrals(50);
        profile.setHostedSessions(30);

        // Act
        String level = service.evaluateLevel(profile);

        // Assert
        assertThat(level).isEqualTo("DIAMOND");
    }

    @Test
    void evaluateLevel_shouldReturnPlatinumWhenMetricsMeetPlatinumThresholds() {
        // Arrange
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setTotalReferrals(100);
        profile.setCoursePurchasesByReferrals(75);
        profile.setHostedSessions(50);

        // Act
        String level = service.evaluateLevel(profile);

        // Assert
        assertThat(level).isEqualTo("PLATINUM");
    }

    @Test
    void evaluateLevel_shouldReturnDiamondWhenSomeMetricsMeetPlatinumButOthersMeetDiamond() {
        // Arrange
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setTotalReferrals(100);
        profile.setCoursePurchasesByReferrals(75);
        profile.setHostedSessions(49); // Under Platinum threshold (50)

        // Act
        String level = service.evaluateLevel(profile);

        // Assert
        assertThat(level).isEqualTo("DIAMOND");
    }
}
