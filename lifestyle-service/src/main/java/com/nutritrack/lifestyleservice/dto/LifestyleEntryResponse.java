package com.nutritrack.lifestyleservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nutritrack.lifestyleservice.model.LifestyleEntry;
import com.nutritrack.lifestyleservice.model.LifestyleType;

import java.time.LocalDateTime;

/**
 * Response DTO for lifestyle entry.
 */
public class LifestyleEntryResponse {

    private Long id;
    private Long userId;
    private LifestyleType type;
    private Double value;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public LifestyleEntryResponse() {
    }

    public LifestyleEntryResponse(Long id, Long userId, LifestyleType type, Double value, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
    }

    /**
     * Convert LifestyleEntry entity to response DTO.
     */
    public static LifestyleEntryResponse fromEntity(LifestyleEntry entry) {
        return new LifestyleEntryResponse(
                entry.getId(),
                entry.getUserId(),
                entry.getType(),
                entry.getValue(),
                entry.getTimestamp()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LifestyleType getType() {
        return type;
    }

    public void setType(LifestyleType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LifestyleEntryResponse{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
