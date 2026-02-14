package com.nutritrack.lifestyleservice.controller;

import com.nutritrack.lifestyleservice.dto.LifestyleEntryRequest;
import com.nutritrack.lifestyleservice.dto.LifestyleEntryResponse;
import com.nutritrack.lifestyleservice.model.LifestyleType;
import com.nutritrack.lifestyleservice.service.LifestyleEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LifestyleEntryController.
 */
@ExtendWith(MockitoExtension.class)
class LifestyleEntryControllerTest {

    @Mock
    private LifestyleEntryService service;

    @InjectMocks
    private LifestyleEntryController controller;

    private LifestyleEntryRequest validRequest;
    private LifestyleEntryResponse validResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now().minusHours(1);

        // Setup valid request
        validRequest = new LifestyleEntryRequest(
                1L,
                "STEPS",
                8000.0,
                now
        );

        // Setup valid response
        validResponse = new LifestyleEntryResponse(
                1L,
                1L,
                LifestyleType.STEPS,
                8000.0,
                now
        );
    }

    @Test
    @DisplayName("POST /lifestyle/entries - Success")
    void testCreateEntry_Success() {
        // Arrange
        when(service.createEntry(any(LifestyleEntryRequest.class))).thenReturn(validResponse);

        // Act
        ResponseEntity<LifestyleEntryResponse> response = controller.createEntry(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(1L, response.getBody().getUserId());
        assertEquals(LifestyleType.STEPS, response.getBody().getType());
        assertEquals(8000.0, response.getBody().getValue());
        verify(service, times(1)).createEntry(any(LifestyleEntryRequest.class));
    }

    @Test
    @DisplayName("POST /lifestyle/entries - Service throws exception")
    void testCreateEntry_ServiceThrowsException() {
        // Arrange
        when(service.createEntry(any(LifestyleEntryRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid lifestyle type"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> controller.createEntry(validRequest));
        verify(service, times(1)).createEntry(any(LifestyleEntryRequest.class));
    }

    @Test
    @DisplayName("GET /lifestyle/entries?userId=1 - Success")
    void testGetEntries_Success() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LifestyleEntryResponse response1 = new LifestyleEntryResponse(1L, 1L, LifestyleType.STEPS, 8000.0, now);
        LifestyleEntryResponse response2 = new LifestyleEntryResponse(2L, 1L, LifestyleType.WATER, 2000.0, now);
        List<LifestyleEntryResponse> responses = Arrays.asList(response1, response2);

        when(service.getEntriesByUserId(1L)).thenReturn(responses);

        // Act
        ResponseEntity<List<LifestyleEntryResponse>> response = controller.getEntries(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(LifestyleType.STEPS, response.getBody().get(0).getType());
        assertEquals(LifestyleType.WATER, response.getBody().get(1).getType());
        verify(service, times(1)).getEntriesByUserId(1L);
    }

    @Test
    @DisplayName("GET /lifestyle/entries?userId=999 - Empty result")
    void testGetEntries_EmptyResult() {
        // Arrange
        when(service.getEntriesByUserId(999L)).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<LifestyleEntryResponse>> response = controller.getEntries(999L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(service, times(1)).getEntriesByUserId(999L);
    }

    @Test
    @DisplayName("GET /lifestyle/entries/{id} - Success")
    void testGetEntry_Success() {
        // Arrange
        when(service.getEntryById(1L)).thenReturn(validResponse);

        // Act
        ResponseEntity<LifestyleEntryResponse> response = controller.getEntry(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(LifestyleType.STEPS, response.getBody().getType());
        verify(service, times(1)).getEntryById(1L);
    }

    @Test
    @DisplayName("GET /lifestyle/entries/{id} - Not found")
    void testGetEntry_NotFound() {
        // Arrange
        when(service.getEntryById(999L))
                .thenThrow(new IllegalArgumentException("Entry not found with id: 999"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> controller.getEntry(999L));
        verify(service, times(1)).getEntryById(999L);
    }

    @Test
    @DisplayName("POST /lifestyle/entries - Create WATER entry")
    void testCreateEntry_WaterType() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LifestyleEntryRequest waterRequest = new LifestyleEntryRequest(1L, "WATER", 2500.0, now);
        LifestyleEntryResponse waterResponse = new LifestyleEntryResponse(2L, 1L, LifestyleType.WATER, 2500.0, now);
        
        when(service.createEntry(any(LifestyleEntryRequest.class))).thenReturn(waterResponse);

        // Act
        ResponseEntity<LifestyleEntryResponse> response = controller.createEntry(waterRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(LifestyleType.WATER, response.getBody().getType());
        assertEquals(2500.0, response.getBody().getValue());
        verify(service, times(1)).createEntry(any(LifestyleEntryRequest.class));
    }

    @Test
    @DisplayName("POST /lifestyle/entries - Create SLEEP entry")
    void testCreateEntry_SleepType() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LifestyleEntryRequest sleepRequest = new LifestyleEntryRequest(1L, "SLEEP", 480.0, now);
        LifestyleEntryResponse sleepResponse = new LifestyleEntryResponse(3L, 1L, LifestyleType.SLEEP, 480.0, now);
        
        when(service.createEntry(any(LifestyleEntryRequest.class))).thenReturn(sleepResponse);

        // Act
        ResponseEntity<LifestyleEntryResponse> response = controller.createEntry(sleepRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(LifestyleType.SLEEP, response.getBody().getType());
        assertEquals(480.0, response.getBody().getValue());
        verify(service, times(1)).createEntry(any(LifestyleEntryRequest.class));
    }

    @Test
    @DisplayName("POST /lifestyle/entries - Create MEDITATION entry")
    void testCreateEntry_MeditationType() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        LifestyleEntryRequest meditationRequest = new LifestyleEntryRequest(1L, "MEDITATION", 30.0, now);
        LifestyleEntryResponse meditationResponse = new LifestyleEntryResponse(4L, 1L, LifestyleType.MEDITATION, 30.0, now);
        
        when(service.createEntry(any(LifestyleEntryRequest.class))).thenReturn(meditationResponse);

        // Act
        ResponseEntity<LifestyleEntryResponse> response = controller.createEntry(meditationRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(LifestyleType.MEDITATION, response.getBody().getType());
        assertEquals(30.0, response.getBody().getValue());
        verify(service, times(1)).createEntry(any(LifestyleEntryRequest.class));
    }

    @Test
    @DisplayName("GET /lifestyle/entries - Multiple users")
    void testGetEntries_MultipleUsers() {
        // Arrange - user 1
        when(service.getEntriesByUserId(1L)).thenReturn(Arrays.asList(validResponse));
        
        // Act - user 1
        ResponseEntity<List<LifestyleEntryResponse>> response1 = controller.getEntries(1L);

        // Assert - user 1
        assertNotNull(response1);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(1, response1.getBody().size());

        // Arrange - user 2
        LocalDateTime now = LocalDateTime.now();
        LifestyleEntryResponse user2Response = new LifestyleEntryResponse(5L, 2L, LifestyleType.WATER, 3000.0, now);
        when(service.getEntriesByUserId(2L)).thenReturn(Arrays.asList(user2Response));
        
        // Act - user 2
        ResponseEntity<List<LifestyleEntryResponse>> response2 = controller.getEntries(2L);

        // Assert - user 2
        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(1, response2.getBody().size());
        assertEquals(2L, response2.getBody().get(0).getUserId());

        verify(service, times(1)).getEntriesByUserId(1L);
        verify(service, times(1)).getEntriesByUserId(2L);
    }
}
