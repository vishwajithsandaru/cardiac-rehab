package com.dexter.fyp.backend.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import com.dexter.fyp.backend.entity.Workout;
import com.dexter.fyp.backend.repository.WorkoutRepository;

import java.util.List;
import java.util.Optional;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;

    public WorkoutService(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    // Get all workouts
    public List<Workout> getAllWorkouts() {
        return workoutRepository.findAll();
    }

    // Get workout by ID
    public Optional<Workout> getWorkoutById(Long id) {
        return workoutRepository.findById(id);
    }

    // Add workout with validation
    public Workout addWorkout(Workout workout) {
        validateWorkout(workout);
        return workoutRepository.save(workout);
    }

    // Update workout with validation + existence check
    public Workout updateWorkout(Workout workout) {
        validateWorkout(workout);
        Workout existingWorkout = workoutRepository.findById(workout.getId())
                .orElseThrow(() -> new EntityNotFoundException("Workout not found with ID: " + workout.getId()));

        existingWorkout.setName(workout.getName());
        existingWorkout.setDuration(workout.getDuration());

        return workoutRepository.save(existingWorkout);
    }

    // Delete workout by ID
    public boolean deleteWorkoutById(Long id) {
        if (workoutRepository.existsById(id)) {
            workoutRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Search workout by name (partial, case-insensitive)
    public List<Workout> searchWorkoutByName(String name) {
        return workoutRepository.findByNameContainingIgnoreCase(name);
    }

    // ========== Private Validation ==========

    private void validateWorkout(Workout workout) {
        if (workout.getName() == null || workout.getName().isBlank()) {
            throw new IllegalArgumentException("Workout name is required");
        }
        if (workout.getDuration() <= 0) {
            throw new IllegalArgumentException("Workout duration must be greater than 0");
        }
    }
}
