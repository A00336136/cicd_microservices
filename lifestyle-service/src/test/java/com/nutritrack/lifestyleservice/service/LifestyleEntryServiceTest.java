package com.nutritrack.lifestyleservice.service;

import com.nutritrack.lifestyleservice.dto.LifestyleEntryRequest;
import com.nutritrack.lifestyleservice.dto.LifestyleEntryResponse;
import com.nutritrack.lifestyleservice.model.LifestyleEntry;
import com.nutritrack.lifestyleservice.model.LifestyleType;
import com.nutritrack.lifestyleservice.repository.LifestyleEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LifestyleEntryService.
 */
@ExtendWith(MockitoExtension.class)
class LifestyleEntryServiceTest {

    @Mock
    private LifestyleEntryRepository repository;

    @InjectMocks
    private LifestyleEntryService service;

    private LifestyleEntryRequest validRequest;
    private LifestyleEntry validEntry;

    @BeforeEach
    void setUp() {
        // Setup valid request
        validRequest = new LifestyleEntryRequest(
                1L,
                "STEPS",
                8000.0,
                LocalDateTime.now().minusHours(1)
        );

        // Setup valid entry
        validEntry = new LifestyleEntry(
                1L,
                LifestyleType.STEPS,
                8000.0,
                LocalDateTime.now().minusHours(1)
        );
        validEntry.setId(1L);
    }

    @Test
    @DisplayName("Should successfully create a lifestyle entry")
    void testCreateEntry_Success() {
        // Arrange
        when(repository.save(any(LifestyleEntry.class))).thenReturn(validEntry);

        // Act
        LifestyleEntryResponse response = service.createEntry(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(LifestyleType.STEPS, response.getType());
        assertEquals(8000.0, response.getValue());
        verify(repository, times(1)).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should create entry with case-insensitive type")
    void testCreateEntry_CaseInsensitiveType() {
        // Arrange
        validRequest.setType("steps"); // lowercase
        when(repository.save(any(LifestyleEntry.class))).thenReturn(validEntry);

        // Act
        LifestyleEntryResponse response = service.createEntry(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals(LifestyleType.STEPS, response.getType());
        verify(repository, times(1)).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should throw exception for null type")
    void testCreateEntry_NullType() {
        // Arrange
        validRequest.setType(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createEntry(validRequest)
        );
        assertEquals("Type cannot be empty", exception.getMessage());
        verify(repository, never()).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should throw exception for empty type")
    void testCreateEntry_EmptyType() {
        // Arrange
        validRequest.setType("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createEntry(validRequest)
        );
        assertEquals("Type cannot be empty", exception.getMessage());
        verify(repository, never()).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid type")
    void testCreateEntry_InvalidType() {
        // Arrange
        validRequest.setType("INVALID_TYPE");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createEntry(validRequest)
        );
        assertTrue(exception.getMessage().contains("Invalid lifestyle type"));
        verify(repository, never()).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should throw exception for timestamp older than 1 year")
    void testCreateEntry_OldTimestamp() {
        // Arrange
        validRequest.setTimestamp(LocalDateTime.now().minusYears(2));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createEntry(validRequest)
        );
        assertTrue(exception.getMessage().contains("cannot be older than 1 year"));
        verify(repository, never()).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should throw exception for STEPS value exceeding maximum")
    void testCreateEntry_InvalidStepsValue() {
        // Arrange
        validRequest.setType("STEPS");
        validRequest.setValue(150000.0); // Exceeds 100,000 max

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createEntry(validRequest)
        );
        assertTrue(exception.getMessage().contains("Steps value cannot exceed 100,000"));
        verify(repository, never()).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should throw exception for WATER value exceeding maximum")
    void testCreateEntry_InvalidWaterValue() {
        // Arrange
        validRequest.setType("WATER");
        validRequest.setValue(15000.0); // Exceeds 10,000 max

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createEntry(validRequest)
        );
        assertTrue(exception.getMessage().contains("Water value cannot exceed 10,000 ml"));
        verify(repository, never()).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should throw exception for SLEEP value exceeding maximum")
    void testCreateEntry_InvalidSleepValue() {
        // Arrange
        validRequest.setType("SLEEP");
        validRequest.setValue(1500.0); // Exceeds 1440 max (24 hours)

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createEntry(validRequest)
        );
        assertTrue(exception.getMessage().contains("Sleep value cannot exceed 1,440 minutes"));
        verify(repository, never()).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should throw exception for JOGGING value exceeding maximum")
    void testCreateEntry_InvalidJoggingValue() {
        // Arrange
        validRequest.setType("JOGGING");
        validRequest.setValue(800.0); // Exceeds 720 max (12 hours)

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createEntry(validRequest)
        );
        assertTrue(exception.getMessage().contains("JOGGING value cannot exceed 720 minutes"));
        verify(repository, never()).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should throw exception for MEDITATION value exceeding maximum")
    void testCreateEntry_InvalidMeditationValue() {
        // Arrange
        validRequest.setType("MEDITATION");
        validRequest.setValue(500.0); // Exceeds 480 max (8 hours)

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createEntry(validRequest)
        );
        assertTrue(exception.getMessage().contains("Meditation value cannot exceed 480 minutes"));
        verify(repository, never()).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should successfully get entries by user ID")
    void testGetEntriesByUserId_Success() {
        // Arrange
        Long userId = 1L;
        LifestyleEntry entry1 = new LifestyleEntry(userId, LifestyleType.STEPS, 8000.0, LocalDateTime.now());
        entry1.setId(1L);
        LifestyleEntry entry2 = new LifestyleEntry(userId, LifestyleType.WATER, 2000.0, LocalDateTime.now());
        entry2.setId(2L);
        
        List<LifestyleEntry> entries = Arrays.asList(entry1, entry2);
        when(repository.findByUserId(userId)).thenReturn(entries);

        // Act
        List<LifestyleEntryResponse> responses = service.getEntriesByUserId(userId);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(LifestyleType.STEPS, responses.get(0).getType());
        assertEquals(LifestyleType.WATER, responses.get(1).getType());
        verify(repository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should return empty list for user with no entries")
    void testGetEntriesByUserId_EmptyResult() {
        // Arrange
        Long userId = 999L;
        when(repository.findByUserId(userId)).thenReturn(Arrays.asList());

        // Act
        List<LifestyleEntryResponse> responses = service.getEntriesByUserId(userId);

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(repository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should successfully get entry by ID")
    void testGetEntryById_Success() {
        // Arrange
        Long entryId = 1L;
        when(repository.findById(entryId)).thenReturn(Optional.of(validEntry));

        // Act
        LifestyleEntryResponse response = service.getEntryById(entryId);

        // Assert
        assertNotNull(response);
        assertEquals(entryId, response.getId());
        assertEquals(LifestyleType.STEPS, response.getType());
        verify(repository, times(1)).findById(entryId);
    }

    @Test
    @DisplayName("Should throw exception when entry not found by ID")
    void testGetEntryById_NotFound() {
        // Arrange
        Long entryId = 999L;
        when(repository.findById(entryId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.getEntryById(entryId)
        );
        assertTrue(exception.getMessage().contains("Entry not found with id: " + entryId));
        verify(repository, times(1)).findById(entryId);
    }

    @Test
    @DisplayName("Should accept valid values at maximum boundaries")
    void testCreateEntry_ValidMaxBoundaryValues() {
        // Test STEPS at maximum
        validRequest.setType("STEPS");
        validRequest.setValue(100000.0);
        when(repository.save(any(LifestyleEntry.class))).thenReturn(validEntry);
        
        assertDoesNotThrow(() -> service.createEntry(validRequest));

        // Test WATER at maximum
        validRequest.setType("WATER");
        validRequest.setValue(10000.0);
        assertDoesNotThrow(() -> service.createEntry(validRequest));

        // Test SLEEP at maximum
        validRequest.setType("SLEEP");
        validRequest.setValue(1440.0);
        assertDoesNotThrow(() -> service.createEntry(validRequest));

        verify(repository, times(3)).save(any(LifestyleEntry.class));
    }

    @Test
    @DisplayName("Should accept timestamp at exactly 1 year ago boundary")
    void testCreateEntry_BoundaryTimestamp() {
        // Arrange
        validRequest.setTimestamp(LocalDateTime.now().minusYears(1).plusDays(1));
        when(repository.save(any(LifestyleEntry.class))).thenReturn(validEntry);

        // Act & Assert
        assertDoesNotThrow(() -> service.createEntry(validRequest));
        verify(repository, times(1)).save(any(LifestyleEntry.class));
    }
}
