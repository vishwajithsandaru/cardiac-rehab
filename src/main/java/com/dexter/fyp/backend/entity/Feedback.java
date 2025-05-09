package com.dexter.fyp.backend.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String feedback;

    private int exerciseHelpFulness;

    private int exerciseEffectiveness;

    private int howWellDidyouFare;

    private String suggestedImprovement;

    private int overallRating;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_plan_id")
    private UserPlan userPlan;

    private LocalDateTime createdDateTime;

}
