package com.dexter.fyp.backend.entity;


import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userPlanId;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "doctor_notes", nullable = true)
    private String doctorNotes;

    @Column(name = "custom_plan_id", nullable = true)
    private String customPlanId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "default_plan_id")
    private Plan defaultPlan;

    @Column(name = "is_current")
    private boolean isCurrent;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = true)
    private Doctor updatedBy;

    @SuppressWarnings("unused")
    private LocalDateTime createdDateTime;

    @OneToMany(mappedBy = "userPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomPlanWorkout> customPlanWorkouts;
    
}
