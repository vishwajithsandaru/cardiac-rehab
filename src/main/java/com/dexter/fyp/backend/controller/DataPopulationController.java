package com.dexter.fyp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dexter.fyp.backend.service.DataPopulationService;

@RestController
@RequestMapping("/api/admin") // Or any suitable path, maybe restrict access later
public class DataPopulationController {

    private final DataPopulationService dataPopulationService;

    public DataPopulationController(DataPopulationService dataPopulationService) {
        this.dataPopulationService = dataPopulationService;
    }

    @PostMapping("/populateWorkouts")
    public ResponseEntity<String> populateWorkouts() {
        try {
            dataPopulationService.populateWorkoutsAndPlans();
            return ResponseEntity.ok("Database populated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error populating database: " + e.getMessage());
        }
    }

    @GetMapping("/clearAllData")
    public ResponseEntity<String> clearDatabase() {
        try {
            dataPopulationService.clearAllData();
            return ResponseEntity.ok("Database cleared successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error clearing database: " + e.getMessage());
        }
    }
}
