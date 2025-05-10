package com.dexter.fyp.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequestPatientDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    @NotBlank(message = "Age is required")
    @Pattern(regexp = "^[0-9]{1,3}$", message = "Age must be a number")
    private Integer age;

    @NotBlank(message = "Height is required")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Height must be a valid number")
    private Float height;

    @NotBlank(message = "Weight is required")
    @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Weight must be a valid number")
    private Float weight;

    @NotNull(message = "Medical records are required")
    private MedicalRecordsDto medicalRecords;
}
