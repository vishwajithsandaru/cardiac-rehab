package com.dexter.fyp.backend.dto;

import java.time.LocalDate;

import com.dexter.fyp.backend.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserUpdateRequest {

    
    @Email(message = "Invalid email format")
    private String email;

    private String firstName;

    private String lastName;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    @Pattern(regexp = "^[0-9]{1,3}$", message = "Age must be a number")
    private Integer age;

    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Height must be a valid number")
    private Float height;

    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Weight must be a valid number")
    private Float weight;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String profilePhotoUrl;

    private MedicalRecordsDto medicalRecords;
    
}
