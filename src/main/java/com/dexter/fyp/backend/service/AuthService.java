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
import com.dexter.fyp.backend.entity.AppUser;
import com.dexter.fyp.backend.entity.Doctor;
import com.dexter.fyp.backend.entity.User;
import com.dexter.fyp.backend.entity.DoctorAvailability;
import com.dexter.fyp.backend.entity.MedicalRecords;
import com.dexter.fyp.backend.enums.Role;
import com.dexter.fyp.backend.repository.AppUserRepository;
import com.dexter.fyp.backend.util.JWTUtil;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder encoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder encoder, JWTUtil jwtUtil,
            AuthenticationManager authenticationManager, UserService userService) {
        this.appUserRepository = appUserRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

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

        List<DoctorAvailabilityDto> availabilites = request.getAvailabilities();
        if(availabilites == null || availabilites.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doctor availability is required");
        }

        List<DoctorAvailability> doctorAvailabilities = availabilites.stream()
                .map(availabilityDto -> {
                    DoctorAvailability availability = new DoctorAvailability();
                    availability.setStartTime(availabilityDto.getStartTime());
                    availability.setEndTime(availabilityDto.getEndTime());
                    availability.setDoctor(doctor);
                    return availability;
                })
                .toList();

        doctor.setAvailabilities(doctorAvailabilities);

        AppUser appUser = doctor;

        appUserRepository.save(appUser);
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(appUser.getEmail(), appUser.getPassword(), List.of());

        String jwtToken = jwtUtil.generateToken(userDetails);
        

        return new AuthResponse(jwtToken);

    }

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
    
}
