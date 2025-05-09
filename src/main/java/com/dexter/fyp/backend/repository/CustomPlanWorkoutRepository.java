package com.dexter.fyp.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dexter.fyp.backend.entity.CustomPlanWorkout;

@Repository
public interface CustomPlanWorkoutRepository extends JpaRepository<CustomPlanWorkout, Integer> {
    
    public List<CustomPlanWorkout> findByUserPlan_UserPlanId(Integer userPlanId);
    
}
