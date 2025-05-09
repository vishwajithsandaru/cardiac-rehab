package com.dexter.fyp.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dexter.fyp.backend.dto.DocUpdateUserPlan;
import com.dexter.fyp.backend.dto.GeneralResponse;
import com.dexter.fyp.backend.entity.Doctor;
import com.dexter.fyp.backend.entity.DoctorAvailability;
import com.dexter.fyp.backend.service.DoctorService;


import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }


    // Get a doctor by ID
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        return doctor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all doctors
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // Update a doctor
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody Doctor updatedDoctor) {
        return ResponseEntity.ok(doctorService.updateDoctor(id, updatedDoctor));
    }

    // Delete a doctor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    // Get doctors by specialization
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialization(@PathVariable String specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }

    // Search doctors by name
    @GetMapping("/search/{name}")
    public ResponseEntity<List<Doctor>> searchDoctors( @PathVariable String name) {
        return ResponseEntity.ok(doctorService.searchDoctorsByName(name));
    }

    // Update a doctor's availability
    @PutMapping("/{id}/availability")
    public ResponseEntity<Doctor> updateAvailability(@PathVariable Long id, @RequestBody List<DoctorAvailability> availabilityList) {
        return ResponseEntity.ok(doctorService.updateAvailability(id, availabilityList));
    }

    // Get doctors available in a specific time range
    @GetMapping("/available")
    public ResponseEntity<List<Doctor>> getAvailableDoctors(
            @RequestParam LocalTime start,
            @RequestParam LocalTime end) {
        return ResponseEntity.ok(doctorService.getAvailableDoctors(start, end));
    }

    @PostMapping("/{id}/updateCurrentUserPlan")
    public ResponseEntity<GeneralResponse> updateCurrentUserPlan(@PathVariable Long id, @RequestBody DocUpdateUserPlan docUpdateUserPlan) {
        try {
            return ResponseEntity.ok(doctorService.updateCurrentUserPlan(id, docUpdateUserPlan));
        } catch (Exception e) {
            GeneralResponse response = new GeneralResponse();
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}
