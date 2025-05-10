package com.dexter.fyp.backend.entity;

import java.time.LocalDate;

import com.dexter.fyp.backend.enums.Gender;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class User extends AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;

    @Min(value = 1, message = "Age must be greater than 0")
    @Max(value = 120, message = "Age seems unrealistic")
    private Integer age;

    @Positive(message = "Height must be positive")
    private Float height;

    @Positive(message = "Weight must be positive")
    private Float weight;

    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String profilePhotoUrl;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "medical_records_id")
    private MedicalRecords medicalRecords;

}
