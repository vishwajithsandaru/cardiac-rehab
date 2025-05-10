package com.dexter.fyp.backend.controller;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dexter.fyp.backend.dto.WorkoutDto;
import com.dexter.fyp.backend.entity.Workout;
import com.dexter.fyp.backend.service.WorkoutService;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        return ResponseEntity.ok(workoutService.getAllWorkouts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workout> getWorkoutById(@PathVariable Long id) {
        return workoutService.getWorkoutById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Workout not found"));
    }

    @PostMapping
    public ResponseEntity<WorkoutDto> createWorkout(@RequestBody WorkoutDto workout) {
        return ResponseEntity.ok(workoutService.addWorkout(workout));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Workout>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(workoutService.searchWorkoutByName(name));
    }

}
