package com.dexter.fyp.backend.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.Data;

@Data
public class DoctorAvailabilityDto {
 
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    
}
