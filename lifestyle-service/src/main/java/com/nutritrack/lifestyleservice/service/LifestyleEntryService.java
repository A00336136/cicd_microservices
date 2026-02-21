package com.nutritrack.lifestyleservice.service;

import com.nutritrack.lifestyleservice.dto.LifestyleEntryRequest;
import com.nutritrack.lifestyleservice.dto.LifestyleEntryResponse;
import com.nutritrack.lifestyleservice.model.LifestyleEntry;
import com.nutritrack.lifestyleservice.model.LifestyleType;
import com.nutritrack.lifestyleservice.repository.LifestyleEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing lifestyle entries.
 */
@Service
public class LifestyleEntryService {

    private final LifestyleEntryRepository repository;

    public LifestyleEntryService(LifestyleEntryRepository repository) {
        this.repository = repository;
    }

    /**
     * Create a new lifestyle entry.
     *
     * @param request the lifestyle entry request
     * @return the created lifestyle entry response
     * @throws IllegalArgumentException if the type is invalid or timestamp is too old
     */
    @Transactional
    public LifestyleEntryResponse createEntry(LifestyleEntryRequest request) {
        // Validate and parse the type (case-insensitive)
        LifestyleType type = parseLifestyleType(request.getType());
        
        // Additional validation: timestamp should not be older than 1 year
        validateTimestamp(request.getTimestamp());
        
        // Additional validation: value should be reasonable for the type
        validateValueForType(type, request.getValue());

        // Create entity
        LifestyleEntry entry = new LifestyleEntry(
                request.getUserId(),
                type,
                request.getValue(),
                request.getTimestamp()
        );

        // Save to database
        LifestyleEntry savedEntry = repository.save(entry);

        // Return response
        return LifestyleEntryResponse.fromEntity(savedEntry);
    }

    /**
     * Get all entries for a user.
     */
    public List<LifestyleEntryResponse> getEntriesByUserId(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(LifestyleEntryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get an entry by ID.
     */
    public LifestyleEntryResponse getEntryById(Long id) {
        LifestyleEntry entry = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found with id: " + id));
        return LifestyleEntryResponse.fromEntity(entry);
    }

    /**
     * Parse lifestyle type from string (case-insensitive).
     */
    private LifestyleType parseLifestyleType(String typeString) {
        if (typeString == null || typeString.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
        
        try {
            return LifestyleType.valueOf(typeString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid lifestyle type: " + typeString + 
                    ". Valid types are: STEPS, WATER, SLEEP, JOGGING, RUNNING, MEDITATION"
            );
        }
    }

    /**
     * Validate that timestamp is not too old (within 1 year).
     */
    private void validateTimestamp(LocalDateTime timestamp) {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        if (timestamp.isBefore(oneYearAgo)) {
            throw new IllegalArgumentException(
                    "Timestamp cannot be older than 1 year. Provided: " + timestamp
            );
        }
    }

    /**
     * Validate value based on the lifestyle type.
     */
    private void validateValueForType(LifestyleType type, Double value) {
        switch (type) {
            case STEPS:
                if (value > 100000) {
                    throw new IllegalArgumentException("Steps value cannot exceed 100,000. Provided: " + value);
                }
                break;
            case WATER:
                if (value > 10000) {
                    throw new IllegalArgumentException("Water value cannot exceed 10,000 ml. Provided: " + value);
                }
                break;
            case SLEEP:
                if (value > 1440) { // 24 hours in minutes
                    throw new IllegalArgumentException("Sleep value cannot exceed 1,440 minutes (24 hours). Provided: " + value);
                }
                break;
            case JOGGING:
            case RUNNING:
                if (value > 720) { // 12 hours in minutes
                    throw new IllegalArgumentException(type + " value cannot exceed 720 minutes (12 hours). Provided: " + value);
                }
                break;
            case MEDITATION:
                if (value > 480) { // 8 hours in minutes
                    throw new IllegalArgumentException("Meditation value cannot exceed 480 minutes (8 hours). Provided: " + value);
                }
                break;
        }
    }
}
