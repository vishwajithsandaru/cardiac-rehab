package com.dexter.fyp.backend.dto;

import java.time.LocalDate;
import java.util.List;

import com.dexter.fyp.backend.enums.Status;

import lombok.Data;

@Data
public class CurrentPlanAndProgressResponse {
    
    private Status status;
    private String message;
    private Integer userPlanId;
    private String planName;
    private String planDescription;
    private String doctorNotes;
    private String modifiedDoctor;
    private Integer completed;
    private Integer total;
    private double progressPercentage;
    private LocalDate todaysDate;
    private List<WorkoutDto> workouts;

}
