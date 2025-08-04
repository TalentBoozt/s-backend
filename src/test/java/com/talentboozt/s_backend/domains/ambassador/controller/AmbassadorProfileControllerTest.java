package com.talentboozt.s_backend.domains.ambassador.controller;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.service.AmbassadorProfileService;
import com.talentboozt.s_backend.domains.common.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmbassadorProfileControllerTest {

    @Mock
    private AmbassadorProfileService ambassadorProfileService;

    @InjectMocks
    private AmbassadorProfileController ambassadorProfileController;

    @Test
    void applyAsAmbassadorReturnsBadRequestWhenIdIsNull() {
        AmbassadorProfileModel request = new AmbassadorProfileModel();
        when(ambassadorProfileService.applyAmbassador(request)).thenReturn(new AmbassadorProfileModel());

        ResponseEntity<?> response = ambassadorProfileController.applyAsAmbassador(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Something went wrong", ((ApiResponse) response.getBody()).getMessage());
    }

    @Test
    void applyAsAmbassadorReturnsOkWhenIdIsNotNull() {
        AmbassadorProfileModel request = new AmbassadorProfileModel();
        AmbassadorProfileModel responseModel = new AmbassadorProfileModel();
        responseModel.setId("123");
        when(ambassadorProfileService.applyAmbassador(request)).thenReturn(responseModel);

        ResponseEntity<?> response = ambassadorProfileController.applyAsAmbassador(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully applied as ambassador", ((ApiResponse) response.getBody()).getMessage());
    }

    @Test
    void getAmbassadorProfileReturnsProfileForValidId() {
        String id = "123";
        AmbassadorProfileModel expectedProfile = new AmbassadorProfileModel();
        expectedProfile.setId(id);
        when(ambassadorProfileService.getAmbassadorProfile(id)).thenReturn(expectedProfile);

        AmbassadorProfileModel result = ambassadorProfileController.getAmbassadorProfile(id);

        assertEquals(expectedProfile, result);
    }

    @Test
    void getAmbassadorProfileThrowsExceptionForInvalidId() {
        String id = "invalid";
        when(ambassadorProfileService.getAmbassadorProfile(id)).thenThrow(new IllegalArgumentException("Profile not found"));

        assertThrows(IllegalArgumentException.class, () -> ambassadorProfileController.getAmbassadorProfile(id));
    }

    @Test
    void updateAmbassadorProfileUpdatesSuccessfully() {
        String id = "123";
        AmbassadorProfileModel request = new AmbassadorProfileModel();
        AmbassadorProfileModel updatedProfile = new AmbassadorProfileModel();
        updatedProfile.setId(id);
        when(ambassadorProfileService.updateAmbassadorProfile(id, request)).thenReturn(updatedProfile);

        AmbassadorProfileModel result = ambassadorProfileController.updateAmbassadorProfile(id, request);

        assertEquals(updatedProfile, result);
    }

    @Test
    void approveAmbassadorProfileApprovesSuccessfully() {
        String id = "123";
        AmbassadorProfileModel approvedProfile = new AmbassadorProfileModel();
        approvedProfile.setId(id);
        when(ambassadorProfileService.approveAmbassadorProfile(id)).thenReturn(approvedProfile);

        AmbassadorProfileModel result = ambassadorProfileController.approveAmbassadorProfile(id);

        assertEquals(approvedProfile, result);
    }

    @Test
    void rejectAmbassadorProfileRejectsSuccessfully() {
        String id = "123";
        AmbassadorProfileModel rejectedProfile = new AmbassadorProfileModel();
        rejectedProfile.setId(id);
        when(ambassadorProfileService.rejectAmbassadorProfile(id)).thenReturn(rejectedProfile);

        AmbassadorProfileModel result = ambassadorProfileController.rejectAmbassadorProfile(id);

        assertEquals(rejectedProfile, result);
    }
}
