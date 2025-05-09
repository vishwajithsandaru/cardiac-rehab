package com.dexter.fyp.backend.service;

import com.dexter.fyp.backend.entity.Booking;
import com.dexter.fyp.backend.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    private static final int STATUS_CANCELLED = -1;
    private static final int STATUS_CONFIRMED = 1;

    private static final int MAX_BOOKING_DURATION_HOURS = 2;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // CREATE
    public Booking createBooking(Booking booking) {
        validateBookingInputs(booking);

        if (isTimeSlotInPast(booking.getTimeSlot().getStartTime())) {
            throw new IllegalArgumentException("Cannot book a time slot in the past");
        }

        if (isTimeSlotTaken(booking.getDoctor().getId(), booking.getTimeSlot().getStartTime(), booking.getTimeSlot().getEndTime())) {
            throw new IllegalArgumentException("Time slot already booked for this doctor");
        }

        if (isUserOverlapping(booking.getUser().getId(), booking.getTimeSlot().getStartTime(), booking.getTimeSlot().getEndTime())) {
            throw new IllegalArgumentException("User already has a booking in this time slot");
        }

        if (isDuplicateBooking(booking)) {
            throw new IllegalArgumentException("Duplicate booking detected for the same doctor, user, and time slot");
        }

        return bookingRepository.save(booking);
    }

    // READ
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByDoctorId(Long doctorId) {
        return bookingRepository.findByDoctorId(doctorId);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsByDate(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByTimeSlotStartTimeBetween(start, end);
    }

    public List<Booking> getUnpaidBookingsByUserId(Long userId) {
        return bookingRepository.findByUserIdAndIsPaidFalse(userId);
    }

    public List<Booking> getBookingsByStatus(int status) {
        return bookingRepository.findByStatus(status);
    }

    // UPDATE
    public Booking updateBooking(Long bookingId, Booking updatedBooking) {
        validateBookingInputs(updatedBooking);

        if (isTimeSlotInPast(updatedBooking.getTimeSlot().getStartTime())) {
            throw new IllegalArgumentException("Cannot update to a time slot in the past");
        }

        return bookingRepository.findById(bookingId).map(existing -> {
            boolean timeChanged = !existing.getTimeSlot().equals(updatedBooking.getTimeSlot());
            if (timeChanged && isTimeSlotTakenExcept(updatedBooking.getDoctor().getId(),
                    updatedBooking.getTimeSlot().getStartTime(),
                    updatedBooking.getTimeSlot().getEndTime(),
                    bookingId)) {
                throw new IllegalArgumentException("Updated time slot is already booked");
            }

            if (timeChanged && isUserOverlappingExcept(updatedBooking.getUser().getId(),
                    updatedBooking.getTimeSlot().getStartTime(),
                    updatedBooking.getTimeSlot().getEndTime(),
                    bookingId)) {
                throw new IllegalArgumentException("User already has a booking in this time slot");
            }

            existing.setDoctor(updatedBooking.getDoctor());
            existing.setUser(updatedBooking.getUser());
            existing.setTimeSlot(updatedBooking.getTimeSlot());
            existing.setRemark(updatedBooking.getRemark());
            existing.setPrice(updatedBooking.getPrice());
            existing.setStatus(updatedBooking.getStatus());
            existing.setPaid(updatedBooking.isPaid());

            return bookingRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    // DELETE
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found");
        }
        bookingRepository.deleteById(id);
    }

    // CANCEL
    public Booking cancelBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).map(existing -> {
            existing.setStatus(STATUS_CANCELLED);
            return bookingRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    // RESCHEDULE
    public Booking rescheduleBooking(Long bookingId, Booking.TimeSlot newTimeSlot) {
        if (isTimeSlotInPast(newTimeSlot.getStartTime())) {
            throw new IllegalArgumentException("Cannot reschedule to a past time slot");
        }

        return bookingRepository.findById(bookingId).map(existing -> {
            if (isTimeSlotTakenExcept(existing.getDoctor().getId(),
                    newTimeSlot.getStartTime(),
                    newTimeSlot.getEndTime(),
                    bookingId)) {
                throw new IllegalArgumentException("New time slot already booked");
            }

            if (isUserOverlappingExcept(existing.getUser().getId(),
                    newTimeSlot.getStartTime(),
                    newTimeSlot.getEndTime(),
                    bookingId)) {
                throw new IllegalArgumentException("User already has a booking in this time slot");
            }

            existing.setTimeSlot(newTimeSlot);
            return bookingRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    // Check if timeslot is already taken
    public boolean isTimeSlotTaken(Long doctorId, LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByDoctorId(doctorId).stream()
                .anyMatch(b -> b.getStatus() == STATUS_CONFIRMED &&
                        b.getTimeSlot().getStartTime().isBefore(end) &&
                        b.getTimeSlot().getEndTime().isAfter(start));
    }

    // Exclude booking ID when checking for time collisions
    public boolean isTimeSlotTakenExcept(Long doctorId, LocalDateTime start, LocalDateTime end, Long excludeBookingId) {
        return bookingRepository.findByDoctorId(doctorId).stream()
                .anyMatch(b -> !b.getId().equals(excludeBookingId) &&
                        b.getStatus() == STATUS_CONFIRMED &&
                        b.getTimeSlot().getStartTime().isBefore(end) &&
                        b.getTimeSlot().getEndTime().isAfter(start));
    }

    // Check if user has an overlapping booking
    public boolean isUserOverlapping(Long userId, LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByUserId(userId).stream()
                .anyMatch(b -> b.getStatus() == STATUS_CONFIRMED &&
                        b.getTimeSlot().getStartTime().isBefore(end) &&
                        b.getTimeSlot().getEndTime().isAfter(start));
    }

    // Same as above but excludes a booking by ID (for update/reschedule)
    public boolean isUserOverlappingExcept(Long userId, LocalDateTime start, LocalDateTime end, Long excludeBookingId) {
        return bookingRepository.findByUserId(userId).stream()
                .anyMatch(b -> !b.getId().equals(excludeBookingId) &&
                        b.getStatus() == STATUS_CONFIRMED &&
                        b.getTimeSlot().getStartTime().isBefore(end) &&
                        b.getTimeSlot().getEndTime().isAfter(start));
    }

    // Check if booking is a duplicate
    private boolean isDuplicateBooking(Booking booking) {
        return bookingRepository.findByDoctorId(booking.getDoctor().getId()).stream()
                .anyMatch(b -> b.getStatus() == STATUS_CONFIRMED &&
                        b.getUser().getId().equals(booking.getUser().getId()) &&
                        b.getTimeSlot().getStartTime().equals(booking.getTimeSlot().getStartTime()) &&
                        b.getTimeSlot().getEndTime().equals(booking.getTimeSlot().getEndTime()));
    }

    // Check if time is in the past
    private boolean isTimeSlotInPast(LocalDateTime startTime) {
        return startTime.isBefore(LocalDateTime.now());
    }

    // Validate input fields and time logic
    private void validateBookingInputs(Booking booking) {
        if (booking.getDoctor() == null || booking.getDoctor().getId() == null) {
            throw new IllegalArgumentException("Doctor is required");
        }
        if (booking.getUser() == null || booking.getUser().getId() == null) {
            throw new IllegalArgumentException("User is required");
        }
        if (booking.getTimeSlot() == null ||
                booking.getTimeSlot().getStartTime() == null ||
                booking.getTimeSlot().getEndTime() == null) {
            throw new IllegalArgumentException("Valid time slot is required");
        }

        LocalDateTime start = booking.getTimeSlot().getStartTime();
        LocalDateTime end = booking.getTimeSlot().getEndTime();

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        Duration duration = Duration.between(start, end);
        if (duration.toHours() > MAX_BOOKING_DURATION_HOURS) {
            throw new IllegalArgumentException("Booking duration exceeds allowed limit of " + MAX_BOOKING_DURATION_HOURS + " hours");
        }
    }
}
