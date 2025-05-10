package com.dexter.fyp.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.dexter.fyp.backend.enums.Status;

import lombok.Data;

@Data
public class BookingDetailsDto {

    private Long doctorId;
    private String doctorFirstName;
    private String doctorLastName;
    private String doctorEmail;
    private String doctorPhone;
    private String doctorProfilePhotoUrl;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String remarks;
    private Status status; 
    
}
