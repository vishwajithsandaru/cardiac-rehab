package com.dexter.fyp.backend.service;

import com.dexter.fyp.backend.dto.BookingAvailabilitiesResponse;
import com.dexter.fyp.backend.dto.BookingDetailsDto;
import com.dexter.fyp.backend.dto.CreateBookingRequest;
import com.dexter.fyp.backend.dto.GeneralResponse;
import com.dexter.fyp.backend.dto.PatientDto;
import com.dexter.fyp.backend.entity.Booking;
import com.dexter.fyp.backend.entity.Doctor;
import com.dexter.fyp.backend.entity.DoctorAvailability;
import com.dexter.fyp.backend.entity.User;
import com.dexter.fyp.backend.enums.Status;
import com.dexter.fyp.backend.repository.BookingRepository;
import com.dexter.fyp.backend.repository.DoctorAvailabilityRepository;
import com.dexter.fyp.backend.repository.DoctorRepository;
import com.dexter.fyp.backend.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;

    private static final int STATUS_CANCELLED = -1;
    private static final int STATUS_CONFIRMED = 1;

    private static final int MAX_BOOKING_DURATION_HOURS = 2;

    public BookingService(BookingRepository bookingRepository, DoctorRepository doctorRepository, UserRepository userRepository, DoctorAvailabilityRepository doctorAvailabilityRepository) {
        this.bookingRepository = bookingRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.doctorAvailabilityRepository = doctorAvailabilityRepository;
    }

    public GeneralResponse createBooking(CreateBookingRequest request) throws Exception{

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new Exception("Doctor not found"));

        User patient = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        Optional<DoctorAvailability> doctorAvailability = doctorAvailabilityRepository.findById(request.getDoctorAvailabilityId());

        if(!doctorAvailability.isPresent()){
            throw new RuntimeException("Doctor availability not found");
        }

        Optional<Booking> existingBooking = bookingRepository.findByDoctorAvailabilityAndBookingDateAndStatus(doctorAvailability.get(), request.getDate(), Status.CONFIRMED);
        if(existingBooking.isPresent()){
            throw new Exception("Booking already exists for this date");
        }else{
            DayOfWeek dayOfWeek = doctorAvailability.get().getDayOfWeek();
            if(dayOfWeek != request.getDate().getDayOfWeek()){
                throw new Exception("Booking date does not match doctor's availability");
            }

            Booking booking = new Booking();
            booking.setDoctor(doctor);
            booking.setUser(patient);
            booking.setDoctorAvailability(doctorAvailability.get());
            booking.setBookingDate(request.getDate());
            booking.setRemark(request.getRemark());
            booking.setStatus(Status.CONFIRMED);

            bookingRepository.save(booking);

            GeneralResponse response = new GeneralResponse();
            response.setStatus(Status.SUCCESS);
            response.setMessage("Booking created successfully");

            return response;
        }
    }

    public BookingAvailabilitiesResponse getDoctorBookingAvailabilites(Long doctorId, LocalDate startDate, LocalDate endDate) throws Exception {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new Exception("Doctor not found"));

        List<BookingAvailabilitiesResponse.Slot> slots = new ArrayList<>();

        LocalDate refDate = startDate;

        while(!refDate.isAfter(endDate)){

            BookingAvailabilitiesResponse.Slot slot = new BookingAvailabilitiesResponse.Slot();
            DayOfWeek dayOfWeek = refDate.getDayOfWeek();

            slot.setDate(refDate);
            slot.setDayOfWeek(dayOfWeek);

            List<BookingAvailabilitiesResponse.Availability> subAvailabilities = new ArrayList<>();

            List<Booking> bookings = bookingRepository.findByDoctorAndBookingDate(doctor, refDate);
            List<DoctorAvailability> availabilities = doctorAvailabilityRepository.findByDoctorAndDayOfWeek(doctor, dayOfWeek);

            for(DoctorAvailability availability : availabilities){
                if(isBookingAvailable(availability, bookings)){

                    BookingAvailabilitiesResponse.Availability availabilityResponse = new BookingAvailabilitiesResponse.Availability();
                    availabilityResponse.setAvailabilityId(availability.getId());
                    availabilityResponse.setStartTime(availability.getStartTime());
                    availabilityResponse.setEndTime(availability.getEndTime());

                    subAvailabilities.add(availabilityResponse);

                }
            }

            if(subAvailabilities.size() > 0){

                slot.setAvailabilities(subAvailabilities);
                slots.add(slot);

            }

            refDate = refDate.plusDays(1);
            
        }

        return new BookingAvailabilitiesResponse(slots);
    }

    private boolean isBookingAvailable(DoctorAvailability availability, List<Booking> bookings){
        for(Booking booking : bookings){
            if(booking.getDoctorAvailability().getId() == availability.getId()){
                return false;
            }
        }
        return true;
    }

    public Page<PatientDto> getDoctorBookings(Long doctorId, int page, int size) throws Exception{

        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        if(!doctor.isPresent()){
            throw new Exception("Doctor not found for the given id.");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        
        Page<PatientDto> patientDtos = bookingRepository.findByDoctorOrderByBookingDateDesc(doctor.get(), pageable).map(this::convertBookingToPatientDto);

        return patientDtos;

    }

    public Page<BookingDetailsDto> getUserBookingsDetails(Long userId, int page, int size) throws Exception{

        Optional<User> user = userRepository.findById(userId);
        if(!user.isPresent()){
            throw new Exception("User not found for the given id.");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        
        Page<BookingDetailsDto> bookingDetailsDtos = bookingRepository.findByUserOrderByBookingDateDesc(user.get(), pageable).map(this::convertBookingToBookingDetailsDto);

        return bookingDetailsDtos;

    }

    private PatientDto convertBookingToPatientDto(Booking booking){

        User user = booking.getUser();
        PatientDto patientDto = new PatientDto();
        patientDto.setUserId(user.getId());
        patientDto.setFirstName(user.getFirstName());
        patientDto.setLastName(user.getLastName());
        patientDto.setPhone(user.getPhoneNumber());
        patientDto.setEmail(user.getEmail());
        patientDto.setProfilePhotoUrl(user.getProfilePhotoUrl());

        return patientDto;

    }

    private BookingDetailsDto convertBookingToBookingDetailsDto(Booking booking){

        Doctor doctor = booking.getDoctor();
        BookingDetailsDto bookingDetailsDto = new BookingDetailsDto();
        bookingDetailsDto.setDoctorId(doctor.getId());
        bookingDetailsDto.setDoctorFirstName(doctor.getFirstName());
        bookingDetailsDto.setDoctorLastName(doctor.getLastName());
        bookingDetailsDto.setDoctorEmail(doctor.getEmail());
        bookingDetailsDto.setDoctorPhone(doctor.getPhone());
        bookingDetailsDto.setDoctorProfilePhotoUrl(doctor.getProfilePhotoUrl());
        bookingDetailsDto.setDate(booking.getBookingDate());
        bookingDetailsDto.setStartTime(booking.getDoctorAvailability().getStartTime());
        bookingDetailsDto.setEndTime(booking.getDoctorAvailability().getEndTime());
        bookingDetailsDto.setRemarks(booking.getRemark());
        bookingDetailsDto.setStatus(booking.getStatus());

        return bookingDetailsDto;

    }


}
