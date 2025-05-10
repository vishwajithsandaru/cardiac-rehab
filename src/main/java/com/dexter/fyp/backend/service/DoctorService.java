package com.dexter.fyp.backend.service;


import org.hibernate.boot.registry.classloading.spi.ClassLoaderService.Work;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.dexter.fyp.backend.dto.DocUpdateUserPlan;
import com.dexter.fyp.backend.dto.GeneralResponse;
import com.dexter.fyp.backend.entity.CustomPlanWorkout;
import com.dexter.fyp.backend.entity.CustomPlanWorkout.PlanWorkoutID;
import com.dexter.fyp.backend.entity.Doctor;
import com.dexter.fyp.backend.entity.DoctorAvailability;
import com.dexter.fyp.backend.entity.User;
import com.dexter.fyp.backend.entity.UserPlan;
import com.dexter.fyp.backend.entity.Workout;
import com.dexter.fyp.backend.enums.Status;
import com.dexter.fyp.backend.repository.CustomPlanWorkoutRepository;
import com.dexter.fyp.backend.repository.DoctorRepository;
import com.dexter.fyp.backend.repository.UserPlanRepository;
import com.dexter.fyp.backend.repository.UserRepository;
import com.dexter.fyp.backend.repository.WorkoutRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.print.Doc;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final UserPlanRepository userPlanRepository;
    private final CustomPlanWorkoutRepository customPlanWorkoutRepository;
    private final WorkoutRepository workoutRepository;

    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository, UserPlanRepository userPlanRepository, CustomPlanWorkoutRepository customPlanWorkoutRepository, WorkoutRepository workoutRepository) {
        this.customPlanWorkoutRepository = customPlanWorkoutRepository;
        this.userPlanRepository = userPlanRepository;
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.workoutRepository = workoutRepository;
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    // READ - All
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // FILTER - Specialization
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationIgnoreCase(specialization);
    }

    // SEARCH - Name
    public List<Doctor> searchDoctorsByName(String keyword) {
        return doctorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
    }

    // ========== Private Helpers ==========

    private void validateDoctor(Doctor doctor) {
        if (doctor.getFirstName() == null || doctor.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (doctor.getLastName() == null || doctor.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (doctor.getEmail() == null || doctor.getEmail().isBlank() || !doctor.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("A valid email address is required");
        }
        if (doctor.getSpecialization() == null || doctor.getSpecialization().isBlank()) {
            throw new IllegalArgumentException("Specialization is required");
        }
    }

    public void validateAvailability(List<DoctorAvailability> availabilities) {
        for (DoctorAvailability a : availabilities) {
            if (a.getStartTime() == null || a.getEndTime() == null) {
                throw new IllegalArgumentException("Start time and end time must be provided for each availability slot");
            }
            if (!a.getStartTime().isBefore(a.getEndTime())) {
                throw new IllegalArgumentException("Start time must be before end time for availability slot");
            }
        }

        for (int i = 0; i < availabilities.size(); i++) {
            for (int j = i + 1; j < availabilities.size(); j++) {
                DoctorAvailability a = availabilities.get(i);
                DoctorAvailability b = availabilities.get(j);
                if (a.getDayOfWeek() == b.getDayOfWeek() && a.getStartTime().isBefore(b.getEndTime()) && b.getStartTime().isBefore(a.getEndTime())) {
                    throw new IllegalArgumentException("Availability time slots overlap");
                }
            }
        }
    }

    @Transactional
    public GeneralResponse updateCurrentUserPlan(Long id, DocUpdateUserPlan docUpdateUserPlan) throws Exception {

        Optional<Doctor> doctorOptional = doctorRepository.findById(id);
        if (doctorOptional.isPresent()) {
            Doctor doctor = doctorOptional.get();
            
            Optional<User> user = userRepository.findById(docUpdateUserPlan.getUserId());
            if (user.isPresent()) {

                if(docUpdateUserPlan.getWorkoutIds() == null || docUpdateUserPlan.getWorkoutIds().isEmpty()){
                    throw new Exception("Workout IDs cannot be null or empty");
                }
                
                UserPlan userPlan = userPlanRepository.findByUserAndIsCurrentTrue(user.get());
                if(userPlan != null){
                    userPlan.setCurrent(false);
                    userPlanRepository.save(userPlan);

                    UserPlan newUserPlan = new UserPlan();

                    String customPlanId = UUID.randomUUID().toString();

                    newUserPlan.setCustomPlanId(customPlanId);
                    newUserPlan.setUser(user.get());
                    newUserPlan.setDoctorNotes(docUpdateUserPlan.getDoctorNote());
                    newUserPlan.setUpdatedBy(doctor);
                    newUserPlan.setDefaultPlan(userPlan.getDefaultPlan());

                    if(docUpdateUserPlan.getPlanName() != null && !docUpdateUserPlan.getPlanName().isEmpty()){
                        newUserPlan.setName(docUpdateUserPlan.getPlanName());
                    } else {
                        newUserPlan.setName(userPlan.getName());
                    }

                    newUserPlan.setCreatedDateTime(LocalDateTime.now());
                    newUserPlan.setCurrent(true);
                    userPlanRepository.save(newUserPlan);

                    List<CustomPlanWorkout> customPlanWorkouts = new ArrayList<>();

                    for(Long l : docUpdateUserPlan.getWorkoutIds()){
                        Optional<Workout> workoutOptional = workoutRepository.findById(l);
                        if(workoutOptional.isPresent()){
                            CustomPlanWorkout customPlanWorkout = new CustomPlanWorkout();
                            PlanWorkoutID pwi = new PlanWorkoutID(newUserPlan.getUserPlanId(), workoutOptional.get().getId());
                            customPlanWorkout.setId(pwi);
                            customPlanWorkout.setWorkout(workoutOptional.get());
                            customPlanWorkout.setUserPlan(newUserPlan);
                            customPlanWorkouts.add(customPlanWorkout);
                        }
                    }

                    customPlanWorkoutRepository.saveAll(customPlanWorkouts);

                    GeneralResponse response = new GeneralResponse();
                    response.setStatus(Status.SUCCESS);
                    response.setMessage("User plan updated successfully");
                    return response;

                } else {
                    throw new Exception("User plan not found");
                }

            } else {
                throw new Exception("User not found");
            }

        }else{
            throw new Exception("Doctor not found");
        }

        
    }


}
