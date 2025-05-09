package com.dexter.fyp.backend.dto;
import lombok.Data;

@Data
public class WorkoutDto {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private String difficulty;
    private int duration = 0;
    private String patientNote;
    private Long alternateExerciseId;
    private Integer sets = 0;
    private Integer reps = 0;
    
}
