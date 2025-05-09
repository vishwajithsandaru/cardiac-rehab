package com.dexter.fyp.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // LVEF (%) - Left Ventricular Ejection Fraction
    @Min(value = 0, message = "LVEF cannot be negative")
    @Max(value = 100, message = "LVEF cannot exceed 100%")
    private Integer lvef;
    
    // NYHA Class - New York Heart Association Functional Classification
    private String nyhaClass; // "Class I", "Class II", "Class III", "Class IV"
    
    // Blood Pressure
    @Min(value = 50, message = "Systolic BP seems too low")
    @Max(value = 250, message = "Systolic BP seems too high")
    private Integer hypertensionUpper; // Systolic
    
    @Min(value = 30, message = "Diastolic BP seems too low")
    @Max(value = 150, message = "Diastolic BP seems too high")
    private Integer hypertensionLower; // Diastolic
    
    // LDL Cholesterol
    @Min(value = 0, message = "Cholesterol level cannot be negative")
    private Double cholesterolLevel;
    
    // Oxygen Saturation
    @Min(value = 50, message = "Oxygen saturation seems too low")
    @Max(value = 100, message = "Oxygen saturation cannot exceed 100%")
    private Integer oxygenSaturation;
    
    // Smoking Status
    private String smokingHistory; // "Never Smoked", "Former Smoker", "Current Smoker"
    
    // Diabetes (HbA1c)
    @Min(value = 0, message = "HbA1c cannot be negative")
    @Max(value = 20, message = "HbA1c seems too high")
    private Double diabetes; // HbA1c percentage
    
    // Exercise Tolerance (METs)
    @Min(value = 0, message = "METs cannot be negative")
    private Double exerciseTolerance;
    
    // Cardiac Event Type
    private String cardiacEventType; // "Myocardial Infarction", "CABG", "Heart Attack", "Angioplasty", "Valve Surgery", etc.
    
    // Date of Last Cardiac Event
    @Past(message = "Date of cardiac event must be in the past")
    private LocalDate dateOfLastCardiacEvent;
    
    // Alcohol Intake
    private String alcoholIntake; // "None", "Occasional", "Moderate", "Heavy"
    
    // Physical Activity Before Event
    private String physicalActivityBeforeEvent; // "Light", "Moderate", "Active"
    
    // List of cardiac errors/symptoms
    @ElementCollection
    private List<String> cardiacError;
}
