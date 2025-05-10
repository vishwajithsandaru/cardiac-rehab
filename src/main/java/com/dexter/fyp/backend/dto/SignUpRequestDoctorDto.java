package com.dexter.fyp.backend.dto;

import java.time.LocalDate;
import java.util.List;

import com.dexter.fyp.backend.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequestDoctorDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotBlank(message = "Emergency contact is required")
    @Pattern(regexp = "^[0-9+\\-() ]+$", message = "Invalid emergency contact format")
    private String emergencyContact;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String profilePhotoUrl;

    @NotNull(message = "Availability list is required")
    @Size(min = 1, message = "At least one availability entry is required")
    private List<@NotNull DoctorAvailabilityDto> availabilities;

}