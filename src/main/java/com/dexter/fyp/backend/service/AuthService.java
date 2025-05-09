package com.dexter.fyp.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.dexter.fyp.backend.dto.AuthResponse;
import com.dexter.fyp.backend.dto.DoctorAvailabilityDto;
import com.dexter.fyp.backend.dto.LoginRequest;
import com.dexter.fyp.backend.dto.SignUpRequestDoctorDto;
import com.dexter.fyp.backend.dto.SignUpRequestPatientDto;
import com.dexter.fyp.backend.dto.UserDetailsDto;
import com.dexter.fyp.backend.entity.AppUser;
import com.dexter.fyp.backend.entity.Doctor;
import com.dexter.fyp.backend.entity.User;
import com.dexter.fyp.backend.entity.DoctorAvailability;
import com.dexter.fyp.backend.entity.MedicalRecords;
import com.dexter.fyp.backend.enums.Role;
import com.dexter.fyp.backend.repository.AppUserRepository;
import com.dexter.fyp.backend.util.JWTUtil;

import jakarta.transaction.Transactional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder encoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final DoctorService doctorService;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder encoder, JWTUtil jwtUtil,
            AuthenticationManager authenticationManager, UserService userService, DoctorService doctorService) {
        this.doctorService = doctorService;
        this.appUserRepository = appUserRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Transactional
    public AuthResponse doctorSignUp(SignUpRequestDoctorDto request){

        if(appUserRepository.existsByEmail(request.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }


        Doctor doctor = new Doctor();
        doctor.setEmail(request.getEmail());
        doctor.setPassword(encoder.encode(request.getPassword()));
        doctor.setRole(Role.DOCTOR);
        doctor.setFirstName(request.getFirstName());
        doctor.setLastName(request.getLastName());
        doctor.setSpecialization(request.getSpecialization());
        doctor.setDateOfBirth(request.getDateOfBirth());
        doctor.setGender(request.getGender());
        doctor.setProfilePhotoUrl(request.getProfilePhotoUrl());

        List<DoctorAvailabilityDto> availabilites = request.getAvailabilities();
        if(availabilites == null || availabilites.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor availability is required");
        }

        List<DoctorAvailability> doctorAvailabilities = availabilites.stream()
                .map(availabilityDto -> {
                    DoctorAvailability availability = new DoctorAvailability();
                    availability.setStartTime(availabilityDto.getStartTime());
                    availability.setEndTime(availabilityDto.getEndTime());
                    availability.setDayOfWeek(availabilityDto.getDayOfWeek());
                    availability.setDoctor(doctor);
                    return availability;
                })
                .toList();
        
        doctorService.validateAvailability(doctorAvailabilities);
        doctor.setAvailabilities(doctorAvailabilities);

        AppUser appUser = doctor;

        appUserRepository.save(appUser);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(appUser.getEmail(), appUser.getPassword(), List.of());

        String jwtToken = jwtUtil.generateToken(userDetails);
        

        return new AuthResponse(jwtToken);

    }

    @Transactional
    public AuthResponse patientSignUp(SignUpRequestPatientDto request){

        if(appUserRepository.existsByEmail(request.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }



        User patientUser = new User();
        patientUser.setEmail(request.getEmail());
        patientUser.setPassword(encoder.encode(request.getPassword()));
        patientUser.setRole(Role.PATIENT);
        patientUser.setFirstName(request.getFirstName());
        patientUser.setLastName(request.getLastName());
        patientUser.setAge(request.getAge());
        patientUser.setHeight(request.getHeight());
        patientUser.setWeight(request.getWeight());
        patientUser.setDateOfBirth(request.getDateOfBirth());
        patientUser.setGender(request.getGender());
        patientUser.setProfilePhotoUrl(request.getProfilePhotoUrl());


        
        if(request.getMedicalRecords() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Medical records are required");
        }

        MedicalRecords medicalRecords = new MedicalRecords();

        medicalRecords.setCholesterolLevel(request.getMedicalRecords().getCholesterolLevel());
        medicalRecords.setDiabetes(request.getMedicalRecords().getDiabetes());
        medicalRecords.setAlcoholIntake(request.getMedicalRecords().getAlcoholIntake());
        medicalRecords.setCardiacError(request.getMedicalRecords().getCardiacError());
        medicalRecords.setCardiacEventType(request.getMedicalRecords().getCardiacEventType());
        medicalRecords.setDateOfLastCardiacEvent(request.getMedicalRecords().getDateOfLastCardiacEvent());
        medicalRecords.setExerciseTolerance(request.getMedicalRecords().getExerciseTolerance());
        medicalRecords.setHypertensionLower(request.getMedicalRecords().getHypertensionLower());
        medicalRecords.setHypertensionUpper(request.getMedicalRecords().getHypertensionUpper());
        medicalRecords.setLvef(request.getMedicalRecords().getLvef());
        medicalRecords.setNyhaClass(request.getMedicalRecords().getNyhaClass());
        medicalRecords.setOxygenSaturation(request.getMedicalRecords().getOxygenSaturation());
        medicalRecords.setPhysicalActivityBeforeEvent(request.getMedicalRecords().getPhysicalActivityBeforeEvent());
        medicalRecords.setSmokingHistory(request.getMedicalRecords().getSmokingHistory());

        patientUser.setMedicalRecords(medicalRecords);

        AppUser appUser = patientUser;

        appUserRepository.save(appUser);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(appUser.getEmail(), appUser.getPassword(), List.of());
        String jwtToken = jwtUtil.generateToken(userDetails);

        userService.assignPlanToClient(patientUser);
        
        return new AuthResponse(jwtToken);

    }

    public AuthResponse login(LoginRequest request) {
       
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        String jwtToken = jwtUtil.generateToken((UserDetails) auth.getPrincipal());
        return new AuthResponse(jwtToken);

    }

    public UserDetailsDto getUserDetails(Authentication authentication){

        String email = authentication.getName();
        AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserDetailsDto userDetailsDto = new UserDetailsDto();
        
        if(appUser instanceof User user){
            userDetailsDto.setUserId(user.getId());
            userDetailsDto.setFirstName(user.getFirstName());
            userDetailsDto.setLastName(user.getLastName());
            userDetailsDto.setEmail(user.getEmail());
            userDetailsDto.setRole(appUser.getRole());
            userDetailsDto.setProfilePhotoUrl(user.getProfilePhotoUrl());
        }else{

            Doctor doctor = (Doctor) appUser;

            userDetailsDto.setUserId(doctor.getId());
            userDetailsDto.setFirstName(doctor.getFirstName());
            userDetailsDto.setLastName(doctor.getLastName());
            userDetailsDto.setEmail(doctor.getEmail());
            userDetailsDto.setRole(doctor.getRole());
            userDetailsDto.setProfilePhotoUrl(doctor.getProfilePhotoUrl());
        }

        return userDetailsDto;
        

    }
    
}
