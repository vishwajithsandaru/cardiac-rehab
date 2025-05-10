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

    @GetMapping("/{id}")
    public ResponseEntity<Plan> getPlan(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    @GetMapping
    public List<Plan> getAllPlans() {
        return planService.getAllPlans();
    }

}
