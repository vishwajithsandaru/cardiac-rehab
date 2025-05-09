package com.dexter.fyp.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Workout {

    @Id
    private Long id;

    @NotBlank(message = "Workout name is required")
    @Size(max = 100, message = "Workout name must not exceed 100 characters")
    @Column(nullable = false)
    private String name;

    @Size(max = 500, message = "Workout description must not exceed 500 characters")
    @Column
    private String description;

    @Column
    private String imageUrl;

    @Column
    private String videoUrl;

    
    @Column(nullable = true)
    private String difficulty;

    @Column(nullable = true)
    private int duration = 0;

    @Size(max = 1000, message = "Patient-specific description must not exceed 1000 characters")
    @Column
    private String patientNote;

    @Column
    private Long alternateExerciseId;

    @Column
    private Integer sets = 0;

    @Column
    private Integer reps = 0;
}
