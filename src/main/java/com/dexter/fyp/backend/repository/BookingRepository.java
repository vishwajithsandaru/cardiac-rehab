package com.dexter.fyp.backend.repository;

import com.dexter.fyp.backend.entity.Booking;
import com.dexter.fyp.backend.entity.Doctor;
import com.dexter.fyp.backend.entity.DoctorAvailability;
import com.dexter.fyp.backend.enums.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByDoctorAvailabilityAndBookingDateAndStatus(DoctorAvailability doctorAvailability, LocalDate bookingDate, Status status);
    List<Booking> findByDoctorAndBookingDateBetween(Doctor doctor, LocalDate startDate, LocalDate endDate);
    List<Booking> findByDoctorAndBookingDate(Doctor doctor, LocalDate bookingDate);
    Page<Booking> findByDoctorOrderByBookingDateDesc(Doctor doctor, Pageable pageable);
    Page<Booking> findByUserOrderByBookingDateDesc(User user, Pageable pageable);

}
