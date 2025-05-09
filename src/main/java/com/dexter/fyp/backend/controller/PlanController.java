package com.dexter.fyp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dexter.fyp.backend.dto.PlanCreateRequest;
import com.dexter.fyp.backend.entity.Plan;
import com.dexter.fyp.backend.service.PlanService;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping
    public ResponseEntity<Plan> createPlan(@RequestBody PlanCreateRequest request) {
        Plan plan = new Plan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setDuration(request.getDuration());
        return ResponseEntity.ok(planService.createPlan(plan, request.getWorkoutIds()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Plan> getPlan(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @GetMapping
    public List<Plan> getAllPlans() {
        return planService.getAllPlans();
    }

    @PostMapping("/{planId}/workouts/{workoutId}")
    public ResponseEntity<Plan> addWorkout(@PathVariable Long planId, @PathVariable Long workoutId) {
        return ResponseEntity.ok(planService.addWorkoutToPlan(planId, workoutId));
    }

    @DeleteMapping("/{planId}/workouts/{workoutId}")
    public ResponseEntity<Plan> removeWorkout(@PathVariable Long planId, @PathVariable Long workoutId) {
        return ResponseEntity.ok(planService.removeWorkoutFromPlan(planId, workoutId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.noContent().build();
    }
}
