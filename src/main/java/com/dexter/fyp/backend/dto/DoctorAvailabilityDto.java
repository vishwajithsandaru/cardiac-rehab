package com.dexter.fyp.backend.dto;

import java.time.LocalTime;

import lombok.Data;

@Data
public class DoctorAvailabilityDto {
 
    private String dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    
}
