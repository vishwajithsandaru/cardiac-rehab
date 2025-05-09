package com.dexter.fyp.backend.service;


import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import com.dexter.fyp.backend.entity.Plan;
import com.dexter.fyp.backend.entity.Workout;
import com.dexter.fyp.backend.repository.PlanRepository;
import com.dexter.fyp.backend.repository.WorkoutRepository;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final WorkoutRepository workoutRepository;

    public PlanService(PlanRepository planRepository, WorkoutRepository workoutRepository) {
        this.planRepository = planRepository;
        this.workoutRepository = workoutRepository;
    }

    // Create a new plan with optional workout IDs
    public Plan createPlan(Plan plan, List<Long> workoutIds) {
        validatePlan(plan);

         if (planRepository.findByName(plan.getName()).isPresent()) {
             throw new IllegalArgumentException("Plan with the same name already exists");
         }

        if (workoutIds != null && !workoutIds.isEmpty()) {
            List<Workout> workouts = workoutRepository.findAllById(workoutIds);
            if (workouts.size() != workoutIds.size()) {
                throw new EntityNotFoundException("One or more workout IDs are invalid");
            }
            plan.setWorkouts(workouts);
        }

        return planRepository.save(plan);
    }

    public Plan getPlanById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with id: " + id));
    }

    public Plan getPlanByName(String name) {
        return planRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Plan not found with name: " + name));
    }

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public void deletePlan(Long planId) {
        if (!planRepository.existsById(planId)) {
            throw new EntityNotFoundException("Plan not found with id: " + planId);
        }
        planRepository.deleteById(planId);
    }

    public Plan addWorkoutToPlan(Long planId, Long workoutId) {
        Plan plan = getPlanById(planId);
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new EntityNotFoundException("Workout not found with id: " + workoutId));

        if (!plan.getWorkouts().contains(workout)) {
            plan.getWorkouts().add(workout);
        }

        return planRepository.save(plan);
    }

    public Plan removeWorkoutFromPlan(Long planId, Long workoutId) {
        Plan plan = getPlanById(planId);
        boolean removed = plan.getWorkouts().removeIf(w -> w.getId().equals(workoutId));

        if (!removed) {
            throw new IllegalArgumentException("Workout with id " + workoutId + " is not part of the plan");
        }

        return planRepository.save(plan);
    }

    private void validatePlan(Plan plan) {
        if (plan.getName() == null || plan.getName().isBlank()) {
            throw new IllegalArgumentException("Plan name is required");
        }
        if (plan.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
    }
}
