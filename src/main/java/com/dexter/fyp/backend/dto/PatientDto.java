package com.dexter.fyp.backend.dto;

import lombok.Data;

@Data
public class PatientDto {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String profilePhotoUrl;
    
}
