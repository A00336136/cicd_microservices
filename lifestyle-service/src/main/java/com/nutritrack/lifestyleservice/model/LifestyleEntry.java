package com.nutritrack.lifestyleservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity class representing a lifestyle activity entry.
 */
@Entity
@Table(name = "lifestyle_entries")
public class LifestyleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be positive")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "Lifestyle type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private LifestyleType type;

    @NotNull(message = "Value cannot be null")
    @Positive(message = "Value must be positive")
    @Column(name = "value", nullable = false)
    private Double value;

    @NotNull(message = "Timestamp cannot be null")
    @PastOrPresent(message = "Timestamp cannot be in the future")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    /**
     * Default constructor for JPA
     */
    public LifestyleEntry() {
    }

    /**
     * Constructor with all fields except id
     */
    public LifestyleEntry(Long userId, LifestyleType type, Double value, LocalDateTime timestamp) {
        this.userId = userId;
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
    }

    // Getters and Setters

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LifestyleEntry that = (LifestyleEntry) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LifestyleEntry{" +
                "id=" + id +
                ", userId=" + userId +
                ", type=" + type +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }
}
