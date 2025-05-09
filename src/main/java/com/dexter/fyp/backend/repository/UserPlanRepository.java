package com.dexter.fyp.backend.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dexter.fyp.backend.entity.User;
import com.dexter.fyp.backend.entity.UserPlan;

@Repository
public interface UserPlanRepository extends JpaRepository<UserPlan, Integer>{

    public List<UserPlan> findByUser_IdOrderByCreatedDateTimeDesc(Long id);
    public UserPlan findByUserAndIsCurrentTrue(User user);
    
}
