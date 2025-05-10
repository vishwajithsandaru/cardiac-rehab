package com.dexter.fyp.backend.controller;


import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dexter.fyp.backend.dto.BookingAvailabilitiesResponse;
import com.dexter.fyp.backend.dto.CreateBookingRequest;
import com.dexter.fyp.backend.dto.GeneralResponse;
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
    public BookingAvailabilitiesResponse getDoctorBookingAvailabilites(@PathVariable Long doctorId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) throws Exception {
        return bookingService.getDoctorBookingAvailabilites(doctorId, startDate, endDate);
    }

   
}
