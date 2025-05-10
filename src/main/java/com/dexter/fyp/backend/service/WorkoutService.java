package com.dexter.fyp.backend.service;
import org.springframework.stereotype.Service;

import com.dexter.fyp.backend.dto.WorkoutDto;
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
    public WorkoutDto addWorkout(WorkoutDto request) {

        Workout workout = new Workout();
        workout.setName(request.getName());
        workout.setDescription(request.getDescription());
        workout.setDifficulty(request.getDifficulty());
        workout.setDuration(request.getDuration());
        workout.setImageUrl(request.getImageUrl());
        workout.setReps(request.getReps());
        workout.setSets(request.getSets());
        workout.setAlternateExerciseId(request.getAlternateExerciseId());
        workout.setPatientNote(request.getPatientNote());
        workout.setVideoUrl(request.getVideoUrl());

        workout = workoutRepository.save(workout);

        request.setId(workout.getId());

        return request;

    }

    // Search workout by name (partial, case-insensitive)
    public List<Workout> searchWorkoutByName(String name) {
        return workoutRepository.findByNameContainingIgnoreCase(name);
    }

}
