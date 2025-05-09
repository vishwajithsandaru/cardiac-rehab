package com.dexter.fyp.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dexter.fyp.backend.entity.UserPlanProgress;
import com.dexter.fyp.backend.entity.UserPlan;

@Repository
public interface UserPlanProgressRepository extends JpaRepository<UserPlanProgress, Integer> {
    // Custom query methods can be defined here if needed
    // For example, you can add methods to find progress by user ID or plan ID

    public UserPlanProgress findByUserPlan(UserPlan userPlan);
    public UserPlanProgress findByUserPlanAndDate(UserPlan userPlan, LocalDate date);
    public List<UserPlanProgress> findByUserPlanAndDateBetween(UserPlan userPlan, LocalDate startDate, LocalDate endDate, Sort sort);
    
}
