package com.dexter.fyp.backend.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingAvailabilitiesResponse {

    List<Slot> slots;
    
    @Data
    public static class Slot{
        private LocalDate date;
        private DayOfWeek dayOfWeek;
        private List<Availability> availabilities;
    }

    @Data
    public static class Availability{
        private Long availabilityId;
        private LocalTime startTime;
        private LocalTime endTime;
    }

}
