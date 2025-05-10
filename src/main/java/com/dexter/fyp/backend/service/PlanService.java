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

    private void validatePlan(Plan plan) {
        if (plan.getName() == null || plan.getName().isBlank()) {
            throw new IllegalArgumentException("Plan name is required");
        }
        if (plan.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
    }
}
