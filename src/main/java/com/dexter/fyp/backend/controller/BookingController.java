package com.dexter.fyp.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dexter.fyp.backend.entity.Booking;
import com.dexter.fyp.backend.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Create a booking
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        return ResponseEntity.ok(bookingService.createBooking(booking));
    }

    // Get all bookings
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        Optional<Booking> booking = bookingService.getBookingById(id);
        return booking.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get bookings by doctor ID
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Booking>> getBookingsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(bookingService.getBookingsByDoctorId(doctorId));
    }

    // Get bookings by patient/user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }

    // Get bookings by date range
    @GetMapping("/date")
    public ResponseEntity<List<Booking>> getBookingsByDate(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(bookingService.getBookingsByDate(start, end));
    }

    // Get unpaid bookings by user ID
    @GetMapping("/user/{userId}/unpaid")
    public ResponseEntity<List<Booking>> getUnpaidBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUnpaidBookingsByUserId(userId));
    }

    // Get bookings by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable int status) {
        return ResponseEntity.ok(bookingService.getBookingsByStatus(status));
    }

    // Update a booking
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody Booking updatedBooking) {
        return ResponseEntity.ok(bookingService.updateBooking(id, updatedBooking));
    }

    // Delete a booking
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    // Cancel a booking (set status to -1)
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    // Reschedule a booking (only change time slot)
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<Booking> rescheduleBooking(@PathVariable Long id, @RequestBody Booking.TimeSlot newTimeSlot) {
        return ResponseEntity.ok(bookingService.rescheduleBooking(id, newTimeSlot));
    }

    // Check if a time slot is already taken
    @GetMapping("/check-slot")
    public ResponseEntity<Boolean> isTimeSlotTaken(
            @RequestParam Long doctorId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(bookingService.isTimeSlotTaken(doctorId, start, end));
    }
}
