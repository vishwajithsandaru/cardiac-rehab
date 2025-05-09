package com.dexter.fyp.backend.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlanCreateRequest {
    // Getters and setters
    private String name;
    private String description;
    private int duration;
    private List<Long> workoutIds;

}
