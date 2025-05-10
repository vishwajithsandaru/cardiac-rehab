package com.dexter.fyp.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dexter.fyp.backend.dto.DocUpdateUserPlan;
import com.dexter.fyp.backend.dto.GeneralResponse;
import com.dexter.fyp.backend.entity.Doctor;
import com.dexter.fyp.backend.service.DoctorService;


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
