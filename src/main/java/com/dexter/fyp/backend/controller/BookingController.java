package com.dexter.fyp.backend.controller;


import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dexter.fyp.backend.dto.BookingAvailabilitiesResponse;
import com.dexter.fyp.backend.dto.BookingDetailsDto;
import com.dexter.fyp.backend.dto.CreateBookingRequest;
import com.dexter.fyp.backend.dto.GeneralResponse;
import com.dexter.fyp.backend.dto.PatientDto;
import com.dexter.fyp.backend.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<GeneralResponse> createBooking(@RequestBody CreateBookingRequest request) throws Exception {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/doctor/{doctorId}/availabilities")
    public ResponseEntity<BookingAvailabilitiesResponse> getDoctorBookingAvailabilites(@PathVariable Long doctorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws Exception {
        return ResponseEntity.ok(bookingService.getDoctorBookingAvailabilites(doctorId, startDate, endDate));
    }

    @GetMapping("/doctor/{doctorId}/mybookings")
    public ResponseEntity<Page<PatientDto>> getDoctorBookings(@PathVariable Long doctorId,  @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) throws Exception{
        return ResponseEntity.ok(bookingService.getDoctorBookings(doctorId, page, size));
    }

    @GetMapping("/user/{userId}/mybookings")
    public ResponseEntity<Page<BookingDetailsDto>> getUserBookings(@PathVariable Long userId,  @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) throws Exception{
        return ResponseEntity.ok(bookingService.getUserBookingsDetails(userId, page, size));
    }
   
}
