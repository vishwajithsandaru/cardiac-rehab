package com.dexter.fyp.backend.repository;

import com.dexter.fyp.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByDoctorId(Long doctorId);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByTimeSlotStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Booking> findByUserIdAndIsPaidFalse(Long userId);
    List<Booking> findByStatus(int status);
}
