package com.dexter.fyp.backend.repository;

import com.dexter.fyp.backend.entity.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findByNameContainingIgnoreCase(String name);
}
