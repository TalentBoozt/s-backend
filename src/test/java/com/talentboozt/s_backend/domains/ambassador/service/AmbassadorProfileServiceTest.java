package com.talentboozt.s_backend.domains.ambassador.service;

import com.talentboozt.s_backend.domains.ambassador.model.AmbassadorProfileModel;
import com.talentboozt.s_backend.domains.ambassador.repository.AmbassadorProfileRepository;
import com.talentboozt.s_backend.domains.auth.model.CredentialsModel;
import com.talentboozt.s_backend.domains.auth.repository.CredentialsRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AmbassadorProfileServiceTest {

    @Mock
    private AmbassadorProfileRepository ambassadorProfileRepository;

    @Mock
    private CredentialsRepository credentialsRepository;

    @InjectMocks
    private AmbassadorProfileService ambassadorProfileService;

    @Test
    void applyAmbassador_createsNewProfileWithDefaultValues() {
        AmbassadorProfileModel request = new AmbassadorProfileModel();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setMotivation("I want to inspire others.");
        request.setProfileLink("http://example.com/johndoe");
        request.setConsentGiven(true);

        // Mock the save method to return the input profile with a generated ID
        AmbassadorProfileModel savedProfile = new AmbassadorProfileModel();
        savedProfile.setId("generated-id");
        savedProfile.setName(request.getName());
        savedProfile.setEmail(request.getEmail());
        savedProfile.setMotivation(request.getMotivation());
        savedProfile.setProfileLink(request.getProfileLink());
        savedProfile.setConsentGiven(request.isConsentGiven());
        savedProfile.setLevel("BRONZE");
        savedProfile.setApplicationStatus("REQUESTED");
        savedProfile.setStatus("REQUESTED");
        savedProfile.setActive(false);

        when(ambassadorProfileRepository.save(any(AmbassadorProfileModel.class)))
                .thenReturn(savedProfile);

        // Call the method under test
        AmbassadorProfileModel result = ambassadorProfileService.applyAmbassador(request);

        // Assertions to validate the behavior
        assertNotNull(result.getId());  // ID should now be non-null
        assertEquals("John Doe", result.getName());
        assertEquals("REQUESTED", result.getApplicationStatus());
        assertEquals("BRONZE", result.getLevel());
        assertFalse(result.isActive());

        // Verifying that the save method was called
        verify(ambassadorProfileRepository).save(any(AmbassadorProfileModel.class));
    }

    @Test
    void approveAmbassadorProfile_setsStatusToActiveAndUpdatesTimestamps() {
        String id = "123";
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setId(id);
        profile.setStatus("REQUESTED");
        profile.setApplicationStatus("REQUESTED");
        profile.setEmployeeId("emp123");

        CredentialsModel credentials = new CredentialsModel();
        credentials.setId("cred123");
        credentials.setEmployeeId("emp123");

        // Mock repository methods
        when(ambassadorProfileRepository.findById(id)).thenReturn(Optional.of(profile));
        when(ambassadorProfileRepository.save(any(AmbassadorProfileModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(credentialsRepository.findByEmployeeId(anyString()))
                .thenReturn(Optional.of(credentials));

        when(credentialsRepository.save(any(CredentialsModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method under test
        AmbassadorProfileModel result = ambassadorProfileService.approveAmbassadorProfile(id);

        // Assertions
        assertEquals("ACTIVE", result.getStatus());
        assertEquals("ACCEPTED", result.getApplicationStatus());
        assertTrue(result.isActive());
        assertNotNull(result.getJoinedAt());

        // Verify saves
        verify(ambassadorProfileRepository).save(profile);
        verify(credentialsRepository).save(credentials);
    }

    @Test
    void rejectAmbassadorProfile_setsStatusToRejected() {
        String id = "123";
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setId(id);
        profile.setStatus("REQUESTED");

        // Mock the findById and save methods
        when(ambassadorProfileRepository.findById(id)).thenReturn(Optional.of(profile));
        when(ambassadorProfileRepository.save(any(AmbassadorProfileModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method under test
        AmbassadorProfileModel result = ambassadorProfileService.rejectAmbassadorProfile(id);

        // Assertions
        assertEquals("REJECTED", result.getStatus());
        assertEquals("REJECTED", result.getApplicationStatus());

        // Verifying that the save method was called
        verify(ambassadorProfileRepository).save(profile);
    }

    @Test
    void promoteAmbassador_incrementsLevelCorrectly() {
        String id = "123";
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setId(id);
        profile.setLevel("BRONZE");

        // Mock the findById and save methods
        when(ambassadorProfileRepository.findById(id)).thenReturn(Optional.of(profile));
        when(ambassadorProfileRepository.save(any(AmbassadorProfileModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method under test
        AmbassadorProfileModel result = ambassadorProfileService.promoteAmbassador(id);

        // Assertions
        assertEquals("SILVER", result.getLevel());

        // Verifying that the save method was called
        verify(ambassadorProfileRepository).save(profile);
    }

    @Test
    void promoteAmbassador_doesNotChangeLevelIfAlreadyPlatinum() {
        String id = "123";
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setId(id);
        profile.setLevel("PLATINUM");  // Already at the highest level

        // Mock the findById method to return the profile
        when(ambassadorProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        // Call the method under test
        AmbassadorProfileModel result = ambassadorProfileService.promoteAmbassador(id);

        // Assertions to ensure the level remains PLATINUM
        assertNotNull(result);  // Ensure the result is not null
        assertEquals("PLATINUM", result.getLevel());  // Ensure the level remains PLATINUM

        // Verifying that save method was NOT called because the level did not change
        verify(ambassadorProfileRepository, never()).save(any(AmbassadorProfileModel.class));
    }

    @Test
    void demoteAmbassador_decrementsLevelCorrectly() {
        String id = "123";
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setId(id);
        profile.setLevel("SILVER");

        // Mock the findById and save methods
        when(ambassadorProfileRepository.findById(id)).thenReturn(Optional.of(profile));
        when(ambassadorProfileRepository.save(any(AmbassadorProfileModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Call the method under test
        AmbassadorProfileModel result = ambassadorProfileService.demoteAmbassador(id);

        // Assertions
        assertEquals("BRONZE", result.getLevel());

        // Verifying that the save method was called
        verify(ambassadorProfileRepository).save(profile);
    }

    @Test
    void demoteAmbassador_doesNotChangeLevelIfAlreadyBronze() {
        String id = "123";
        AmbassadorProfileModel profile = new AmbassadorProfileModel();
        profile.setId(id);
        profile.setLevel("BRONZE");  // Already at the lowest level

        // Mock the findById method to return the profile
        when(ambassadorProfileRepository.findById(id)).thenReturn(Optional.of(profile));

        // Call the method under test
        AmbassadorProfileModel result = ambassadorProfileService.demoteAmbassador(id);

        // Assertions to ensure the level remains BRONZE
        assertNotNull(result);  // Ensure the result is not null
        assertEquals("BRONZE", result.getLevel());  // Ensure the level remains BRONZE

        // Verifying that save method was NOT called because the level did not change
        verify(ambassadorProfileRepository, never()).save(any(AmbassadorProfileModel.class));
    }
}
