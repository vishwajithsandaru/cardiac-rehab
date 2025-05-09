package com.dexter.fyp.backend.dto;

import java.util.List;

import lombok.Data;

@Data
public class SignUpRequestDoctorDto {
    
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String specialization;
    private String emergencyContact;

    private List<DoctorAvailabilityDto> availabilities;

}
