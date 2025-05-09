package com.dexter.fyp.backend.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
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
public class UserPlanProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_plan_id")
    private UserPlan userPlan;

    @Column(name = "count_completed")
    private int countCompleted;

    @Column(name = "count_total")
    private int countTotal;

    @Column(name = "percentage")
    private double percentage;

    @Column(name = "date")
    private LocalDate date;
    
}
