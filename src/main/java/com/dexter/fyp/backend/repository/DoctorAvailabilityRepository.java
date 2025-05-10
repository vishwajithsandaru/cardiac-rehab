package com.dexter.fyp.backend.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dexter.fyp.backend.entity.Doctor;
import com.dexter.fyp.backend.entity.DoctorAvailability;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);
    
}
