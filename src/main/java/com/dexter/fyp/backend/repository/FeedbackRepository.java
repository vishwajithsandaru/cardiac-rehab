package com.dexter.fyp.backend.repository;

import com.dexter.fyp.backend.entity.Feedback;
import com.dexter.fyp.backend.entity.UserPlan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Feedback findTopByUserPlanOrderByCreatedDateTimeDesc(UserPlan userPlan);
    
}
