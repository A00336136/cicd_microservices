package com.nutritrack.lifestyleservice.repository;

import com.nutritrack.lifestyleservice.model.LifestyleEntry;
import com.nutritrack.lifestyleservice.model.LifestyleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for LifestyleEntry entity.
 * Provides CRUD operations and custom query methods for lifestyle entries.
 */
@Repository
public interface LifestyleEntryRepository extends JpaRepository<LifestyleEntry, Long> {

    /**
     * Find all lifestyle entries for a specific user.
     *
     * @param userId the user ID
     * @return list of lifestyle entries
     */
    List<LifestyleEntry> findByUserId(Long userId);

    /**
     * Find all lifestyle entries for a specific user and type.
     *
     * @param userId the user ID
     * @param type the lifestyle type
     * @return list of lifestyle entries
     */
    List<LifestyleEntry> findByUserIdAndType(Long userId, LifestyleType type);

    /**
     * Find all lifestyle entries for a specific user within a time range.
     *
     * @param userId the user ID
     * @param startTime the start timestamp
     * @param endTime the end timestamp
     * @return list of lifestyle entries
     */
    List<LifestyleEntry> findByUserIdAndTimestampBetween(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find all lifestyle entries for a specific user and type within a time range.
     *
     * @param userId the user ID
     * @param type the lifestyle type
     * @param startTime the start timestamp
     * @param endTime the end timestamp
     * @return list of lifestyle entries
     */
    List<LifestyleEntry> findByUserIdAndTypeAndTimestampBetween(
            Long userId, LifestyleType type, LocalDateTime startTime, LocalDateTime endTime);
}
