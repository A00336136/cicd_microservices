package com.nutritrack.lifestyleservice.controller;

import com.nutritrack.lifestyleservice.dto.LifestyleEntryRequest;
import com.nutritrack.lifestyleservice.dto.LifestyleEntryResponse;
import com.nutritrack.lifestyleservice.service.LifestyleEntryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for lifestyle entries.
 */
@RestController
@RequestMapping("/lifestyle/entries")
public class LifestyleEntryController {

    private final LifestyleEntryService service;

    public LifestyleEntryController(LifestyleEntryService service) {
        this.service = service;
    }

    /**
     * Create a new lifestyle entry.
     * 
     * POST /lifestyle/entries
     * 
     * @param request the lifestyle entry request
     * @return 201 Created with the created entry, or 400 Bad Request on validation failure
     */
    @PostMapping
    public ResponseEntity<LifestyleEntryResponse> createEntry(@Valid @RequestBody LifestyleEntryRequest request) {
        LifestyleEntryResponse response = service.createEntry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all entries for a specific user.
     * 
     * GET /lifestyle/entries?userId={userId}
     */
    @GetMapping
    public ResponseEntity<List<LifestyleEntryResponse>> getEntries(@RequestParam Long userId) {
        List<LifestyleEntryResponse> entries = service.getEntriesByUserId(userId);
        return ResponseEntity.ok(entries);
    }

    /**
     * Get a specific entry by ID.
     * 
     * GET /lifestyle/entries/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<LifestyleEntryResponse> getEntry(@PathVariable Long id) {
        LifestyleEntryResponse entry = service.getEntryById(id);
        return ResponseEntity.ok(entry);
    }
}
