package com.dexter.fyp.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class PlanDto {
    
    private Long id;
    private String name;
    private String description;
    private String introduction;
    private int duration;
    private List<WorkoutDto> workouts;

}
