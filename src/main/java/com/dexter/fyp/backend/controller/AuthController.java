package com.dexter.fyp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dexter.fyp.backend.dto.AuthResponse;
import com.dexter.fyp.backend.dto.LoginRequest;
import com.dexter.fyp.backend.dto.SignUpRequestDoctorDto;
import com.dexter.fyp.backend.dto.SignUpRequestPatientDto;
import com.dexter.fyp.backend.dto.UserDetailsDto;
import com.dexter.fyp.backend.service.AuthService;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/doctor/signup")
    public ResponseEntity<AuthResponse> signupDoctor(@RequestBody SignUpRequestDoctorDto request) {
        return ResponseEntity.ok(authService.doctorSignUp(request));
    }

    @PostMapping("/patient/signup")
    public ResponseEntity<AuthResponse> signupPatient(@RequestBody SignUpRequestPatientDto request) {
        return ResponseEntity.ok(authService.patientSignUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDetailsDto> login(Authentication authentication) {
        return ResponseEntity.ok(authService.getUserDetails(authentication));
    }


}
