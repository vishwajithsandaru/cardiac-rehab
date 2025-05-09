package com.dexter.fyp.backend.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomPlanWorkout {

    @EmbeddedId
    private PlanWorkoutID id;

    @ManyToOne
    @MapsId("userPlanId")
    @JoinColumn(name = "plan_id")
    private UserPlan userPlan;
    
    @ManyToOne
    @MapsId("workoutId")
    @JoinColumn(name = "workout_id")
    private Workout workout;

    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanWorkoutID implements Serializable {
        
        private Integer userPlanId;
        private Long workoutId;

    }

}

